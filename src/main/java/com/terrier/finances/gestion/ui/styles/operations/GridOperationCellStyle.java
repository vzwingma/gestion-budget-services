/**
 * 
 */
package com.terrier.finances.gestion.ui.styles.operations;

import com.terrier.finances.gestion.business.OperationsService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.vaadin.ui.StyleGenerator;

/**
 * Style des lignes du tableau de dépense
 * @author vzwingma
 */
public class GridOperationCellStyle implements StyleGenerator<LigneDepense> {


	private static final long serialVersionUID = -6709397765771547573L;


	@Override
	public String apply(LigneDepense depense) {

		// Style de la ligne
		StringBuilder style = new StringBuilder("v-grid-row-");
		//  Sauf pour les dépenses réalisées, et celle réserve

		if(!OperationsService.ID_SS_CAT_RESERVE.equals(depense.getSsCategorie().getId())){
			style.append(depense.getEtat().getId());
		}

		// Style de la dernière opération
		if(depense.isDerniereOperation()){
			style.append(" last-depense");
		}
		return style.toString();
	}
}
