/**
 * 
 */
package com.terrier.finances.gestion.model.business.budget;

import java.util.ArrayList;
import java.util.List;

/**
 * Objet du résumé par catégorie
 * @author vzwingma
 *
 */
public class ResumeTotalCategories extends TotalBudgetMensuel {

	
	public ResumeTotalCategories(String typeTotal, double totalADate, double totalFinMois) {
		super(typeTotal, totalADate, totalFinMois);
	}

	private List<ResumeTotalCategories> sousCategories = new ArrayList<>();
	

	/**
	 * @return the sousCategories
	 */
	public List<ResumeTotalCategories> getSousCategories() {
		return sousCategories;
	}

	/**
	 * @param sousCategories the sousCategories to set
	 */
	public void setSousCategories(List<ResumeTotalCategories> sousCategories) {
		this.sousCategories = sousCategories;
	}


	
}
