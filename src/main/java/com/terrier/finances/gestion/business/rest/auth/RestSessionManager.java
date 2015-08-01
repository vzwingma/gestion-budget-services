package com.terrier.finances.gestion.business.rest.auth;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.UserNotAuthorizedException;

/**
 * Gestionnaire de sessions REST
 * @author vzwingma
 *
 */
@Service
public class RestSessionManager implements AuthenticationProvider {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestSessionManager.class);

	@Autowired
	private AuthenticationService authenticationService;
	
	// Gestionnaire des sessions REST
	private Map<String, RestSession> sessionsManager = new HashMap<String, RestSession>();

	/**
	 * @return l'instance du manager UI
	 */
	public void registerSession(String authUser, Utilisateur utilisateur){
		String idSession = AuthenticationService.hashPassWord(authUser);
		if(sessionsManager.get(idSession) == null){
			LOGGER.info("Création de la session : {}", idSession);
			sessionsManager.put(idSession, new RestSession(idSession));
		}
		sessionsManager.get(idSession).registerUtilisateur(utilisateur);
	}
	
	/**
	 * @return l'instance du manager UI
	 * @throws UnsupportedEncodingException 
	 */
	public RestSession getSession(String authUser) throws UserNotAuthorizedException{
		String idSession = AuthenticationService.hashPassWord(authUser);
		if(authUser == null){
			LOGGER.error("Session {} introuvable", idSession);
			throw new UserNotAuthorizedException();
		}

		if(sessionsManager.get(idSession) == null){
			// 2eme chance pour l'auth
			try {
				String[] loginMdp = new String(Base64.decodeBase64(authUser.substring(6)), "UTF-8").split(":");
				Utilisateur utilisateur = authenticationService.authenticate(loginMdp[0], loginMdp[1]);
				registerSession(authUser, utilisateur);
			} catch (Exception e) {
				LOGGER.error("Erreur", e);
				throw new UserNotAuthorizedException();
			}
		}
		
		if(sessionsManager.get(idSession) == null){
			LOGGER.error("Session {} introuvable", idSession);
			throw new UserNotAuthorizedException();
		}
		return sessionsManager.get(idSession);
	}
	
	
	


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
		else{
			registerSession(getIdSession(username+":"+password), utilisateur);
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
	

	/**
	 * @param data données
	 * @return données en base64
	 */
	private String getIdSession(String data){
		return "Basic " + Base64.encodeBase64String(data.getBytes());
	}
}
