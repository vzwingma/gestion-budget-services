/**
 * 
 */
package com.terrier.finances.gestion.ui.styles.total;


import java.math.BigDecimal;

import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.TextRenderer;

import elemental.json.Json;
import elemental.json.JsonValue;


/**
 * Style des cellules du tableau des catégories
 * @author vzwingma
 *
 */
public class GridTotalCellStyle extends TextRenderer implements StyleGenerator<TotalBudgetMensuel> {


	private static final long serialVersionUID = -2438700237527871644L;


	private EntetesTreeResumeDepenseEnum colonne;

	public GridTotalCellStyle(EntetesTreeResumeDepenseEnum colonne) {
		this.colonne = colonne;
	}

	@Override
	public String apply(TotalBudgetMensuel item) {
		if(EntetesTreeResumeDepenseEnum.VALEUR_NOW.equals(this.colonne)){
			return item.getTotalADate() >= 0 ? "valeur v-table-cell-content-totaux" : "valeur_rouge v-table-cell-content-totaux";
		}
		else if(EntetesTreeResumeDepenseEnum.VALEUR_FIN.equals(this.colonne)){
			return item.getTotalFinMois() >= 0 ? "valeur v-table-cell-content-totaux" : "valeur_rouge v-table-cell-content-totaux";
		}
		return "v-table-cell-content-totaux";
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.renderers.TextRenderer#encode(java.lang.Object)
	 */
	@Override
	public JsonValue encode(Object value) {
        if (value == null) {
            return super.encode(null);
        } else if(value instanceof Double){
        	StringBuilder valeur = new StringBuilder();
			Double truncatedDouble=BigDecimal.valueOf((Double)value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			valeur.append(truncatedDouble);
			valeur.append(" €");
            return Json.create(valeur.toString());
        }
		return super.encode(value);
	}
}
