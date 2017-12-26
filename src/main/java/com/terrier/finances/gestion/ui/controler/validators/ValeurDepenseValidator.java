/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

/**
 * @author vzwingma
 *
 */
public class ValeurDepenseValidator implements Validator<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -624292040215572087L;

	private String message;

	public ValeurDepenseValidator(String message){
		this.message = message;
	}

	@Override
	public ValidationResult apply(Object value, ValueContext context) {
		if(value != null){
			if( value instanceof String){
				try{
					String valeur = ((String)value).replaceAll(",", ".");
					Double d = Double.valueOf(valeur);
					if(!Double.isInfinite(d) && !Double.isNaN(d)){
						return null;
					}			
				}
				catch(NumberFormatException e){ }
			}
			else if(value instanceof Float){
				Float f = (Float)value;
				if(!Float.isInfinite(f) && !Float.isNaN(f)){
					return null;
				}
			}
		}
		//throw new InvalidValueException(message);
		return null;
	}



}
