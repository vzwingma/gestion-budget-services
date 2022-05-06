package io.github.vzwingma.finances.budget.services.communs.utils.exceptions;

/**
 * Utilisateur non authentifié
 * @author vzwingma
 *
 */
public class UserAccessForbiddenException extends AbstractBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428709492299879225L;
	
	/**
	 * Message d'erreur
	 * @param libelleErreur libellé de l'erreur
	 */
	public UserAccessForbiddenException(String libelleErreur){
		super(libelleErreur);
	}
}
