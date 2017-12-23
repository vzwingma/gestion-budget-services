/**
 * 
 */
package com.terrier.finances.gestion.data.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
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
	 * @throws Exception
	 */
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {

		String db = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_DB);
		String host = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_HOST);
		String username = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_USERNAME);
		String password = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_PWD);
		int port =  getIntgerEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_PORT);

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
	private String getStringEnvVar(MongoDBCOnfigEnum cle){
		return System.getenv(cle.name());
	}

	/**
	 * Retourne la valeur int de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	private Integer getIntgerEnvVar(MongoDBCOnfigEnum cle){
		String env = System.getenv(cle.name());
		try{
			return Integer.parseInt(env);
		}
		catch(NumberFormatException e){
			LOGGER.error("La clé {} n'est pas un nombre. La valeur devient nulle ", cle.name(), e);
			return null;
		}
	}
}
