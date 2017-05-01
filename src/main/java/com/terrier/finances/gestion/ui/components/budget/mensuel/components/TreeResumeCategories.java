package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import java.math.BigDecimal;

import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUITreeTableComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TreeResumeCategoriesController;
import com.vaadin.v7.data.Property;

/**
 * Tableau de suivi des dépenses
 * @author vzwingma
 *
 */
public class TreeResumeCategories extends AbstractUITreeTableComponent<TreeResumeCategoriesController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187184070043964584L;


	public TreeResumeCategories(){
		// Start controleur
		startControleur();

	}
	
	/**
	 * Affichage dans le tableau en mode consultation
	 * @see com.vaadin.ui.Table#formatPropertyValue(java.lang.Object, java.lang.Object, com.vaadin.data.Property)
	 */
	@Override
	protected String formatPropertyValue(Object rowId, Object colId,
			Property<?> property) {

		String colonneId = (String)colId;
		
		if(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId().equals(colonneId) || EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId().equals(colonneId)){
			StringBuffer valeur = new StringBuffer();
			Double value = (Double)property.getValue();
			
			if(value != null){
				value = new BigDecimal((Double)property.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			else{
				value = 0D;
			}
			valeur.append(Math.abs(value));
			valeur.append(" €");
			return valeur.toString();
		}	
		return super.formatPropertyValue(rowId, colId, property);
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITreeTableComponent#createControleur()
	 */
	@Override
	public TreeResumeCategoriesController createControleur() {
		return new TreeResumeCategoriesController(this);
	}
}
