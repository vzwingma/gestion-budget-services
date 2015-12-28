package com.terrier.finances.gestion.ui.sessions;

import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.FacadeServices;
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
	private static ConcurrentHashMap<String, UISession> componentsManager = new ConcurrentHashMap<String, UISession>();


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
		UISession session = componentsManager.putIfAbsent(idSession, new UISession(idSession));
		session.setLastAccessTime(Calendar.getInstance());
		return session;
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


	/**
	 * @return le nombre de sessions actives soit utilisateur authentifié
	 */
	public static int getNombreSessionsActives(){
		int nbSessionsActives = 0;
		int sessionValidity = Integer.parseInt(FacadeServices.get().getServiceParams().getUiValiditySessionPeriod());
		Calendar validiteSession = Calendar.getInstance();
		validiteSession.add(Calendar.MINUTE, -sessionValidity);
		if(componentsManager != null){
			for (Iterator<UISession> iterator = componentsManager.values().iterator(); iterator.hasNext();) {
				UISession session = (UISession) iterator.next();
				if(session.getLastAccessTime().before(validiteSession)){
					LOGGER.warn("La session {} n'a pas été utilisé depuis {}. Déconnexion automatique", session.getIdSession(), validiteSession.getTime());
					iterator.remove();
				}
				LOGGER.debug(" > {} : active : {}. Dernière activité : {}", session.getIdSession(), session.isActive(), session.getLastAccessTime().getTime());
				if(session.isActive()){
					nbSessionsActives ++;
				}
			}
		}
		return nbSessionsActives;
	}
}
