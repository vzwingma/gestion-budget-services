package com.terrier.finances.gestion.communs.utils.exceptions;

/**
 * Budget non trouvé
 * @author vzwingma
 *
 */
public class CompteClosedException extends AbstractBusinessException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5654536011914512355L;

	/**
	 * Erreur compte clos
	 * @param libelleErreur libellé de l'erreur
	 */
	public CompteClosedException(String libelleErreur){
		super(libelleErreur);
	}
}
