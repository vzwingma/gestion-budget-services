package com.terrier.finances.gestion.services.budgets.api;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.api.IntervallesCompteAPIObject;
import com.terrier.finances.gestion.communs.operations.model.api.LibellesOperationsAPIObject;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.business.ports.IComptesServiceProvider;
import com.terrier.finances.gestion.services.budgets.business.ports.IOperationsRequest;
import com.terrier.finances.gestion.services.budgets.business.ports.IParametragesServiceProvider;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.communs.business.ports.IServiceProvider;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * API Budget/Operations
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.BUDGET_BASE)
@Api(protocols="https", value="Opérations", tags={"Opérations"},
	consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
public class OperationsAPIController extends AbstractAPIController {


	@Autowired
	private IOperationsRequest operationService;

	@Autowired 
	private IParametragesServiceProvider paramClientApi;

	@Autowired
	private IComptesServiceProvider compteClientApi;

	/**
	 * Retour le budget d'un utilisateur
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
						@RequestParam("annee") Integer annee) {
		logger.info("[idCompte={}] getBudget {}/{}", idCompte, mois, annee);

		if(mois != null && annee != null){
			try{
				Month month = Month.of(mois);
				return getEntity(operationService.getBudgetMensuel(idCompte, month, annee, getIdProprietaire()));
			}
			catch(NumberFormatException e){
				return ResponseEntity.badRequest().build();
			}
			catch (BudgetNotFoundException | DataNotFoundException e) {
				logger.error("Impossible de trouver le budget demandé");
				return ResponseEntity.notFound().build();
			}
		}
		return ResponseEntity.badRequest().build();
	}



	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
	 * @return budget mis à jour
	 * @throws DataNotFoundException données introuvables
	 * @throws BudgetNotFoundException  budget introuvable
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
			@PathVariable("idBudget") String idBudget) throws DataNotFoundException, BudgetNotFoundException{

		logger.info("[idBudget={}] chargeBudget", idBudget);
		if(idBudget != null){
			try {
				return getEntity(operationService.getBudgetMensuel(idBudget, getIdProprietaire()));
			}
			catch (DataNotFoundException | BudgetNotFoundException e) {
				logger.error("[idBudget={}] Impossible de charger le budget", idBudget);
			}
		}
		return ResponseEntity.notFound().build();
	}


	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
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
	public @ResponseBody ResponseEntity<BudgetMensuel> reinitializeBudget(@PathVariable("idBudget") String idBudget) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		logger.info("[idBudget={}] reinitialisation", idBudget);
		if(idBudget != null && getIdProprietaire() != null){
			BudgetMensuel budgetUpdated = operationService.reinitialiserBudgetMensuel(idBudget, getIdProprietaire());
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
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Retourne l'état d'un budget mensuel : {etat}", notes="{etat} : indique si le budget est ouvert ou cloturé.", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget actif"),
			@ApiResponse(code = 423, message = "Budget inactif"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="actif", required=false, value="Etat actif du compte", paramType="query"),
	})	

	@GetMapping(value=BudgetApiUrlEnum.BUDGET_ETAT)
	public ResponseEntity<Boolean> isBudgetActif(
			@PathVariable("idBudget") String idBudget, 
			@RequestParam(value="actif", required=false, defaultValue="false") Boolean isActif) throws BudgetNotFoundException {

		logger.trace("[idBudget={}] actif ? : {}", idBudget, isActif);

		if(Boolean.TRUE.equals(isActif)){
			boolean isBudgetActif = operationService.isBudgetMensuelActif(idBudget);
			logger.info("[idBudget={}] isActif ? : {}",idBudget, isBudgetActif );
			if(isBudgetActif){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.status(HttpStatus.LOCKED).build();
			}
		}
		return ResponseEntity.notFound().build();
	}

	
	/**
	 * Retourne le statut du budget
	 * @param idBudget id du compte
	 * @return statut du budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Retourne l'état de mise à jour d'un budget mensuel : {uptodate}", notes="{uptodate} indique si le budget a été mis à jour en BDD par rapport à la date passée en paramètre", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget à jour"),
			@ApiResponse(code = 401, message = "Utilisateur non authentifié"),
			@ApiResponse(code = 403, message = "Opération non autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 426, message = "Le Budget a été mis à jour par rapport à la date renvoyée")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="uptodateto", required=false, value="Activité du budget par rapport à la date en paramètres (en ms)", paramType="query"),
	})	

	@GetMapping(value=BudgetApiUrlEnum.BUDGET_UP_TO_DATE)
	public ResponseEntity<Boolean> isBudgetUptoDate(
			@PathVariable("idBudget") String idBudget, @RequestParam(value="uptodateto", required=false) Long uptodateto) throws BudgetNotFoundException {

		logger.trace("[idBudget={}] uptodateto ? {}", idBudget, uptodateto );

		if(uptodateto != null){
			boolean isUpToDate = operationService.isBudgetIHMUpToDate(idBudget, uptodateto);
			logger.info("[idBudget={}] isIHM Up To Date {} ? : {}",idBudget, BudgetDateTimeUtils.getLibelleDateFromMillis(uptodateto), isUpToDate );
			if(isUpToDate){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.status(HttpStatus.UPGRADE_REQUIRED).build();
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
			@RequestParam(value="actif") Boolean setActif) throws BudgetNotFoundException {

		logger.info("[idBudget={}] set Actif : {}", idBudget, setActif );
		BudgetMensuel budgetActif = operationService.setBudgetActif(idBudget, setActif, getIdProprietaire());
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
			@PathVariable("idOperation") String idOperation) throws BudgetNotFoundException {

		logger.info("[idBudget={}][idOperation={}] setAsDerniereOperation", idBudget, idOperation);
		boolean resultat = operationService.setLigneAsDerniereOperation(idBudget, idOperation, getIdProprietaire());
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
			@RequestBody LigneOperation operation) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		logger.info("[idBudget={}][idOperation={}] createOrUpdateOperation", idBudget, idOperation);
		if(operation != null && idBudget != null && getIdProprietaire() != null){
			operation.setId(idOperation);
			operationService.completeCategoriesOnOperation(operation, this.paramClientApi.getCategories());
			try {
				return getEntity(operationService.updateOperationInBudget(idBudget, operation, getIdProprietaire()));
			}
			catch (CompteClosedException e) {
				return ResponseEntity.unprocessableEntity().build();
			}
		}
		else {
			return ResponseEntity.badRequest().build();
		}
	}



	/**
	 * Mise à jour d'une opération
	 * @param idBudget id du budget
	 * @param idOperation opération à mettre à jour
	 * @param idCompte id du compte à mettre à jour
	 * @return budget mis à jour
	 * @throws DataNotFoundException données non trouvées
	 * @throws BudgetNotFoundException  budget non trouvé
	 * @throws CompteClosedException  compte clos
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
			@RequestBody LigneOperation operation) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		logger.info("[idBudget={}][idOperation={}] createOperation InterCompte [{}]", idBudget, idOperation, idCompte);
		if(operation != null && idBudget != null){
			try {
				operation.setId(idOperation);
				operationService.completeCategoriesOnOperation(operation, this.paramClientApi.getCategories());
				BudgetMensuel budgetUpdated = operationService.createOperationIntercompte(idBudget, operation, idCompte, getIdProprietaire());
				return getEntity(budgetUpdated);
			}
			catch (CompteClosedException e) {
				return ResponseEntity.unprocessableEntity().build();
			}
		}
		return ResponseEntity.badRequest().build();
	}

	/**
	 * Mise à jour d'une opération
	 * @param idBudget id du budget
	 * @param idOperation opération à mettre à jour
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
			@PathVariable("idOperation") String idOperation) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		
		if(idOperation != null && idBudget != null && getIdProprietaire() != null){
			logger.info("[idBudget={}][idOperation={}] deleteOperation", idBudget, idOperation);
			BudgetMensuel budgetUpdated = operationService.deleteOperation(idBudget, idOperation, getIdProprietaire());
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
	 * Retourne le compte
	 * @param idCompte id du compte
	 * @return compte associé
	 * @throws DataNotFoundException erreur données non trouvées
	 * @throws UserNotAuthorizedException utilisateur non trouvé
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
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_COMPTE_INTERVALLES)
	public @ResponseBody ResponseEntity<IntervallesCompteAPIObject> getIntervallesBudgetsCompte(@PathVariable("idCompte") String idCompte) throws DataNotFoundException, UserNotAuthorizedException{
		logger.info("[idCompte={}] getIntervallesBudgetsCompte", idCompte);
		if(getIdProprietaire() != null) {
			LocalDate[] intervalles = this.operationService.getIntervallesBudgets(idCompte);
			if(intervalles != null && intervalles.length >= 2){
				IntervallesCompteAPIObject intervallesAPI = new IntervallesCompteAPIObject();
				intervallesAPI.setDatePremierBudget(BudgetDateTimeUtils.getNbDayFromLocalDate(intervalles[0]));
				intervallesAPI.setDateDernierBudget(BudgetDateTimeUtils.getNbDayFromLocalDate(intervalles[1]));
				return getEntity(intervallesAPI);	
			}
			throw new DataNotFoundException("Impossible de trouver l'intervalle de budget pour le compte " + idCompte);
		}
		throw new UserNotAuthorizedException("Impossible de charger les données d'un utilisateur anonyme");
	}



	/**
	 * Liste des libellés des opérations d'un compte (tout mois confondu)
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
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_COMPTE_OPERATIONS_LIBELLES)
	public  @ResponseBody ResponseEntity<LibellesOperationsAPIObject> getLibellesOperations(@PathVariable("idCompte") String idCompte, @RequestParam("annee") Integer annee) {
		logger.info("[idCompte={}] get Libellés Opérations de l'année {}", idCompte, annee);
		if(getIdProprietaire() != null) {
			Set<String> libelles = this.operationService.getLibellesOperations(idCompte, annee);
			if(libelles != null && !libelles.isEmpty()){
				LibellesOperationsAPIObject libellesO = new LibellesOperationsAPIObject();
				libellesO.setIdCompte(idCompte);
				libellesO.setLibellesOperations(libelles);
				return getEntity(libellesO);
			}
		}
		return ResponseEntity.noContent().build();
	}


	@Override
	public List<IServiceProvider> getHTTPClients() {
		return Arrays.asList(this.compteClientApi, this.paramClientApi);
	}
}

