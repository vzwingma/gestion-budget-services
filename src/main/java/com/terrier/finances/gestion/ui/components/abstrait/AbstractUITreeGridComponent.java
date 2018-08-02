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
 * @param <D> donnée métier
 * @param <C> controleur
 */
public abstract class AbstractUITreeGridComponent<C extends AbstractUIController<?>, D> extends TreeGrid<D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504689977742498737L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUITreeGridComponent.class);
	
	
	private C controleur;
	


	/**
	 * Démarrage
	 */
	public void startControleur(){
		controleur = createControleurTreeGrid();
		LOGGER.info("[INIT] Démarrage du controleur {}", getControleur());
		controleur.start();
		paramComponentsOnTreeGrid();
	}
	
	/**
	 * Initialisation des composants graphiques suite au démarrage du controleur
	 */
	public abstract void paramComponentsOnTreeGrid();
	

	
	/**
	 * @return controleur associé
	 */
	public C getControleur(){
		return controleur;
	}
	
	/**
	 * @return création d'un controleur
	 */
	public abstract C createControleurTreeGrid();

}
