/**
 * 
 */
package com.terrier.finances.gestion.services.statut.business;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.admin.model.DependencyName;
import com.terrier.finances.gestion.communs.admin.model.StatutDependencyAPIObject;
import com.terrier.finances.gestion.communs.admin.model.StatutStateEnum;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;

/**
 * Retourne le statut de l'application
 * @author vzwingma
 *
 */
@Service
public class StatusApplicationService extends AbstractBusinessService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatusApplicationService.class);

	
	// Statut de l'application
	private StatutDependencyAPIObject statutApplication;
	
	@PostConstruct
	public void initApplication(){
		this.statutApplication = new StatutDependencyAPIObject(DependencyName.APPLICATION);
		this.statutApplication.setVersion(this.getVersion());
		this.statutApplication.setDescription("Services Budget v" + this.getVersion() + " [" + this.getBuildTime() + "]");
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.OK);
		this.statutApplication.addDependency(DependencyName.DATABASE, DependencyName.APPLICATION, "Base de données Mongo");
		this.statutApplication.addDependency(DependencyName.REST_SERVICE, DependencyName.APPLICATION, "API");
		
		LOGGER.info("[INIT] Démarrage de l'application Services v{} [{}]", this.getVersion(), this.getBuildTime());
	}

	
	/**
	 * Mise à jour de la dépendance
	 * @param nomDependance nom de la dépendance
	 * @param statut statut de la dépendance
	 */
	public void updateDependencyStatut(DependencyName nomDependance, StatutStateEnum statut){
		this.statutApplication.updateStatusModule(nomDependance, statut);
	}

	/**
	 * @return the statutApplication
	 */
	public StatutDependencyAPIObject getStatutApplication() {
		return statutApplication;
	}
	
	
	@PreDestroy
	public void stopApplication(){
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.FATAL);
	}
}
