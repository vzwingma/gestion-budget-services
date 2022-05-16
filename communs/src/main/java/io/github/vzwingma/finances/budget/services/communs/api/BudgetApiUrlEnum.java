package io.github.vzwingma.finances.budget.services.communs.api;

/**
 * Enum des URL d'API
 * @author vzwingma
 *
 */
public class BudgetApiUrlEnum {


	private BudgetApiUrlEnum(){
		// Constructeur privé pour une classe enum
	}
	
	public static final String PARAM_ID_BUDGET = "idBudget";
	public static final String PARAM_ID_COMPTE = "idCompte";
	public static final String PARAM_ID_OPERATION = "idOperation";

	/**
	 * Utilisateurs
	 */
	public static final String USERS_BASE = "/utilisateurs/v2";
	public static final String USERS_ACCESS_DATE = "/lastaccessdate";
	public static final String USERS_ACCESS_DATE_FULL = USERS_BASE + USERS_ACCESS_DATE;
	public static final String USERS_PREFS = "/preferences";
	public static final String USERS_PREFS_FULL = USERS_BASE + USERS_PREFS;

	/**
	 * Comptes
	 */
	public static final String COMPTES_BASE = "/comptes/v2";
	public static final String COMPTES_LIST = "";
	public static final String COMPTES_LIST_FULL = COMPTES_BASE + COMPTES_LIST;

	public static final String COMPTES_ID = "/{"+PARAM_ID_COMPTE + "}"; 
	public static final String COMPTES_ID_FULL = COMPTES_BASE + COMPTES_ID;
	

	
	/**
	 * Paramétrages
	 */
	public static final String PARAMS_BASE = "/parametres/v2";
	public static final String PARAMS_CATEGORIES = "/categories";
	public static final String PARAMS_CATEGORIES_FULL = PARAMS_BASE + PARAMS_CATEGORIES;
	
	
	/**
	 * Budget
	 */
	public static final String BUDGET_BASE = "/budgets/v2";
	public static final String BUDGET_ID = "/{"+ PARAM_ID_BUDGET + "}";
	public static final String BUDGET_ID_FULL = BUDGET_BASE + BUDGET_ID;

	// Avec en paramètre
	// - idCompte & mois & annee
	public static final String BUDGET_QUERY = "/query"; 
	public static final String BUDGET_QUERY_FULL = BUDGET_BASE + BUDGET_QUERY;
	// Avec en paramètres : 
	// - actif
	public static final String BUDGET_ETAT = "/{"+PARAM_ID_BUDGET+"}/etat"; 
	public static final String BUDGET_ETAT_FULL = BUDGET_BASE + BUDGET_ETAT;
	/// ou
	// - uptodateto (long timestamp)
	public static final String BUDGET_UP_TO_DATE = "/{"+PARAM_ID_BUDGET+"}/upToDate"; 
	public static final String BUDGET_UP_TO_DATE_FULL = BUDGET_BASE + BUDGET_UP_TO_DATE;

	public static final String BUDGET_COMPTE_INTERVALLES = "/{"+PARAM_ID_COMPTE+"}/intervalles";
	public static final String BUDGET_COMPTE_INTERVALLES_FULL = BUDGET_BASE + BUDGET_COMPTE_INTERVALLES;
	
	public static final String BUDGET_COMPTE_OPERATIONS_LIBELLES = "/{"+PARAM_ID_COMPTE+"}/operations/libelles";
	public static final String BUDGET_COMPTE_OPERATIONS_LIBELLES_FULL = BUDGET_BASE + BUDGET_COMPTE_OPERATIONS_LIBELLES;
	
	/**
	 * Operations
	 */
	public static final String BUDGET_OPERATION = BUDGET_ID + "/operations/{"+PARAM_ID_OPERATION + "}";
	public static final String BUDGET_OPERATION_FULL = BUDGET_BASE + BUDGET_OPERATION;	

	public static final String BUDGET_OPERATION_DERNIERE = BUDGET_OPERATION + "/derniereOperation";
	public static final String BUDGET_OPERATION_DERNIERE_FULL = BUDGET_BASE + BUDGET_OPERATION_DERNIERE;
	
	public static final String BUDGET_OPERATION_INTERCOMPTE = BUDGET_OPERATION + "/versCompte/{"+PARAM_ID_COMPTE + "}";
	public static final String BUDGET_OPERATION_INTERCOMPTE_FULL = BUDGET_BASE + BUDGET_OPERATION_INTERCOMPTE;
}
