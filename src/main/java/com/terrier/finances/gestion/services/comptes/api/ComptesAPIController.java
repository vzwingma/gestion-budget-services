/**
 * 
 */
package com.terrier.finances.gestion.services.comptes.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.COMPTES_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Comptes", tags={"Comptes"})
public class ComptesAPIController extends AbstractAPIController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesAPIController.class);
	

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
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
	})	
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_LIST+"/{idUtilisateur}")
	public @ResponseBody ResponseEntity<List<CompteBancaire>> getComptesUtilisateur(@PathVariable("idUtilisateur") String idUtilisateur) throws DataNotFoundException{
		LOGGER.info("[API][idUser={}] getComptes", idUtilisateur);
		return getEntity(comptesService.getComptesUtilisateur(idUtilisateur));
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
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
	})	
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_ID+"/{idCompte}/{idUtilisateur}")
	public @ResponseBody ResponseEntity<CompteBancaire> getCompteUtilisateur(@PathVariable("idCompte") String idCompte, @PathVariable("idUtilisateur") String idUtilisateur) throws DataNotFoundException{
		LOGGER.info("[API][idUser={}][idCompte={}] getCompte", idUtilisateur, idCompte);
		return getEntity(comptesService.getCompteById(idCompte, idUtilisateur));
	}
}
