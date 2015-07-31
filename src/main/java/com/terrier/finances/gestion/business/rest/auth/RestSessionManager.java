package com.terrier.finances.gestion.business.rest.auth;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.UserNotAuthorizedException;

/**
 * Gestionnaire de sessions REST
 * @author vzwingma
 *
 */
public class RestSessionManager {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestSessionManager.class);

	@Autowired
	AuthenticationService serviceAuth;
	
	// Gestionnaire des sessions REST
	private Map<String, RestSession> sessionsManager = new HashMap<String, RestSession>();

	private static RestSessionManager instance;
	
	public static synchronized RestSessionManager getInstance(){
		if(RestSessionManager.instance == null){
			RestSessionManager.instance = new RestSessionManager();
		}
		return RestSessionManager.instance;
	}
	/**
	 * @return l'instance du manager UI
	 */
	public void registerSession(String authUser, Utilisateur utilisateur){
		String idSession = AuthenticationService.hashPassWord("Basic " + authUser);
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
		LOGGER.info("AuthUser : {}", authUser);
		String idSession = AuthenticationService.hashPassWord(authUser);
		if(authUser == null){
			LOGGER.error("Session {} introuvable", idSession);
			throw new UserNotAuthorizedException();
		}
		if(sessionsManager.get(idSession) == null){
			// 2eme chance pour l'auth
			String[] loginMdp;
			try {
				loginMdp = new String(Base64.decodeBase64(authUser), "UTF8").substring(6).split(":");
				Utilisateur utilisateur = serviceAuth.authenticate(loginMdp[0], loginMdp[1]);
				registerSession(authUser, utilisateur);
			} catch (UnsupportedEncodingException e) {
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
}
