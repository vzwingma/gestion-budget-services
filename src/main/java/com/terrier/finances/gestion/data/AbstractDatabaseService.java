/**
 * 
 */
package com.terrier.finances.gestion.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.business.statut.StatusApplicationService;
import com.terrier.finances.gestion.business.statut.objects.DependencyName;
import com.terrier.finances.gestion.business.statut.objects.StatutStateEnum;

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
	
	@Autowired
	private StatusApplicationService statutApplicationService;
	

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
		updateMongoStatus();
		return (MongoOperations) mongoTemplate;
	}
	
	
	/**
	 * 
	 */
	private void updateMongoStatus(){
		LOGGER.info("{} : Update Mongo Statut : {}", this.getClass(), mongoTemplate.getDb().getStats());
		StatutStateEnum statutDB = mongoTemplate.getDb().getStats() != null ?
				mongoTemplate.getDb().getStats().ok() ? StatutStateEnum.OK : StatutStateEnum.FATAL
						: StatutStateEnum.INCONNU;
		LOGGER.debug("Statut DB : {} -> {}", mongoTemplate.getDb().getStats(), statutDB);		
		
		statutApplicationService.updateDependencyStatut(DependencyName.DATABASE, statutDB);
	}
}
