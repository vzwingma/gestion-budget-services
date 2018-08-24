package com.terrier.finances.gestion.ui.styles.operations;

import java.math.BigDecimal;

import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.ui.renderers.TextRenderer;

import elemental.json.Json;
import elemental.json.JsonValue;

/**
 * Renderer des colonnes d'une opération de budget 
 * @author vzwingma
 *
 */
public class OperationBudgetTypeRenderer extends TextRenderer {

	private static final long serialVersionUID = -5303132152283523988L;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.renderers.TextRenderer#encode(java.lang.Object)
	 */
	@Override
	public JsonValue encode(Object value) {
		
		if (value == null) {
            return super.encode(null);
            // Valeur
        } else if(value instanceof Double){
        	StringBuilder valeur = new StringBuilder();
			Double truncatedDouble=BigDecimal.valueOf((Double)value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			valeur.append(truncatedDouble);
			valeur.append(" €");
            return Json.create(valeur.toString());
        }
		// Périodique
        else if(value instanceof Boolean){
            return Json.create((Boolean) value ? "oui" : "non");
        }
		// TypeDepenseEnum
        else if(value instanceof TypeDepenseEnum){
            return Json.create(((TypeDepenseEnum) value).getLibelle());
        }
		// Valeur
        else if(value instanceof Float){
        	
        	Float f = (Float)value;
        	StringBuilder valeur = new StringBuilder();
        	valeur.append(f < 0 ? "- " : "+ ");
        	valeur.append(String.format("%.2f", Math.abs(f)));
			valeur.append(" €");
            return Json.create(valeur.toString());
        }
		return super.encode(value);
	}
}
