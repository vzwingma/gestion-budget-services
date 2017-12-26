package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIGridComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableResumeTotauxController;

/**
 * Tableau de suivi des dépenses
 * @author vzwingma
 *
 */
public class TableResumeTotaux extends AbstractUIGridComponent<TableResumeTotauxController, TotalBudgetMensuel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187184070043964584L;


	public TableResumeTotaux(){
		// Start controleur
		startControleur();
	}
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITableComponent#createControleur()
	 */
	@Override
	public TableResumeTotauxController createControleur() {
		return new TableResumeTotauxController(this);
	}
}
