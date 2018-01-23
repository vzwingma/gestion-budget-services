/**
 * 
 */
package com.terrier.finances.gestion.model.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Budget non trouvé
 * @author vzwingma
 *
 */
public class CompteClosedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428709492299879225L;


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompteClosedException.class);
	
	
	public CompteClosedException(StringBuilder libelleErreur){
		super(libelleErreur.toString());
		LOGGER.error("{}", libelleErreur);
	}
}
