/**
 * 
 */
package com.terrier.finances.gestion.services.parametrages.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;
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
@Api(protocols="https", value="Parametrages", tags={"Parametrages"},
		consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
public class ParametragesAPIController extends AbstractAPIController {



	@Autowired
	private ParametragesService paramsServices;
	
	/**
	 * @return la liste des catégories d'opérations
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Liste des catégories d'opérations")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@GetMapping(value=BudgetApiUrlEnum.PARAMS_CATEGORIES)
	public @ResponseBody ResponseEntity<List<CategorieOperation>> getCategories() throws DataNotFoundException{
		
		List<CategorieOperation> listeCategories = paramsServices.getCategories();
		logger.info("Chargement des {} Categories", listeCategories != null ? listeCategories.size() : "-1");
		return getEntities(listeCategories);
	}

	/**
	 * Liste des clients HTTPS nécessaires vers les autres µS
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<AbstractHTTPClient> getHTTPClients() {
		return new ArrayList<>();
	}
}
