package com.terrier.finances.gestion.services.utilisateurs.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

/**
 * Gestionnaire de sessions REST
 * @author vzwingma
 *
 */
@Deprecated
@Service
public class AuthenticationService implements AuthenticationManager {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private UtilisateursService authenticationService;

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationManager#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		String idUtilisateur = authenticationService.authenticateDeprecated(username, password);
		if (idUtilisateur == null) {
			throw new BadCredentialsException("Erreur d'authentification.");
		}
		LOGGER.info("[SEC] AuthToken : [{}]", username);
		return new UsernamePasswordAuthenticationToken(idUtilisateur, password, AuthorityUtils.createAuthorityList("ROLE_REST_USER"));
	}
}
