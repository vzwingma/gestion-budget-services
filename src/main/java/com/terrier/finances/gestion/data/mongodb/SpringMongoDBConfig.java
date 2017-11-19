/**
 * 
 */
package com.terrier.finances.gestion.data.mongodb;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

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
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {
		validateProperty(obj, "Invalid JNDI object reference");

	    String db = null;
	    String host = null;
	    String username = null;
	    String password = null;
	    int port = 0;

	    Reference ref = (Reference) obj;
	    Enumeration<RefAddr> props = ref.getAll();
	    while (props.hasMoreElements()) {
	        RefAddr addr = (RefAddr) props.nextElement();
	        String propName = addr.getType();
	        String propValue = (String) addr.getContent();
	        // LOGGER.info("JNDI Prop : [{}] : [{}]", propName, propValue);
	        if (propName.equals("db")) {
	            db = propValue;
	        } else if (propName.equals("host")) {
	            host = propValue;
	        } else if (propName.equals("username")) {
	            username = propValue;
	        } else if (propName.equals("password")) {
	            password = propValue;
	        } else if (propName.equals("port")) {
	            try {
	                port = Integer.parseInt(propValue);
	            } catch (NumberFormatException e) {
	            	LOGGER.error("Erreur dans le traitement de {}", propValue);
	                throw new NamingException("Invalid port value " + propValue);
	            }
	        }
	    }
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
	    ServerAddress serveur = new ServerAddress(host, port);
	    
	    String mongoURI = new StringBuilder("mongodb://").append(username).append(":").append(password).append("@").append(host).append(":").append(port).append("/").append(db).toString();
	    String mongoURILog = new StringBuilder("mongodb://").append(username).append("@").append(host).append(":").append(port).append("/").append(db).toString();
	    
	    
		LOGGER.info("[INIT] Configuration de la connexion vers MongDB : [{}]", mongoURILog);
		if(username != null && username.length() > 0  && password != null){
			return new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(mongoURI)));
		}
		else{
			LOGGER.warn("[INIT] Configuration de dévelopement");
			return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(serveur), db));
		}
	}
	
	
	/**
	 * Validate internal Object properties
	 * 
	 * @param property
	 * @param errorMessage
	 * @throws NamingException
	 */
	private void validateProperty(Object property, String errorMessage)
	        throws NamingException {
		LOGGER.info("validate property : {}", property);
	    if (property == null) {
	        throw new NamingException(errorMessage);
	    }
	}
}
