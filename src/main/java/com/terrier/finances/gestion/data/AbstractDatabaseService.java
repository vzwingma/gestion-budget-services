/**
 * 
 */
package com.terrier.finances.gestion.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * DataServices
 * @author vzwingma
 *
 */
public abstract class AbstractDatabaseService {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDatabaseService.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	

	/**
	 * Constructeur permettant de définir les composants utilisés en DATA
	 */
	public AbstractDatabaseService(){
		LOGGER.info("[INIT] Service {}", this.getClass());
		
	}
	
	/**
	 * @return opérations MongoDB
	 */
	public MongoOperations getMongoOperation(){
		return (MongoOperations) mongoTemplate;
	}
}
