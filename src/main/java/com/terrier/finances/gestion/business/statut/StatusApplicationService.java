/**
 * 
 */
package com.terrier.finances.gestion.business.statut;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.CommandResult;
import com.terrier.finances.gestion.business.statut.objects.DependencyName;
import com.terrier.finances.gestion.business.statut.objects.StatutDependencyObject;
import com.terrier.finances.gestion.business.statut.objects.StatutStateEnum;
import com.terrier.finances.gestion.data.AbstractDatabaseService;

/**
 * Retourne le statut de l'application
 * @author vzwingma
 *
 */
@Service
public class StatusApplicationService {

	// Statut de l'application
	private StatutDependencyObject statutApplication;
	

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatusApplicationService.class);
	
	@PostConstruct
	public void initApplication(){
		LOGGER.info("initApplication");
		this.statutApplication = new StatutDependencyObject(DependencyName.APPLICATION);
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.OK);
		this.statutApplication.addDependency(DependencyName.DATABASE, DependencyName.APPLICATION);
	}

	
	public void updateMongoStatut( CommandResult statsDatabase){
		StatutStateEnum statutDB = statsDatabase != null ?
				statsDatabase.ok() ? StatutStateEnum.OK : StatutStateEnum.FATAL
						: StatutStateEnum.INCONNU;
		LOGGER.debug("Statut DB : {} -> {}", statsDatabase, statutDB);		
		this.statutApplication.updateStatusModule(DependencyName.DATABASE, statutDB);
	}

	/**
	 * @return the statutApplication
	 */
	public StatutDependencyObject getStatutApplication() {


		return statutApplication;
	}
	
	
	@PreDestroy
	public void stopApplication(){
		LOGGER.info("stopApplication");
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.FATAL);
	}
}
