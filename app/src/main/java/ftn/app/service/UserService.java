package ftn.app.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import ftn.app.dto.LoginDTO;
import ftn.app.model.Confirmation;
import ftn.app.model.Provider;
import ftn.app.model.User;
import ftn.app.model.UserPastPasswords;
import ftn.app.model.enums.EventType;
import ftn.app.repository.ConfirmationRepository;
import ftn.app.repository.PastPasswordsRepository;
import ftn.app.repository.UserRepository;
import ftn.app.service.interfaces.IUserService;
import ftn.app.util.LoggingUtil;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final MessageSource messageSource;

    private final PastPasswordsRepository pastPasswordsRepository;
    private final ConfirmationRepository confirmationRepository;

    public UserService(UserRepository userRepository,
                       MailService mailService,
                       MessageSource messageSource,
                       PastPasswordsRepository pastPasswordsRepository,
                       ConfirmationRepository confirmationRepository){
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.messageSource = messageSource;
        this.pastPasswordsRepository = pastPasswordsRepository;
        this.confirmationRepository = confirmationRepository;
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user.get();
        }
    }



    @Override
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.FOUND);
        }
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        user.setLastPasswordResetDate(new Date());
        UserPastPasswords pastPassword = new UserPastPasswords(user.getEmail(), user.getPassword());
        pastPasswordsRepository.save(pastPassword);
        return userRepository.save(user);
    }

    @Override
    public Boolean confirmation(User user, String confirmation) {
        user = userRepository.findByEmail(user.getEmail()).get();
        List<Confirmation> existing = confirmationRepository.findAllByUserAndExpired(user, false);
        for (Confirmation c : existing) {
            if (c.getConfirmation().equals(confirmation)) {
                c.setExpired(true);
                confirmationRepository.save(c);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean registerConfirmation(User user, String confirmation) {
        user = userRepository.findByEmail(user.getEmail()).get();
        if(confirmation(user, confirmation)){
            user.setIsConfirmed(true);
            userRepository.save(user);
            return true;
        }
        else{
            throw new BadCredentialsException("user.invalidConfirmation");
        }
    }

    @Override
    public Boolean passwordConfirmation(User user, String confirmation) {
        String password = user.getPassword();
        user = userRepository.findByEmail(user.getEmail()).get();
        if(confirmation(user, confirmation)){
            List<UserPastPasswords> pastPasswords = pastPasswordsRepository.getUserPastPasswordsByEmail(user.getEmail()).get();
            for(UserPastPasswords pastPassword : pastPasswords) {
                if(passwordEncoder().matches(password,pastPassword.getPassword())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("password.hadAlready", null, Locale.getDefault()));
                }
            }
            user.setPassword(passwordEncoder().encode(password));
            user.setLastPasswordResetDate(new Date());
            UserPastPasswords pastPassword = new UserPastPasswords(user.getEmail(), user.getPassword());
            pastPasswordsRepository.save(pastPassword);
            userRepository.save(user);
            return true;
        }
        else{
            throw new BadCredentialsException("user.invalidConfirmation");
        }
    }

    @Override
    public void sendConfirmationEmail(User user) {
        user = userRepository.findByEmail(user.getEmail()).get();
        int confirmationString = (int) Math.floor(Math.random() * (99999 - 10000 + 1) + 10000);
        Confirmation confirmation = new Confirmation();
        confirmation.setConfirmation(Integer.toString(confirmationString));
        confirmation.setUser(user);
        confirmation.setExpired(false);
        confirmationRepository.save(confirmation);
        try {
            mailService.sendSimpleMessage(user.getEmail(), "Email confirmation", confirmation.getConfirmation());
            //Otkomentarisati za odbranu/test sendgrida. Zakomentarisano jer ima limit od 100 poruka dnevno, pa da ne predjemo limit dok testiramo
//            mailService.sendTextEmail(user.getEmail(), "Email confirmation", confirmation.getConfirmation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendConfirmationMessage(User user) {
        user = userRepository.findByEmail(user.getEmail()).get();
        int confirmationString = (int) Math.floor(Math.random() * (99999 - 10000 + 1) + 10000);
        Confirmation confirmation = new Confirmation();
        confirmation.setConfirmation(Integer.toString(confirmationString));
        confirmation.setUser(user);
        confirmation.setExpired(false);
        confirmationRepository.save(confirmation);
        sendWhatsappMessage(user.getPhoneNumber(), confirmation.getConfirmation());
    }

    @Override
    public Boolean isConfirmed(LoginDTO loginInfo) {
        Optional<User> user = userRepository.findByEmail(loginInfo.getEmail());
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
        }
        if(user.get().getIsConfirmed()){
            return true;
        }
        throw new BadCredentialsException("Bad credentials");
    }
    @Override
    public void sendWhatsappMessage(String number, String message){
        Twilio.init("AC559b6719c0f31fd677511078de1cab33","");
        Message.creator(new PhoneNumber("whatsapp:"+number),
                new PhoneNumber("whatsapp:+14155238886"), message).create();
    }

    @Override
    public void processOAuthPostLogin(String email) {
        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setProvider(Provider.GOOGLE);

            userRepository.save(user);
            sendConfirmationEmail(user);
        }
    }

    @Override
    public boolean ifEmailExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public User getUserFromEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", email));
        } else {
            return user.get();
        }
    }

}