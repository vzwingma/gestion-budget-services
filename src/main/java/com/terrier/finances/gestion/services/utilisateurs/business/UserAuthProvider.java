package com.terrier.finances.gestion.services.utilisateurs.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

/**
 * Gestionnaire de sessions REST
 * @author vzwingma
 *
 */
@Service
public class UserAuthProvider implements AuthenticationProvider {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthProvider.class);

	@Autowired
	private UtilisateursService authenticationService;
		


	/**
	 * Authentification REST
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		String idUtilisateur = authenticationService.authenticate(username, password);
		if (idUtilisateur == null) {
			throw new BadCredentialsException("Erreur d'authentification.");
		}
		LOGGER.info("[SEC] AuthToken : [{}]", username);
		return new UsernamePasswordAuthenticationToken(idUtilisateur, password, AuthorityUtils.createAuthorityList("ROLE_REST_USER"));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
