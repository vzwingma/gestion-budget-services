package com.terrier.finances.gestion.business.rest.auth;

import java.util.HashMap;
import java.util.Map;

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

	// Gestionnaire des sessions REST
	private static Map<String, RestSession> RESTSessionManager = new HashMap<String, RestSession>();

	
	/**
	 * @return l'instance du manager UI
	 */
	public static void registerSession(String authUser, Utilisateur utilisateur){
		String idSession = AuthenticationService.hashPassWord("Basic " + authUser);
		if(RESTSessionManager.get(idSession) == null){
			LOGGER.info("Création de la session : {}", idSession);
			RESTSessionManager.put(idSession, new RestSession(idSession));
		}
		RESTSessionManager.get(idSession).registerUtilisateur(utilisateur);
	}
	
	/**
	 * @return l'instance du manager UI
	 */
	public static RestSession getSession(String authUser) throws UserNotAuthorizedException{
		LOGGER.info("AuthUser : {}", authUser);
		String idSession = AuthenticationService.hashPassWord(authUser);
		
		if(RESTSessionManager.get(idSession) == null || authUser == null){
			LOGGER.error("Session {} introuvable", idSession);
			throw new UserNotAuthorizedException();
		}
		return RESTSessionManager.get(idSession);
	}
}
