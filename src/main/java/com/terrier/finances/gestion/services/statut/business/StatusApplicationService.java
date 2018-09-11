/**
 * 
 */
package com.terrier.finances.gestion.services.statut.business;

import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.utils.data.DataUtils;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.statut.api.StatutDependencyAPIObject;
import com.terrier.finances.gestion.services.statut.model.DependencyName;
import com.terrier.finances.gestion.services.statut.model.StatutStateEnum;

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
	/**
	 * Info de version de l'application 
	 */
	private String version;
	private String buildTime;
	
	// Statut de l'application
	private StatutDependencyAPIObject statutApplication;
	
	@PostConstruct
	public void initApplication(){
		this.statutApplication = new StatutDependencyAPIObject(DependencyName.APPLICATION);
		this.statutApplication.setDescription("Services Budget v" + this.version + " [" + this.buildTime + "]");
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.OK);
		this.statutApplication.addDependency(DependencyName.DATABASE, DependencyName.APPLICATION, "Base de données Mongo");
		this.statutApplication.addDependency(DependencyName.REST_SERVICE, DependencyName.APPLICATION, "API");
		
		LOGGER.info("[INIT] Démarrage de l'application Services v{} [{}]", this.version, this.buildTime);
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@Value("${budget.version:CURRENT}")
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the buildTime
	 */
	public String getBuildTime() {
		return buildTime;
	}

	/**
	 * @param utcBuildTime the buildTime to set (en UTC)
	 */
	@Value("${budget.build.time:NOW}")
	public void setBuildTime(String utcBuildTime) {
		try {
			this.buildTime = DataUtils.getUtcToLocalTime(utcBuildTime);
		} catch (ParseException e) {
			this.buildTime = utcBuildTime;
		}
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
