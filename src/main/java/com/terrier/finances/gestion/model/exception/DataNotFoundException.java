/**
 * 
 */
package com.terrier.finances.gestion.model.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Erreur sur le chargement de données
 * @author vzwingma
 *
 */
public class DataNotFoundException extends Exception {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataNotFoundException.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5428709492299879225L;

	/**
	 * @param message
	 */
	public DataNotFoundException(String message) {
		super(message);
		LOGGER.error(message);
	}
	
	
	

}
