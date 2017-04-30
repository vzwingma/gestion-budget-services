package com.terrier.finances.gestion.business.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;

/**
 * Gestionnaire de sessions REST
 * @author vzwingma
 *
 */
@Component("userAuthProvider")
public class UserAuthProvider implements AuthenticationProvider {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthProvider.class);

	@Autowired
	private AuthenticationService authenticationService;
		


	/**
	 * Authentification REST
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		Utilisateur utilisateur = authenticationService.authenticate(username, password);
		if (utilisateur == null) {
			throw new BadCredentialsException("Erreur d'authentification.");
		}
		LOGGER.info("[SEC] AuthToken : [{}]", username);
		return new UsernamePasswordAuthenticationToken(utilisateur, password, AuthorityUtils.createAuthorityList("ROLE_REST_USER"));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
