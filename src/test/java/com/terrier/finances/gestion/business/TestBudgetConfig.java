package com.terrier.finances.gestion.business;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.terrier.finances.gestion.business.BudgetConfig;
import com.terrier.finances.gestion.data.mongodb.SpringMongoDBConfig;

@Configuration
public class TestBudgetConfig {

	@Autowired
	private SpringMongoDBConfig mongoDBConfig;
	

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetConfig.class);
	/**
	 * @return Factory MongDB
	 * @throws Exception erreur lors de la connexion
	 */
	public @Bean MongoDbFactory mongoDbFactory() throws Exception {

		ServerAddress serveur = new ServerAddress(mongoDBConfig.getIpServer(), mongoDBConfig.getPortServer());
		LOGGER.info("*** Configuration de la connexion vers MongDB : {} ***", serveur);
		if(mongoDBConfig.getLoginMongoDB() != null && mongoDBConfig.getLoginMongoDB().length() > 0  && mongoDBConfig.getPwdMongoDB() != null){
			LOGGER.info(" User : [{}]", mongoDBConfig.getLoginMongoDB());
			MongoCredential credential = MongoCredential.createMongoCRCredential(mongoDBConfig.getLoginMongoDB(), "admin", mongoDBConfig.getPwdMongoDB().toCharArray());
			return new SimpleMongoDbFactory(new MongoClient(serveur, Arrays.asList(credential)), mongoDBConfig.getNomMongoDB());
		}
		else{
			LOGGER.warn("Configuration de dévelopement");
			return new SimpleMongoDbFactory(new MongoClient(serveur), mongoDBConfig.getNomMongoDB());
		}

	}

	/**
	 * @return template Mongo
	 * @throws Exception erreur lors de la connexion
	 */ 
	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		try{
			LOGGER.info("Connexion à la BDD {}", mongoTemplate.getDb().getName());
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la connexion à la BDD", e);
		}
		return mongoTemplate;
	}
}
