/**
 * 
 */
package com.terrier.finances.gestion.services.communs.spi.mongodb;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

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
	 * @return Factory de connexion BDD
	 */
	@Bean
	public MongoDatabaseFactory mongoDbFactory() {

		String db = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_DB);
		String host = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_HOST);
		String username = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_USERNAME);
		String password = getStringEnvVar(MongoDBConfigEnum.MONGODB_CONFIG_PWD);

		//create mongo template
		String mongoURI = new StringBuilder("mongodb+srv://")
				.append(username).append(":").append(password)
				.append("@").append(host).append("/").append(db)
				.append("?retryWrites=true&w=majority")
				.toString();

		if(LOGGER.isInfoEnabled()) {
			String mongoURItoLog = mongoURI.replaceAll(":(.)*@", "*");
			LOGGER.info("[INIT] Configuration de la connexion vers MongoDB Atlas : [{}]", mongoURItoLog);
		}

		return new SimpleMongoClientDatabaseFactory(mongoURI);
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
