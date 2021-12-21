/**
 * 
 */
package com.terrier.finances.gestion.services.parametrages.api;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controleur REST -
 * Adapteur du port {@link IParametrageRequest}
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.PARAMS_BASE)
public class ParametragesAPIController extends AbstractAPIController {

	@Autowired
	private IParametrageRequest paramsServices;
	
	/**
	 * @return la liste des catégories d'opérations
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@Operation(method = "GET", summary = "Liste des catégories d'opérations", tags={"Paramétrages"})
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = CategorieOperation.class)) }),
			@ApiResponse(responseCode = "401", description = "L'action n'est pas authentifiée"),
            @ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @ApiResponse(responseCode = "404", description = "Session introuvable")
    })
	@GetMapping(value=BudgetApiUrlEnum.PARAMS_CATEGORIES)
	public @ResponseBody ResponseEntity<List<CategorieOperation>> getCategories() throws DataNotFoundException{
		
		List<CategorieOperation> listeCategories = paramsServices.getCategories();
		logger.info("Chargement des {} Categories", listeCategories != null ? listeCategories.size() : "-1");
		return getEntities(listeCategories);
	}
}
