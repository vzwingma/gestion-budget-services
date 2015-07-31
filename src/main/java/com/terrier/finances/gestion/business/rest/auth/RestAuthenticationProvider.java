/**
 * 
 */
package com.terrier.finances.gestion.business.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

/**
 * @author vzwingma
 *
 */
@Component
public class RestAuthenticationProvider implements AuthenticationProvider {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthenticationProvider.class);

	
	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String username = authentication.getName();
        String password = (String) authentication.getCredentials();
 
        LOGGER.info(" {}/{}", username, password);
 
        if (username == null) {
            throw new BadCredentialsException("Erreur d'authentification.");
        }
 
        if (!password.equals(password)) {
            throw new BadCredentialsException("Erreur d'authentification.");
        }
        return new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
