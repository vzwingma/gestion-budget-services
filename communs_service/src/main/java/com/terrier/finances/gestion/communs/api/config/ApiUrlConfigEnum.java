package com.terrier.finances.gestion.communs.api.config;

/**
 * Enum des config
 * @author vzwingma
 *
 */
public enum ApiUrlConfigEnum {

	// URL des services
	APP_CONFIG_URL_PARAMS("http://localhost:8091"),
	
	APP_CONFIG_URL_OPERATIONS("http://localhost:8094"),
	
	APP_CONFIG_URL_COMPTES("http://localhost:8092"),

	APP_CONFIG_URL_UTILISATEURS("http://localhost:8093");	
	
	
	private final String defaultValue;
	
	ApiUrlConfigEnum(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
}
