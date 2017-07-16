/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.business.statut.StatusApplicationService;
import com.terrier.finances.gestion.business.statut.objects.DependencyName;
import com.terrier.finances.gestion.business.statut.objects.StatutDependencyObject;
import com.terrier.finances.gestion.business.statut.objects.StatutStateEnum;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value="/rest")
public class StatutRestController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatutRestController.class);

	
	@Autowired
	private StatusApplicationService statusApplicationService;

	@PostConstruct
	public void initApplication(){
		LOGGER.info("initApplication");
		statusApplicationService.updateDependencyStatut(DependencyName.REST_SERVICE, StatutStateEnum.OK);
	}

	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/statut", method=RequestMethod.GET)
	public StatutDependencyObject ping(){
		LOGGER.info("Appel statut : {}", this.statusApplicationService.getStatutApplication());
		return this.statusApplicationService.getStatutApplication();
	}
	
	
	@PreDestroy
	public void stopApplication(){
		LOGGER.info("stopApplication");
		statusApplicationService.updateDependencyStatut(DependencyName.REST_SERVICE, StatutStateEnum.DEGRADE);
	}
}
