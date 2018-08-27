package com.terrier.finances.gestion.ui.sessions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.ui.controler.FacadeServices;
import com.vaadin.ui.UI;

/**
 * Gestionnaire des UI par Session utilisateur
 * @author vzwingma
 *
 */
@Service
public class UISessionManager implements Runnable {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UISessionManager.class);

	// Gestionnaire des composants UI
	private ConcurrentHashMap<String, UISession> sessionsMap = new ConcurrentHashMap<>();

	private ScheduledThreadPoolExecutor pool;

	private static UISessionManager sessionManager;

	/**
	 * @return l'instance de sessions Manager
	 */
	public static UISessionManager get(){
		return UISessionManager.sessionManager;
	}
	/**
	 * Démarrage du controle des sessions
	 */
	@PostConstruct
	public void startSessionsControl(){
		pool = new ScheduledThreadPoolExecutor(1);
		pool.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
		setSessionManager(this);
	}

	/**
	 * Update du session manager
	 * @param manager UISessionManager
	 */
	private static synchronized void setSessionManager(UISessionManager manager){
		UISessionManager.sessionManager = manager;
	}
	
	/**
	 * Arrêt du controle des sessions
	 */
	@PreDestroy
	public void stopSessionsControl(){
		this.pool.shutdown();
	}

	/**
	 * @return l'instance du manager UI
	 */
	public UISession getSession(){
		String idSession = null;
		if(UI.getCurrent() != null && UI.getCurrent().getSession() != null && UI.getCurrent().getSession().getCsrfToken() != null){
			idSession = UI.getCurrent().getSession().getCsrfToken();
		}
		else{
			LOGGER.warn("[TEST] ***** id session de test  ***** ");
			idSession = "TEST";
		}
		sessionsMap.putIfAbsent(idSession, new UISession(idSession));
		UISession session = sessionsMap.get(idSession);
		session.setLastAccessTime(Instant.now());

		return session;
	}

	/**
	 * Déconnexion de l'utilisateur
	 */
	public void deconnexion(){
		String idSession = null;
		if(UI.getCurrent() != null && UI.getCurrent().getSession() != null && UI.getCurrent().getSession().getCsrfToken() != null){
			idSession = UI.getCurrent().getSession().getCsrfToken();
		}
		else{
			LOGGER.warn("[TEST] ***** id session de test  ***** ");
			idSession = "TEST";
		}
		sessionsMap.get(idSession).deconnexion();
		sessionsMap.remove(idSession);
	}


	/**
	 * @return le nombre de sessions actives soit utilisateur authentifié
	 */
	public long getNombreSessionsActives(){
		return sessionsMap.values().stream().filter(s -> s.isActive()).count();
	}

	/**
	 * Vérification des sessions
	 */
	@Override
	public void run() {
		int sessionValidity = Integer.parseInt(FacadeServices.get().getServiceParams().getUiValiditySessionPeriod());
		Instant validiteSession = Instant.now();
		LOGGER.info("Durée de validité d'une session : {} minutes", sessionValidity);
		validiteSession = validiteSession.minus(sessionValidity, ChronoUnit.MINUTES);
		for (Iterator<UISession> iterator = sessionsMap.values().iterator(); iterator.hasNext();) {
			UISession session = iterator.next();
			if(session.getLastAccessTime().isBefore(validiteSession)){
				LOGGER.warn("La session {} n'a pas été utilisé depuis {}. Déconnexion automatique", session.getIdSession(), validiteSession);
				iterator.remove();
			}
			else{
				LOGGER.debug(" > {} : active : {}. Dernière activité : {}", session.getIdSession(), session.isActive(), session.getLastAccessTime());
			}
		}
	}
}
