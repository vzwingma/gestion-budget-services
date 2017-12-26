package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIGridComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux.GridResumeTotauxController;
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
	public void paramComponentsOnPage() {
		
		setSelectionMode(SelectionMode.NONE);
		// Style
		setStyleGenerator(new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.CATEGORIE));
////	getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Align.RIGHT);
////	getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Align.RIGHT);
		
		/**
		 * Total resume
		 */
		Column<TotalBudgetMensuel, String> c = addColumn(TotalBudgetMensuel::getTypeTotal).setCaption(EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle());
		c.setId(EntetesTreeResumeDepenseEnum.CATEGORIE.getId());
		c.setSortable(false);
		c.setResizable(false);
		c.setHidable(false);
		//c.setStyleGenerator(new TableTotalCellStyle(EntetesTreeResumeDepenseEnum.CATEGORIE));

		Column<TotalBudgetMensuel, Double> c2 = addColumn(TotalBudgetMensuel::getTotalADate);
		c2.setId(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId());
		c2.setSortable(false);
		c2.setResizable(false);
		c2.setHidable(false);
		GridTotalCellStyle c2style = new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_NOW);
		c2.setStyleGenerator(c2style);
		c2.setRenderer(c2style);
		
		Column<TotalBudgetMensuel, Double> c3 = addColumn(TotalBudgetMensuel::getTotalFinMois);
		c3.setId(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId());
		c3.setSortable(false);
		c3.setResizable(false);
		c3.setHidable(false);
		GridTotalCellStyle c3style = new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_FIN);
		c3.setStyleGenerator(c3style);
		c3.setRenderer(c3style);
	}
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITableComponent#createControleur()
	 */
	@Override
	public GridResumeTotauxController createControleur() {
		return new GridResumeTotauxController(this);
	}
}
