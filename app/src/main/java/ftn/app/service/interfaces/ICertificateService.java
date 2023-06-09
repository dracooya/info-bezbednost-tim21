package ftn.app.service.interfaces;

import ftn.app.dto.*;
import ftn.app.model.Certificate;
import ftn.app.model.CertificateRequest;
import ftn.app.model.User;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICertificateService {
    Certificate saveCertificate(CertificateRequestDTO requestDTO, User requester);
    boolean isValidCertificate(Certificate certificate, boolean isOverallValid);
    boolean isValidCertificate(MultipartFile file);
    List<CertificateDetailsDTO> getAllCertificates();
    List<CertificateDetailsDTO> getEligibleCertificatesForIssuing();
    boolean isValidCertificate(String serialNumber);
    Certificate withdraw(User user, String certificateSerialNumber, WithdrawingReasonDTO reason);
    ByteArrayResource getCertificate(String serialNumber, User requester);
    List<CertificateDetailsWithUserInfoDTO> getAllDetailedCertificates();
}
