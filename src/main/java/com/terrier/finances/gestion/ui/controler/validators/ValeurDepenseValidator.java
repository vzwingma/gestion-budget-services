/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.validators;

import com.vaadin.data.Validator;

/**
 * @author vzwingma
 *
 */
public class ValeurDepenseValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -624292040215572087L;

	private String message;
	
	public ValeurDepenseValidator(String message){
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.data.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(Object value) throws InvalidValueException {
		if(value != null && value instanceof String){
			try{
				String valeur = ((String)value).replaceAll(",", ".");
				Double d = Double.valueOf(valeur);
				if(!Double.isInfinite(d) && !Double.isNaN(d)){
					return;
				}			
			}
			catch(NumberFormatException e){ }
		}
		throw new InvalidValueException(message);
	}

}
