package com.terrier.finances.gestion.communs.utils.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exeption métier
 * @author vzwingma
 *
 */
public class AbstractBusinessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8869692972880299979L;

	/**
	 * Exception métier
	 * @param libelleErreur
	 */
    public AbstractBusinessException(String libelleErreur){
        logErreur(libelleErreur, null);
	}

	/**
	 * Exception métier
	 * @param libelleErreur libellé Erreur
	 * @param e exception
	 */
	public AbstractBusinessException(String libelleErreur, Throwable e){
		logErreur(libelleErreur, e);
	}

	/**
	 * @param libelleErreur libellé Erreur
	 * @param ex exception
	 */
	private void logErreur(String libelleErreur, Throwable ex){
		/**
		 * Logger
		 */
		Logger logger = LoggerFactory.getLogger(this.getClass());
		if(ex != null){
			logger.error("{}", libelleErreur);
		}
		else{
			logger.error("{}", libelleErreur, ex);
		}
	}

}
