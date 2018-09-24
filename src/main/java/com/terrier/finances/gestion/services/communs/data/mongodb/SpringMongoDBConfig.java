/**
 * 
 */
package com.terrier.finances.gestion.services.communs.data.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientURI;

/**
 * Configuration de connexion à la BDD via des variables d'environnement
 * @author vzwingma
 *
 */
@Configuration
public class SpringMongoDBConfig {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoDBConfig.class);


	/**
	 * 
	 * @param host de la BDD
	 * @param port de la BDD
	 * @param username login
	 * @param password mot de passe
	 * @param db nom de la BDD
	 * @return Template de connexion BDD
	 */
	@Bean
	public MongoTemplate mongoTemplate() {

		String db = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_DB, "budget-app-dev");
		String host = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_HOST, "ds113936.mlab.com");
		String username = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_USERNAME, "budget");
		String password = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PWD, "budgetdev");
		int port =  getIntgerEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PORT, 13936);

		//create mongo template
		String mongoURI = new StringBuilder("mongodb://").append(username).append(":").append(password).append("@").append(host).append(":").append(port).append("/").append(db).toString();
		String mongoURILog = new StringBuilder("mongodb://").append(username).append("@").append(host).append(":").append(port).append("/").append(db).toString();

		LOGGER.info("[INIT] Configuration de la connexion vers MongDB : [{}]", mongoURILog);
		return new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(mongoURI)));
	}


	/**
	 * Retourne la valeur string de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	private String getStringEnvVar(MongoDBConfigEnum cle, String defaultVar){
		String envVar = System.getenv(cle.name());
		if(envVar != null) {
			return envVar;
		}
		else {
			LOGGER.warn("La clé {0} n'est définie. Utilisation de la valeur par défaut : {1} ", cle.name(), defaultVar);
			 return defaultVar;
		}
	}

	/**
	 * Retourne la valeur int de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	private Integer getIntgerEnvVar(MongoDBConfigEnum cle, Integer defaultVar){
		String env = System.getenv(cle.name());
		try{
			return Integer.parseInt(env);
		}
		catch(NumberFormatException e){
			LOGGER.error("La clé {0}={1} n'est pas un nombre. La valeur par défaut : {2} ", cle.name(), env, defaultVar);
			return defaultVar;
		}
	}
}
