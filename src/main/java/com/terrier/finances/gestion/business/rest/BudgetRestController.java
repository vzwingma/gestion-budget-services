/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.business.statut.StatusApplicationService;
import com.terrier.finances.gestion.business.statut.objects.StatutDependencyObject;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value="/rest")
public class BudgetRestController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetRestController.class);

	
	@Autowired
	private StatusApplicationService statusApplicationService;

	
	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/statut", method=RequestMethod.GET)
	public StatutDependencyObject ping(){
		LOGGER.info("Appel statut : {}", this.statusApplicationService.getStatutApplication());
		return this.statusApplicationService.getStatutApplication();
	}
	
	
}
