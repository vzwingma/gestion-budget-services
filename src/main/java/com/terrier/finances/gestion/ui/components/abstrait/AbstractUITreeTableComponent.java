/**
 * 
 */
package com.terrier.finances.gestion.ui.components.abstrait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.v7.ui.TreeTable;

/**
 * @author vzwingma
 *
 * @param <CONTROL> controleur
 */
public abstract class AbstractUITreeTableComponent<CONTROL extends AbstractUIController<?>> extends TreeTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504689977742498737L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUITreeTableComponent.class);
	
	private CONTROL controleur;
	


	/**
	 * Démarrage
	 */
	public void startControleur(){
		controleur = createControleur();
		LOGGER.info("[INIT] Démarrage du controleur {}", getControleur());
		controleur.start();
	}
	

	
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
