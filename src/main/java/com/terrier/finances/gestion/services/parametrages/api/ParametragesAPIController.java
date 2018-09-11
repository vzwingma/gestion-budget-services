/**
 * 
 */
package com.terrier.finances.gestion.services.parametrages.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieDepense;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.parametrages.business.ParametragesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.PARAMS_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Parametrages", tags={"Parametrages"})
public class ParametragesAPIController extends AbstractAPIController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesAPIController.class);

	@Autowired
	private ParametragesService paramsServices;
	
	/**
	 * @return la liste des catégories d'opérations
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Catégories d'opérations")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@GetMapping(value=BudgetApiUrlEnum.PARAMS_CATEGORIES)
	public @ResponseBody ResponseEntity<List<CategorieDepense>> getCategories() throws DataNotFoundException{
		LOGGER.info("[API] getCategories");
		return getEntities(paramsServices.getCategories());
	}
}
