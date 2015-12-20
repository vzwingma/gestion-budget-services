package com.terrier.finances.gestion.ui.sessions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.UI;

/**
 * Gestionnaire des UI par Session utilisateur
 * @author vzwingma
 *
 */
public class UISessionManager {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UISessionManager.class);

	// Gestionnaire des composants UI
	private static Map<String, UISession> componentsManager = new HashMap<String, UISession>();
	

	/**
	 * @return l'instance du manager UI
	 */
	public static UISession getSession(){
		String idSession = null;
		if(UI.getCurrent() != null && UI.getCurrent().getSession() != null && UI.getCurrent().getSession().getCsrfToken() != null){
			 idSession = UI.getCurrent().getSession().getCsrfToken();
		}
		else{
			LOGGER.warn("[TEST] ***** id session de test  ***** ");
			idSession = "TEST";
		}
		if(componentsManager.get(idSession) == null){
			componentsManager.put(idSession, new UISession(idSession));
		}
		return componentsManager.get(idSession);
	}

	/**
	 * Déconnexion de l'utilisateur
	 */
	public static void deconnexion(){
		String idSession = null;
		if(UI.getCurrent() != null && UI.getCurrent().getSession() != null && UI.getCurrent().getSession().getCsrfToken() != null){
			 idSession = UI.getCurrent().getSession().getCsrfToken();
		}
		else{
			LOGGER.warn("[TEST] ***** id session de test  ***** ");
			idSession = "TEST";
		}
		componentsManager.get(idSession).deconnexion();
		componentsManager.remove(idSession);
	}
}
