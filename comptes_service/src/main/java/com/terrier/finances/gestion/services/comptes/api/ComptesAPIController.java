/**
 * 
 */
package com.terrier.finances.gestion.services.comptes.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * API Comptes
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.COMPTES_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Comptes", tags={"Comptes"})
public class ComptesAPIController extends AbstractAPIController {


	@Autowired
	private ComptesService comptesService;


	/**
	 * Retour la liste des comptes
	 * @param idUtilisateur id de l'utilisateur
	 * @return liste des comptes de l'utilisateur
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Comptes d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être authentifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_LIST)
	public @ResponseBody ResponseEntity<List<CompteBancaire>> getComptesUtilisateur() throws DataNotFoundException{
		logger.info("getComptes");
		return getEntities(comptesService.getComptesUtilisateur(getIdProprietaire()));
	}

	/**
	 * Retourne le compte
	 * @param idCompte id du compte
	 * @return compte associé
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être authentifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="path")
	})	
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_ID)
	public @ResponseBody ResponseEntity<CompteBancaire> getCompteUtilisateur(@PathVariable("idCompte") String idCompte) throws DataNotFoundException{
		logger.info("[idCompte={}] getCompte", idCompte);
		return getEntity(comptesService.getCompteById(idCompte, getIdProprietaire()));
	}




	@Override
	public List<AbstractHTTPClient<?>> getHTTPClients() {
		return new ArrayList<>();
	}
}
