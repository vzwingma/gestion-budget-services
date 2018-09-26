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
import com.terrier.finances.gestion.services.communs.AppConfig;

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

		String db = AppConfig.getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_DB);
		String host = AppConfig.getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_HOST);
		String username = AppConfig.getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_USERNAME);
		String password = AppConfig.getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PWD);
		int port = AppConfig.getIntgerEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PORT);

		//create mongo template
		String mongoURI = new StringBuilder("mongodb://").append(username).append(":").append(password).append("@").append(host).append(":").append(port).append("/").append(db).toString();
		String mongoURILog = new StringBuilder("mongodb://").append(username).append("@").append(host).append(":").append(port).append("/").append(db).toString();

		LOGGER.info("[INIT] Configuration de la connexion vers MongDB : [{}]", mongoURILog);
		return new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(mongoURI)));
	}

}
