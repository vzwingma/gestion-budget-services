/**
 * 
 */
package com.terrier.finances.gestion.services.communs.data.mongodb;

/**
 * Clé de variable d'environnement
 * @author vzwingma
 *
 */
public enum MongoDBConfigEnum {

	MONGODB_CONFIG_DB("budget-app-dev"), 
	MONGODB_CONFIG_HOST("ds113936.mlab.com"), 
	MONGODB_CONFIG_PORT("budget"),
	MONGODB_CONFIG_USERNAME( "budgetdev"),
	MONGODB_CONFIG_PWD(13936);
	
	
	private int intDefaultValue;
	private String defaultValue;
	
	private MongoDBConfigEnum(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
	private MongoDBConfigEnum(int intDefaultValue) {
		this.intDefaultValue = intDefaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * @return the defaultValue
	 */
	public int getIntDefaultValue() {
		return intDefaultValue;
	}
}
