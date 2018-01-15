package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import com.terrier.finances.gestion.model.business.budget.ResumeTotalCategories;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUITreeGridComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.resume.TreeGridResumeCategoriesController;
import com.terrier.finances.gestion.ui.styles.total.GridTotalCellStyle;

/**
 * Tableau de suivi des dépenses
 * @author vzwingma
 *
 */
public class TreeGridResumeCategories extends AbstractUITreeGridComponent<ResumeTotalCategories, TreeGridResumeCategoriesController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187184070043964584L;

	public static final int TAILLE_COLONNE_VALEUR = 90;
	
	
	public TreeGridResumeCategories(){
		// Start controleur
		startControleur();

	}

	
	@Override
	public void paramComponentsOnPage() {
		
		addColumn(ResumeTotalCategories::getTypeTotal)
		.setId(EntetesTreeResumeDepenseEnum.CATEGORIE.getId())
		.setCaption(EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle());
		
		addColumn(ResumeTotalCategories::getTotalADate)
		.setId(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId())
		.setCaption(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle())
		.setWidth(TAILLE_COLONNE_VALEUR)
		.setRenderer(new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_NOW));
		
		addColumn(ResumeTotalCategories::getTotalFinMois)
		.setId(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId())
		.setCaption(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle())
		.setWidth(TAILLE_COLONNE_VALEUR)
		.setRenderer(new GridTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_FIN));
		
		/**

		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Align.RIGHT);
		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Align.RIGHT);
		 */
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITreeTableComponent#createControleur()
	 */
	@Override
	public TreeGridResumeCategoriesController createControleur() {
		return new TreeGridResumeCategoriesController(this);
	}



}
