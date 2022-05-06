package io.github.vzwingma.finances.budget.services.communs.utils.exceptions;

/**
 * Budget non trouvé
 * @author vzwingma
 *
 */
public class BudgetNotFoundException extends AbstractBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7444641623195237945L;

	/**
	 * Exception Budget introuvable
	 * @param libelleErreur libellé de l'erreur
	 */
	public BudgetNotFoundException(String libelleErreur) {
		super(libelleErreur);
	}

}
