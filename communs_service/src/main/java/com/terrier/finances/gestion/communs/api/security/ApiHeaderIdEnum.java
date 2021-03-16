package com.terrier.finances.gestion.communs.api.security;

/**
 * Enum des entêtes HTTP pour gérer les CorrID
 * @author vzwingma
 *
 */
public class ApiHeaderIdEnum {
	
	/**
	 * Entête ID de corrélation fonctionnel
	 */
	public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
	/**
	 * Entête ID de corrélation technique
	 */
	public static final String HEADER_API_CORRELATION_ID = "X-API-Correlation-ID";
	/**
	 * Mot clé ID de corrélation fonctionnel
	 */
	public static final String LOG_CORRELATION_ID = "CorrId";
	/**
	 * Mot clé ID de corrélation technique
	 */
	public static final String API_CORRELATION_ID = "ApiId";
	
	private ApiHeaderIdEnum() {
		// Constructeur privé pour utilitaire
	}
}
