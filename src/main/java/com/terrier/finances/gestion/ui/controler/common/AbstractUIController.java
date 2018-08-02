package com.terrier.finances.gestion.ui.controler.common;

import java.io.Serializable;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.business.OperationsService;
import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.ui.controler.FacadeServices;
import com.vaadin.ui.AbstractComponent;

/**
 * Controleur d'un composant UI
 * @author vzwingma
 * 
 * @param <P> composant associé
 */
public abstract class AbstractUIController<P extends AbstractComponent> implements AbstractUIService, Serializable {


	/**
	 * Constructeur
	 * @param composant
	 */
	public AbstractUIController(P composant){
		this.component = composant;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5842153579328118762L;
	// Page associée au controleur
	private P component;


	
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
		// Ajout des données
		miseAJourVueDonnees();
	}
	
	/**
	 * @return service métier dépense
	 */
	public OperationsService getServiceOperations(){
		return FacadeServices.get().getServiceOperations();
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
		getUISession().registerUIControler(this);
	}
	
	/**
	 * @return the page
	 */
	public P getComponent() {
		return component;
	}	
}
