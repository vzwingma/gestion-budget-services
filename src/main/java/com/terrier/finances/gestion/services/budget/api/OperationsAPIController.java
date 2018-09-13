package com.terrier.finances.gestion.services.budget.api;

import java.time.Month;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.business.OperationsService;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * API Budget/Operations
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.BUDGET_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Operations", tags={"Operations"})
public class OperationsAPIController extends AbstractAPIController {


	@Autowired
	private OperationsService operationService;

	/**
	 * Retour le budget d'un utilisateur
	 * @param idUtilisateur id de l'utilisateur
	 * @param idCompte id du compte
	 * @param mois mois du budget
	 * @param annee du budget
	 * @return budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Budget mensuel d'un compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="mois", required=true, value="No de mois", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="annee", required=true, value="No de l'année", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="query")			
	})	
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_QUERY)
	public  @ResponseBody ResponseEntity<BudgetMensuel> getBudget(
			@RequestParam("idCompte") String idCompte, 
			@RequestParam("mois") Integer mois, 
			@RequestParam("annee") Integer annee, 
			@RequestParam("idUtilisateur") String idUtilisateur) throws BudgetNotFoundException, DataNotFoundException {
		LOGGER.info("[API][idUser={}][idCompte={}] getBudget {}/{}", idUtilisateur, idCompte, mois, annee);

		if(mois != null && annee != null){
			try{
				Month month = Month.of(mois);
				return getEntity(operationService.chargerBudgetMensuel(idUtilisateur, idCompte, month, annee));
			}
			catch(NumberFormatException e){
				throw new DataNotFoundException("Erreur dans les paramètres en entrée");
			}
		}
		throw new BudgetNotFoundException("Erreur dans les paramètres en entrée");
	}



	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param budget budget
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour d'un budget")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget mis à jour"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
	})	
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> updateBudget(@PathVariable("idBudget") String idBudget, @PathVariable("idUtilisateur") String idUtilisateur, @RequestBody BudgetMensuel budget) throws DataNotFoundException{
		LOGGER.info("[API][idBudget={}] updateBudget",idBudget);
		if(budget != null && idBudget != null && idBudget.equals(budget.getId())){
			BudgetMensuel budgetUpdated = operationService.calculEtSauvegardeBudget(budget, idUtilisateur);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget");
	}


	/**
	 * Retourne le statut du budget
	 * @param idBudget id du compte
	 * @return statut du budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Etat d'un budget mensuel : 2 paramètres {actif} ou {uptodate}")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget actif"),
			@ApiResponse(code = 204, message = "Budget inactif"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="actif", required=false, value="Etat actif du compte", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="uptodateto", required=false, value="Activité du budget par rapport à la date en paramètres (en ms)", paramType="query"),
	})	
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_ETAT)
	public ResponseEntity<Boolean> isBudgetActif(
			@PathVariable("idBudget") String idBudget, 
			@PathVariable("idUtilisateur") String idUtilisateur,
			@RequestParam(value="actif", required=false, defaultValue="false") Boolean isActif,  @RequestParam(value="uptodateto", required=false) Long uptodateto) throws BudgetNotFoundException {

		LOGGER.info("[API][idBudget={}] actif ? : {}, uptodateto ? {}",idBudget, isActif, uptodateto );

		if(isActif){
			boolean isBudgetActif = operationService.isBudgetMensuelActif(idBudget);
			LOGGER.info("[API][idBudget={}] isActif ? : {}",idBudget, isBudgetActif );
			if(isBudgetActif){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.noContent().build();
			}
		}
		else if(uptodateto != null){
			boolean isUpToDate = operationService.isBudgetUpToDate(idBudget, new Date(uptodateto), idUtilisateur);
			LOGGER.info("[API][idBudget={}] isUpToDateto {} ? : {}",idBudget, uptodateto, isUpToDate );
			if(isUpToDate){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.noContent().build();
			}
		}
		return ResponseEntity.notFound().build();
	}



	/**
	 * Met à jour le statut du budget
	 * @param idBudget id du compte
	 * @return statut du budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Etat d'un budget mensuel : 2 paramètres {actif} ou {uptodate}")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 500, message = "Opération en échec")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="actif", required=false, value="Etat actif du compte", paramType="query"),
	})	
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_ETAT)
	public ResponseEntity<BudgetMensuel> setBudgetActif(
			@PathVariable("idBudget") String idBudget, 
			@PathVariable("idUtilisateur") String idUtilisateur,
			@RequestParam(value="actif") Boolean setActif) throws BudgetNotFoundException {

		LOGGER.info("[API][idBudget={}] set Actif : {}",idBudget, setActif );
		BudgetMensuel budgetActif = operationService.setBudgetActif(idBudget, setActif, idUtilisateur);
		return getEntity(budgetActif);
	}
}
