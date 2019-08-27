package com.terrier.finances.gestion.services.budget.api;

import java.time.Month;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budget.business.OperationsService;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

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
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Opérations", tags={"Opérations"})
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
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Recherche d'un budget mensuel pour un compte d'un utilisateur", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="mois", required=true, value="No de mois", paramType="query"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="annee", required=true, value="No de l'année", paramType="query")
	})	

	@GetMapping(value=BudgetApiUrlEnum.BUDGET_QUERY)
	public  @ResponseBody ResponseEntity<BudgetMensuel> getBudget(
			@RequestParam("idCompte") String idCompte, 
			@RequestParam("mois") Integer mois, 
			@RequestParam("annee") Integer annee, 
			@RequestAttribute("userSession") UserBusinessSession userSession) throws UserNotAuthorizedException, BudgetNotFoundException, DataNotFoundException {
		logger.info("[idCompte={}] getBudget {}/{}", idCompte, mois, annee);

		if(mois != null && annee != null){
			try{
				Month month = Month.of(mois);
				return getEntity(operationService.chargerBudgetMensuel(idCompte, month, annee, userSession));
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
	 * @throws BudgetNotFoundException 
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Chargement d'un budget", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget chargé"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
	})	

	@GetMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> getBudget(
			@PathVariable("idBudget") String idBudget, 
			@RequestAttribute("userSession") UserBusinessSession userSession) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException{

		logger.info("[idBudget={}] chargeBudget", idBudget);
		if(idBudget != null){
			return getEntity(operationService.chargerBudgetMensuel(idBudget, userSession));
		}
		throw new DataNotFoundException("Impossible de charger le budget");
	}


	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param budget budget
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 */
	@ApiOperation(httpMethod="DELETE",protocols="HTTPS", value="Réinitialisation d'un budget", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget réinitialisé"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 405, message = "Compte clos. Impossible de réinitialiser le budget")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
	})	

	@DeleteMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> reinitializeBudget(@PathVariable("idBudget") String idBudget, @RequestAttribute("userSession") UserBusinessSession userSession) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		logger.info("[idBudget={}] reinitialisation", idBudget);
		if(idBudget != null){
			BudgetMensuel budgetUpdated = operationService.reinitialiserBudgetMensuel(idBudget, userSession);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de réinitialiser le budget");
	}
	/**
	 * Retourne le statut du budget
	 * @param idBudget id du compte
	 * @return statut du budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Retourne l'un des états d'un budget mensuel : {etat} ou {uptodate}", notes="{etat} : indique si le budget est ouvert ou cloturé. {uptodate} indique si le budget a été mis à jour en BDD par rapport à la date passée en paramètre", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget actif"),
			@ApiResponse(code = 204, message = "Budget inactif"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
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
			@RequestAttribute("userSession") UserBusinessSession userSession,
			@RequestParam(value="actif", required=false, defaultValue="false") Boolean isActif,  @RequestParam(value="uptodateto", required=false) Long uptodateto) throws UserNotAuthorizedException, BudgetNotFoundException {

		logger.info("[idBudget={}] actif ? : {}, uptodateto ? {}", idBudget, isActif, uptodateto );

		if(isActif){
			boolean isBudgetActif = operationService.isBudgetMensuelActif(idBudget);
			logger.info("[idBudget={}] isActif ? : {}",idBudget, isBudgetActif );
			if(isBudgetActif){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.noContent().build();
			}
		}
		else if(uptodateto != null){
			boolean isUpToDate = operationService.isBudgetUpToDate(idBudget, new Date(uptodateto));
			logger.info("[idBudget={}] isUpToDateto {} ? : {}",idBudget, uptodateto, isUpToDate );
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
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour de l'{état} d'un budget mensuel (ouvert/cloturé)", notes="{etat} : indique si le budget est ouvert ou cloturé.", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 500, message = "Opération en échec")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="actif", required=false, value="Etat actif du compte", paramType="query"),
	})	

	@PostMapping(value=BudgetApiUrlEnum.BUDGET_ETAT)
	public ResponseEntity<BudgetMensuel> setBudgetActif(
			@PathVariable("idBudget") String idBudget,
			@RequestAttribute("userSession") UserBusinessSession userSession,
			@RequestParam(value="actif") Boolean setActif) throws UserNotAuthorizedException, BudgetNotFoundException {

		logger.info("[idBudget={}] set Actif : {}", idBudget, setActif );
		BudgetMensuel budgetActif = operationService.setBudgetActif(idBudget, setActif, userSession);
		return getEntity(budgetActif);
	}



	/**
	 * Met à jour le flag de l'opération comme dernière opération réalisée
	 * @param idBudget id du compte
	 * @return résultat de l'action
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Met à jour le flag de l'opération comme dernière opération réalisée", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget mis à jour"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="idOperation", required=true, value="Id de l'opération", paramType="path"),
	})	

	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE)
	public ResponseEntity<Boolean> setAsDerniereOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation, 
			@RequestAttribute("userSession") UserBusinessSession userSession) throws UserNotAuthorizedException, BudgetNotFoundException {

		logger.info("[idBudget={}][idOperation={}] setAsDerniereOperation", idBudget, idOperation);
		boolean resultat = operationService.setLigneAsDerniereOperation(idBudget, idOperation, userSession);
		if(resultat){
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.noContent().build();
		}
	}


	/**
	 * Mise à jour d'une opération
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param operation opération à mettre à jour
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException 
	 * @throws CompteClosedException 
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour d'une opération", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération mise à jour"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),	
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 423, message = "Compte clos")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idOperation", required=true, value="Id de l'opération", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=LigneOperation.class, name="operation", required=true, value="Operation", paramType="body"),
	})	
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION)
	public @ResponseBody ResponseEntity<BudgetMensuel> createOrUpdateOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation,
			@RequestAttribute("userSession") UserBusinessSession userSession, 
			@RequestBody LigneOperation operation) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		logger.info("[idBudget={}][idOperation={}] createOrUpdateOperation", idBudget, idOperation);
		if(operation != null && idBudget != null && userSession != null){
			operation.setId(idOperation);
			completeCategoriesOnOperation(operation);
			BudgetMensuel budgetUpdated = operationService.createOrUpdateOperation(idBudget, operation, userSession);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget " + idBudget + " avec l'opération " + operation);
	}

	

	/**
	 * Mise à jour d'une opération
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param operation opération à mettre à jour
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException 
	 * @throws CompteClosedException 
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour d'une opération Intercomptes", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération mise à jour"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),	
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 423, message = "Compte clos")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idOperation", required=true, value="Id de l'opération", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte destination", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=LigneOperation.class, name="operation", required=true, value="Operation", paramType="body"),
	})	

	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE)
	public @ResponseBody ResponseEntity<BudgetMensuel> createOperationIntercomptes(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation,
			@PathVariable("idCompte") String idCompte,
			@RequestAttribute("userSession") UserBusinessSession userSession, 
			@RequestBody LigneOperation operation) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		logger.info("[idBudget={}][idOperation={}] createOperation InterCompte [{}]", idBudget, idOperation, idCompte);
		if(operation != null && idBudget != null){
			operation.setId(idOperation);
			completeCategoriesOnOperation(operation);
			BudgetMensuel budgetUpdated = operationService.createOperationIntercompte(idBudget, operation, idCompte, userSession);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget " + idBudget + " avec l'opération " + operation);
	}

	/**
	 * Mise à jour d'une opération
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param operation opération à mettre à jour
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException 
	 * @throws CompteClosedException 
	 */
	@ApiOperation(httpMethod="DELETE",protocols="HTTPS", value="Suppression d'une opération", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération supprimée"),
			@ApiResponse(code = 204, message = "Opération supprimée"),			
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),	
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 405, message = "Compte clos")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idOperation", required=true, value="Id Opération", paramType="path"),
	})	

	@DeleteMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION)
	public @ResponseBody ResponseEntity<BudgetMensuel> deleteOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation,
			@RequestAttribute("userSession") UserBusinessSession userSession
		) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		
		if(idOperation != null && idBudget != null && userSession != null){
			logger.info("[idBudget={}][idOperation={}] deleteOperation", idBudget, idOperation);
			BudgetMensuel budgetUpdated = operationService.deleteOperation(idBudget, idOperation, userSession);
			if(budgetUpdated != null) {
				return getEntity(budgetUpdated);
			}
			else {
				return ResponseEntity.notFound().build();
			}
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget " + idBudget + " avec l'opération " + idOperation);
	}
	
	/**
	 * Réinjection des catégories dans les opérations du budget
	 * @param budget
	 */
	private void completeCategoriesOnOperation(LigneOperation operation){
		List<CategorieOperation> categories = operationService.getServiceParams().getCategories();
		try {
			CategorieOperation catFound = BudgetDataUtils.getCategorieById(operation.getIdSsCategorie(), categories);
			if(catFound != null) {
				operation.setSsCategorie(catFound);
				return;
			}
		}
		catch (Exception e) {
			logger.warn("Impossible de retrouver la sous catégorie : {}", operation.getIdSsCategorie(), e);
		}
		logger.warn("Impossible de retrouver la sous catégorie : {} parmi la liste ci dessous. Le fonctionnement peut être incorrect. \n {}", operation.getIdSsCategorie(), categories);

	}
}

