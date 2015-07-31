package com.terrier.finances.gestion.business.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;

/**
 * Gestionnaire des UI par Session utilisateur
 * @author vzwingma
 *
 */
public class RestSession {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestSession.class);

	private String idSession;
	
	/**
	 * Session Manager
	 * @param idSession idSessions
	 */
	public RestSession(String idSession){
		LOGGER.debug("[INIT][{}] Session REST ", idSession);
		this.idSession = idSession;
	}

	/**
	 * Utilisateur courant
	 */
	private Utilisateur utilisateur = new Utilisateur();

	/**
	 * Déconnexion de l'utilisateur
	 */
	public void deconnexion(){
		// Suppression de l'utilisateur
		this.utilisateur = null;
	}


	/**
	 * Enregistrement de l'utilisateur
	 * @param utilisateur USER
	 */
	public boolean registerUtilisateur(Utilisateur utilisateur){
		LOGGER.info("[{}] Enregistrement de l'utilisateur : {}", idSession, utilisateur);
		this.utilisateur = utilisateur;
		return true;
	}
	
	
	/**
	 * @return the utilisateurCourant
	 */
	public Utilisateur getUtilisateur() {
		// LOGGER.trace("[{}] Utilisateur courant > {}", this.idSession, this.utilisateurCourant);
		return utilisateur;
	}
	
	
	
	/**
	 * @return the idSession
	 */
	public String getIdSession() {
		return idSession;
	}
}
