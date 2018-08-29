/**
 * 
 */
package com.terrier.finances.gestion.services.statut.api;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.services.statut.business.StatusApplicationService;
import com.terrier.finances.gestion.services.statut.model.DependencyName;
import com.terrier.finances.gestion.services.statut.model.StatutDependencyObject;
import com.terrier.finances.gestion.services.statut.model.StatutStateEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value="/rest")
@Api(consumes="application/json", protocols="https", value="Administration", tags={"Administration"})
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
	@ApiOperation(httpMethod="GET", produces="application/json", protocols="https", value="Statut de l'opération", response=StatutDependencyObject.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Statut de l'application"),
            @ApiResponse(code = 401, message = "L'opération doit être identifiée"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    }) 
	@GetMapping(value="/statut")
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
