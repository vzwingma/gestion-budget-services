package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIGridComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux.GridResumeTotauxController;
import com.terrier.finances.gestion.ui.styles.operations.OperationBudgetTypeRenderer;
import com.terrier.finances.gestion.ui.styles.total.GridTotalCellStyle;

/**
 * Tableau de suivi des dépenses
 * @author vzwingma
 *
 */
public class GridResumeTotaux extends AbstractUIGridComponent<GridResumeTotauxController, TotalBudgetMensuel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187184070043964584L;


	public GridResumeTotaux(){
		// Start controleur
		startControleur();
	}



	@Override
	public void paramComponentsOnGrid() {

		setSelectionMode(SelectionMode.NONE);

		/**
		 * Total resume
		 */
		addColumn(TotalBudgetMensuel::getTypeTotal)
		.setCaption(EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle())
		.setId(EntetesTreeResumeDepenseEnum.CATEGORIE.getId())
		.setSortable(false)
		.setResizable(false)
		.setHidable(false);

		addColumn(TotalBudgetMensuel::getTotalADate)
		.setId(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId())
		.setSortable(false)
		.setResizable(false)
		.setHidable(false)
		.setStyleGenerator(new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_NOW))
		.setRenderer(new OperationBudgetTypeRenderer());

		addColumn(TotalBudgetMensuel::getTotalFinMois)
		.setId(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId())
		.setSortable(false)
		.setResizable(false)
		.setHidable(false)
		.setStyleGenerator(new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_FIN))
		.setRenderer(new OperationBudgetTypeRenderer());
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITableComponent#createControleur()
	 */
	@Override
	public GridResumeTotauxController createControleurGrid() {
		return new GridResumeTotauxController(this);
	}
}
