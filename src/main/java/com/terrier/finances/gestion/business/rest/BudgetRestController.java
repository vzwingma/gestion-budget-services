/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.data.DepensesDatabaseService;

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
	private DepensesDatabaseService service;

	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping(){
		LOGGER.info("Appel ping du service");
		return "L'application est démarrée";
	}
	
	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/migrate", method=RequestMethod.GET)
	public List<String> migrate(){
		LOGGER.info("Appel migration du service du service");
		return service.migrationBDD();
	}
}
