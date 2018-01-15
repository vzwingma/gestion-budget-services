/**
 * 
 */
package com.terrier.finances.gestion.ui.components.abstrait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.ui.TreeGrid;

/**
 * @author vzwingma
 *
 * @param <CONTROL> controleur
 */
public abstract class AbstractUITreeGridComponent<OBJECT, CONTROL extends AbstractUIController<?>> extends TreeGrid<OBJECT> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504689977742498737L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUITreeGridComponent.class);
	
	private CONTROL controleur;
	


	/**
	 * Démarrage
	 */
	public void startControleur(){
		controleur = createControleur();
		LOGGER.info("[INIT] Démarrage du controleur {}", getControleur());
		controleur.start();
		paramComponentsOnPage();
	}
	
	/**
	 * Initialisation des composants graphiques suite au démarrage du controleur
	 */
	public abstract void paramComponentsOnPage();
	

	
	/**
	 * @return controleur associé
	 */
	public CONTROL getControleur(){
		return controleur;
	}
	
	/**
	 * @return création d'un controleur
	 */
	public abstract CONTROL createControleur();

}
