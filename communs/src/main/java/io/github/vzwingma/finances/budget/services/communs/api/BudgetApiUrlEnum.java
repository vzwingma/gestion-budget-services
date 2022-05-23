package io.github.vzwingma.finances.budget.services.communs.api;

/**
 * Enum des URL d'API
 * @author vzwingma
 *
 */
@Deprecated
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
}
