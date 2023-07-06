package ftn.app.auth;

import ftn.app.model.enums.EventType;
import ftn.app.util.LoggingUtil;
import ftn.app.util.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private final TokenUtils tokenUtils;

	private final UserDetailsService userDetailsService;

	public TokenAuthenticationFilter(TokenUtils tokenHelper, UserDetailsService userDetailsService) {
		this.tokenUtils = tokenHelper;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String username = "";
		String authToken = tokenUtils.getToken(request);
		if (authToken != null) {
			try {
				username = tokenUtils.getUsernameFromToken(authToken);
				if (username != null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					if (tokenUtils.validateToken(authToken, userDetails)) {
						TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
						authentication.setToken(authToken);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
			catch (ExpiredJwtException ex) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired! Log in again!");
				if(username == null) username = "";
				LoggingUtil.LogEvent(username, EventType.FAIL, "attempted accessing " + request.getRequestURI() + " URL. Request denied due to expired token.");
				return;
			}
		}
		chain.doFilter(request, response);
	}
}