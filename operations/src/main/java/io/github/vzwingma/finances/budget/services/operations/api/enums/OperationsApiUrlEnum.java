package io.github.vzwingma.finances.budget.services.operations.api.enums;

/**
 * Enum des URL d'API
 * @author vzwingma
 *
 */
public class OperationsApiUrlEnum {


	private OperationsApiUrlEnum(){
		// Constructeur privé pour une classe enum
	}
	
	public static final String PARAM_ID_BUDGET = "{idBudget}";
	public static final String PARAM_ID_COMPTE = "{idCompte}";
	public static final String PARAM_ID_OPERATION = "{idOperation}";

	
	/**
	 * Budget
	 */
	public static final String BUDGET_BASE = "/budgets/v2";
	public static final String BUDGET_ID = "/"+ PARAM_ID_BUDGET;

	// Avec en paramètre
	// - idCompte & mois & annee
	public static final String BUDGET_QUERY = "/query"; 

	// Avec en paramètres : 
	// - actif
	public static final String BUDGET_ETAT = "/"+PARAM_ID_BUDGET+"/etat";
	/// ou
	// - uptodateto (long timestamp)
	public static final String BUDGET_UP_TO_DATE = "/"+PARAM_ID_BUDGET+"/upToDate";
	public static final String BUDGET_COMPTE_INTERVALLES = "/"+PARAM_ID_COMPTE+"/intervalles";
	public static final String BUDGET_COMPTE_OPERATIONS_LIBELLES = "/"+PARAM_ID_COMPTE+"/operations/libelles";

	/**
	 * Operations
	 */
	public static final String BUDGET_OPERATION = BUDGET_ID + "/operations/"+PARAM_ID_OPERATION ;
	public static final String BUDGET_OPERATION_DERNIERE = BUDGET_OPERATION + "/derniereOperation";
	public static final String BUDGET_OPERATION_INTERCOMPTE = BUDGET_OPERATION + "/versCompte/"+PARAM_ID_COMPTE ;
}
