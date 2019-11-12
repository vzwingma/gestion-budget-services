/**
 * 
 */
package com.terrier.finances.gestion.services.communs.data.mongodb;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;

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
	 * @return Factory de connexion BDD
	 */
	@Bean
	public MongoDbFactory mongoDbFactory() {

		String db = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_DB);
		String host = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_HOST);
		String username = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_USERNAME);
		String password = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PWD);

		//create mongo template
		String mongoURI = new StringBuilder("mongodb+srv://")
				.append(username).append(":").append(password)
				.append("@").append(host).append("/").append(db)
				.toString();
		// Mongo Options
		Builder options = MongoClientOptions.builder()
				.writeConcern(WriteConcern.MAJORITY)
				.retryWrites(true)
				.sslEnabled(true)
				.sslInvalidHostNameAllowed(true);

		if(LOGGER.isInfoEnabled()) {
			String mongoURItoLog = mongoURI.replaceAll(":(.)*@", "*");
			LOGGER.info("[INIT] Configuration de la connexion vers MongoDB Atlas : [{}]", mongoURItoLog);
		}
		return new SimpleMongoDbFactory(new MongoClientURI(mongoURI, options));
	}

	/**
	 * @return MongoOperations de connexion BDD
	 **/
	@Bean
	public MongoOperations mongoOperations() {
		return new MongoTemplate(mongoDbFactory());
	}
	/**
	 * Retourne la valeur string de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	private static String getStringEnvVar(MongoDBConfigEnum cle){
		String envVar =  StringEscapeUtils.escapeJava(System.getenv(cle.name()));
		if(envVar != null) {
			return envVar;
		}
		else if(LOGGER.isWarnEnabled()) {
			LOGGER.warn("La clé {} n'est pas définie", cle);
		}
		return null;
	}
}
