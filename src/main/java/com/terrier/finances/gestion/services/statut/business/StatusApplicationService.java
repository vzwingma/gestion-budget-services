/**
 * 
 */
package com.terrier.finances.gestion.services.statut.business;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.services.communs.abstrait.AbstractBusinessService;
import com.terrier.finances.gestion.services.statut.model.DependencyName;
import com.terrier.finances.gestion.services.statut.model.StatutDependencyObject;
import com.terrier.finances.gestion.services.statut.model.StatutStateEnum;

/**
 * Retourne le statut de l'application
 * @author vzwingma
 *
 */
@Service
public class StatusApplicationService extends AbstractBusinessService {

	// Statut de l'application
	private StatutDependencyObject statutApplication;
	
	@PostConstruct
	public void initApplication(){
		this.statutApplication = new StatutDependencyObject(DependencyName.APPLICATION);
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.OK);
		this.statutApplication.addDependency(DependencyName.DATABASE, DependencyName.APPLICATION);
		this.statutApplication.addDependency(DependencyName.REST_SERVICE, DependencyName.APPLICATION);
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
	public StatutDependencyObject getStatutApplication() {


		return statutApplication;
	}
	
	
	@PreDestroy
	public void stopApplication(){
		this.statutApplication.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.FATAL);
	}
}
