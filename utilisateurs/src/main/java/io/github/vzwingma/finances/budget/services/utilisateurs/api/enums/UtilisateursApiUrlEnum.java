package io.github.vzwingma.finances.budget.services.utilisateurs.api.enums;

/**
 * Enum des URL d'API
 * @author vzwingma
 *
 */

public class UtilisateursApiUrlEnum {


	private UtilisateursApiUrlEnum(){
		// Constructeur privé pour une classe enum
	}

	/**
	 * Utilisateurs
	 */
	public static final String USERS_BASE = "/utilisateurs/v1";
	public static final String USERS_ACCESS_DATE = "/lastaccessdate";
	public static final String USERS_PREFS = "/preferences";

}
