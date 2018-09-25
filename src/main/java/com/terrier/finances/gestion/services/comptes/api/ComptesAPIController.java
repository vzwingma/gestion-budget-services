/**
 * 
 */
package com.terrier.finances.gestion.services.comptes.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.comptes.model.api.IntervallesCompteAPIObject;
import com.terrier.finances.gestion.communs.operations.model.api.LibellesOperationsAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

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
	 * @throws UserNotAuthorizedException 
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Comptes d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être authentifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_LIST)
	public @ResponseBody ResponseEntity<List<CompteBancaire>> getComptesUtilisateur(@RequestAttribute("userSession") UserBusinessSession userSession) throws DataNotFoundException, UserNotAuthorizedException{
		logger.info("getComptes");
		return getEntities(comptesService.getComptesUtilisateur(userSession.getUtilisateur().getId()));
	}

	/**
	 * Retourne le compte
	 * @param idCompte id du compte
	 * @return compte associé
	 * @throws DataNotFoundException erreur données non trouvées
	 * @throws UserNotAuthorizedException 
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
	public @ResponseBody ResponseEntity<CompteBancaire> getCompteUtilisateur(@PathVariable("idCompte") String idCompte, @RequestAttribute("userSession") UserBusinessSession userSession) throws DataNotFoundException, UserNotAuthorizedException{
		logger.info("[idCompte={}] getCompte", idCompte);
		return getEntity(comptesService.getCompteById(idCompte, userSession.getUtilisateur().getId()));
	}


	/**
	 * Retourne le compte
	 * @param idCompte id du compte
	 * @return compte associé
	 * @throws DataNotFoundException erreur données non trouvées
	 * @throws UserNotAuthorizedException 
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Intervalles des budgets pour un compte")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être authentifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="path"),
	})	
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_INTERVALLES)
	public @ResponseBody ResponseEntity<IntervallesCompteAPIObject> getIntervallesBudgetsCompte(@PathVariable("idCompte") String idCompte,  @RequestAttribute("userSession") UserBusinessSession userSession) throws DataNotFoundException, UserNotAuthorizedException{
		logger.info("[idCompte={}] getIntervallesBudgetsCompte", idCompte);

		LocalDate[] intervalles = comptesService.getIntervallesBudgets(idCompte);
		if(intervalles != null && intervalles.length >= 2){
			IntervallesCompteAPIObject intervallesAPI = new IntervallesCompteAPIObject();
			intervallesAPI.setDatePremierBudget(BudgetDateTimeUtils.getLongFromLocalDate(intervalles[0]));
			intervallesAPI.setDateDernierBudget(BudgetDateTimeUtils.getLongFromLocalDate(intervalles[1]));
			return getEntity(intervallesAPI);	
		}
		throw new DataNotFoundException("Impossible de trouver l'intervalle de budget pour le compte " + idCompte);
	}


	/**
	 * Liste des libellés des opérations d'un compte (tout mois confondu)
	 * @param idUtilisateur id Utilisateur
	 * @param idCompte idCompte
	 * @param annee année
	 * @throws UserNotAuthorizedException 
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Libelles des opérations des budgets de l'année pour un compte")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 204, message = "Aucune donnée"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être authentifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="annee", required=true, value="Année", paramType="query"),
	})		
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_OPERATIONS_LIBELLES)
	public  @ResponseBody ResponseEntity<LibellesOperationsAPIObject> getLibellesOperations( @RequestAttribute("userSession") UserBusinessSession userSession, @PathVariable("idCompte") String idCompte, @RequestParam("annee") Integer annee) throws UserNotAuthorizedException{
		logger.info("[idCompte={}] get Libellés Opérations : {}", annee);
		Set<String> libelles = comptesService.getLibellesOperations(userSession.getUtilisateur().getId(), idCompte, annee);
		if(libelles != null && !libelles.isEmpty()){
			LibellesOperationsAPIObject libellesO = new LibellesOperationsAPIObject();
			libellesO.setIdCompte(idCompte);
			libellesO.setLibellesOperations(libelles);
			return getEntity(libellesO);
		}
		return ResponseEntity.noContent().build();
	}

}
