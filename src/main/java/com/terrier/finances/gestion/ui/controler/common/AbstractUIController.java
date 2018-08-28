package com.terrier.finances.gestion.ui.controler.common;

import java.io.Serializable;

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
	 * Register le controleur sur la session IHM
	 */
	private void registerControlerToUIComponentManager(){
		getUserSession().registerUIControler(this);
	}
	
	/**
	 * @return the page
	 */
	public P getComponent() {
		return component;
	}	
}
