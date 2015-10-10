/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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


	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping(){
		LOGGER.info("Appel ping du service");
		return "L'application est démarrée";
	}
}
