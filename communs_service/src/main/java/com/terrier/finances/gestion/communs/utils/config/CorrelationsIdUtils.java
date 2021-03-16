package com.terrier.finances.gestion.communs.utils.config;

import com.terrier.finances.gestion.communs.api.security.ApiHeaderIdEnum;

public class CorrelationsIdUtils {


	private CorrelationsIdUtils() {
		// Constructeur privé
	}
	/**
	 * Setter des id de corrélation dans les logs
	 * @param corrID id de corrélation fonctionnel
	 * @param apiCorrID id de corrélation technique d'appel d'API
	 */
	public static void putCorrIdsOnMDC(String corrID, String apiCorrID) {
		putCorrIdOnMDC(corrID);
		putApiIdOnMDC(apiCorrID);
	}

	/**
	 * Setter des id de corrélation dans les logs
	 * @param corrId id de corrélation fonctionnel
	 */
	public static void putCorrIdOnMDC(String corrId) {
		org.slf4j.MDC.put(ApiHeaderIdEnum.HEADER_CORRELATION_ID, new StringBuilder("[").append(ApiHeaderIdEnum.LOG_CORRELATION_ID).append("=").append(corrId).append("]").toString());
	}
	/**
	 * Setter des id de corrélation dans les logs
	 * @param apiCorrID id de corrélation technique d'appel d'API
	 */
	public static void putApiIdOnMDC(String apiCorrID) {
		org.slf4j.MDC.put(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID, new StringBuilder("[").append(ApiHeaderIdEnum.API_CORRELATION_ID).append("=").append(apiCorrID).append("]").toString());
	}
	/**
	 * Clear de l'id de corrélation techniqu
	 */
	public static void clearApiIdOnMDC(){
		org.slf4j.MDC.remove(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID);
	}
}
