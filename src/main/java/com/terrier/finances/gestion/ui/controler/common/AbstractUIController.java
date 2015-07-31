package com.terrier.finances.gestion.ui.controler.common;

import java.io.Serializable;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.ui.controler.FacadeServices;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.AbstractComponent;

/**
 * Controleur d'un composant UI
 * @author vzwingma
 * 
 * @param <COMPONENT> composant associé
 */
public abstract class AbstractUIController<COMPONENT extends AbstractComponent> implements Serializable {


	/**
	 * Constructeur
	 * @param composant
	 */
	public AbstractUIController(COMPONENT composant){
		this.component = composant;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5842153579328118762L;
	// Page associée au controleur
	private COMPONENT component;

	/**
	 * Initialisation des composants
	 */
	public abstract void initDynamicComponentsOnPage();

	
	/**
	 * Mise à jour de la vue avec les données chargées
	 */
	public abstract void miseAJourVueDonnees();
	
	/**
	 * Démarrage
	 * @param component associée
	 */
	public void start(){
		// Enregistrement des controleurs
		registerControlerToUIComponentManager();
		// Init
		initDynamicComponentsOnPage();

		miseAJourVueDonnees();
	}
	
	/**
	 * @return service métier dépense
	 */
	public BusinessDepensesService getServiceDepense(){
		return FacadeServices.get().getServiceDepense();
	}
	
	/**
	 * @return service paramétrage
	 */
	public ParametragesService getServiceParams(){
		return FacadeServices.get().getServiceParams();
	}
	
	/**
	 * @return service auth
	 */
	public AuthenticationService getServiceAuthentification(){
		return FacadeServices.get().getServiceAuth();
	}
	/**
	 * Register le controleur sur la session IHM
	 */
	private void registerControlerToUIComponentManager(){
		UISessionManager.getSession().registerUIControler(this);
	}
	
	/**
	 * @return the page
	 */
	public COMPONENT getComponent() {
		return component;
	}	
}
