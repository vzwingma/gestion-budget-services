/**
 * 
 */
package com.terrier.finances.gestion.data.mongodb;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientURI;

/**
 * Configuration de connexion à la BDD via JNDI
 * @author vzwingma
 *
 */
public class SpringMongoDBConfig implements ObjectFactory {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoDBConfig.class);


	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 * 
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
		String db = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_DB);
		String host = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_HOST);
		String username = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_USERNAME);
		String password = getStringEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_PWD);
		int port =  getIntgerEnvVar(MongoDBCOnfigEnum.MONGODB_CONFIG_PORT);
		return createMondoDBConfig(host, port, username, password, db);

	}


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
	private MongoTemplate createMondoDBConfig(String host, int port, String username, String password, String db) throws Exception{
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
		return System.getenv(cle.getKey());
	}
	
	/**
	 * Retourne la valeur int de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	private Integer getIntgerEnvVar(MongoDBCOnfigEnum cle){
		String env = System.getenv(cle.getKey());
		try{
			return Integer.parseInt(env);
		}
		catch(NumberFormatException e){
			LOGGER.error("La clé {} n'est pas un nombre. La valeur devient nulle ", cle.getKey(), e);
			return null;
		}
	}
}
