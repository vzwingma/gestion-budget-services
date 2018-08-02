/**
 * 
 */
package com.terrier.finances.gestion.ui.styles.operations;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;

/**
 * Style des colonnes Actions du tableau des opérations
 * @author vzwingma
 */
public class GridOperationCellActionsStyle extends GridOperationCellStyle {


	//
	private static final long serialVersionUID = -289916798139753848L;

	@Override
	public String apply(LigneDepense depense) {
		return "v-grid-cell-actions";
	}
}
