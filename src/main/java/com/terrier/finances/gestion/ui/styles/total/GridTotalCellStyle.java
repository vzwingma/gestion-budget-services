/**
 * 
 */
package com.terrier.finances.gestion.ui.styles.total;


import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.vaadin.ui.StyleGenerator;


/**
 * Style des cellules du tableau des catégories
 * @author vzwingma
 *
 */
public class GridTotalCellStyle implements StyleGenerator<TotalBudgetMensuel> {


	private static final long serialVersionUID = -2438700237527871644L;


	private EntetesTreeResumeDepenseEnum colonne;

	public GridTotalCellStyle(EntetesTreeResumeDepenseEnum colonne) {
		this.colonne = colonne;
	}

	@Override
	public String apply(TotalBudgetMensuel item) {
		StringBuilder style = new StringBuilder("v-grid-cell-content-totaux");
		
		if((EntetesTreeResumeDepenseEnum.VALEUR_NOW.equals(this.colonne) && item.getTotalADate() < 0)
				|| (EntetesTreeResumeDepenseEnum.VALEUR_FIN.equals(this.colonne) && item.getTotalFinMois() < 0)){
			style.append("_rouge");
		}
		return style.toString();
	}
}
