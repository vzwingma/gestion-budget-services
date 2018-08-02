/**
 * 
 */
package com.terrier.finances.gestion.ui.components.abstrait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.Grid;


/**
 * @author vzwingma
 * @param <D> donnée métier
 * @param <C> controleur
 */
public abstract class AbstractUIGridComponent<C extends AbstractUIController<?>, D> extends Grid<D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504689977742498737L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUIGridComponent.class);
	
	private C controleurGrid;
	


	/**
	 * Démarrage
	 */
	public void startControleur(){
		controleurGrid = createControleurGrid();
		LOGGER.info("[INIT] Démarrage du controleur {}", getControleur());
		controleurGrid.start();
		paramComponentsOnGrid();
	}
	
	/**
	 * Initialisation des composants graphiques suite au démarrage du controleur
	 */
	public abstract void paramComponentsOnGrid();
	
	/**
	 * @return controleur associé
	 */
	public C getControleur(){
		return controleurGrid;
	}
	
	/**
	 * @return création d'un controleur
	 */
	public abstract C createControleurGrid();

	
	/**
	 * @return le budget mensuel courant
	 */
	public BudgetMensuel getBudgetMensuelCourant(){
		return UISessionManager.get().getSession().getBudgetMensuelCourant();
	}
}
