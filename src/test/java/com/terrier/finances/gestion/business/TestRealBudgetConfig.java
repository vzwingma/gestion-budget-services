package com.terrier.finances.gestion.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


@Configuration
public class TestRealBudgetConfig {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestRealBudgetConfig.class);
	

	private MongoTemplate getMongoOperationDev() throws Exception{
		ServerAddress serveur = new ServerAddress("192.168.99.100", 27017);
		LOGGER.info("*** Configuration de la connexion vers MongDB : {} ***", serveur);
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(serveur), "local"));
		return mongoTemplate;
	}
	

	/**
	 * @return template Mongo
	 * @throws Exception erreur lors de la connexion
	 */ 
	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = getMongoOperationDev();
		try{
			LOGGER.info("Connexion à l'instance [{}] : OK", mongoTemplate.getDb().getName());
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la connexion à la BDD", e);
		}
		return mongoTemplate;
	}
	
}
