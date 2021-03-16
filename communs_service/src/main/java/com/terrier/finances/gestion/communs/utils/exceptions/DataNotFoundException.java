package com.terrier.finances.gestion.communs.utils.exceptions;

/**
 * Erreur sur le chargement de données
 * @author vzwingma
 *
 */
public class DataNotFoundException extends AbstractBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5067449966377506087L;

	/**
	 * @param message d'erreur
	 */
	public DataNotFoundException(String message) {
		super(message);
	}
}
