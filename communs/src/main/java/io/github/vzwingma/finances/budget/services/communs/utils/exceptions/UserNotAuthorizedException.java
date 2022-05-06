package io.github.vzwingma.finances.budget.services.communs.utils.exceptions;

/**
 * Utilisateur non autorisé
 * @author vzwingma
 *
 */
public class UserNotAuthorizedException extends AbstractBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428709492299879225L;
	
	/**
	 * Message d'erreur
	 * @param libelleErreur message d'erreur
	 */
	public UserNotAuthorizedException(String libelleErreur){
		super(libelleErreur);
	}
}
