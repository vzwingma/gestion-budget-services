/**
 * 
 */
package com.terrier.finances.gestion.data.mongodb;

/**
 * Clé de variable d'environnement
 * @author vzwingma
 *
 */
public enum MongoDBCOnfigEnum {

	MONGODB_CONFIG_DB("mongodb.config.db.name"), 
	MONGODB_CONFIG_HOST("mongodb.config.db.host"), 
	MONGODB_CONFIG_PORT("mongodb.config.db.port"),
	MONGODB_CONFIG_USERNAME("mongodb.config.db.username"),
	MONGODB_CONFIG_PWD("mongodb.config.db.password");

	// Clé de la variable d'environnement
	private String key;

	private MongoDBCOnfigEnum(String key){
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}
