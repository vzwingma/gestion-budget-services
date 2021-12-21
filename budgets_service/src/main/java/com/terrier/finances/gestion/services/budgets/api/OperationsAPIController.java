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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	 * @param annee année du budget
	 * @return budget
	 */
	@Operation(method = "GET", description = "Recherche d'un budget mensuel pour un compte d'un utilisateur", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
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
	 */
	@Operation(method="GET",description="Chargement d'un budget", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Budget chargé",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
	})
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> getBudget(
			@PathVariable("idBudget") String idBudget) {

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
	 * @throws DataNotFoundException budget non trouvé
	 */
	@Operation(method="DELETE",description="Réinitialisation d'un budget", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Budget réinitialisé",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "405", description = "Compte clos. Impossible de réinitialiser le budget")
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
	 */
	@Operation(method="GET",description="Retourne l'état d'un budget mensuel : {etat}; {etat} : indique si le budget est ouvert ou cloturé.", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Budget actif"),
			@ApiResponse(responseCode = "423", description = "Budget inactif"),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
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
	 */
	@Operation(method="GET",description="Retourne l'état de mise à jour d'un budget mensuel : {uptodate} : {uptodate} indique si le budget a été mis à jour en BDD par rapport à la date passée en paramètre", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Budget à jour"),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "426", description = "Le Budget a été mis à jour par rapport à la date renvoyée")
	})
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_UP_TO_DATE)
	public ResponseEntity<Boolean> isBudgetUptoDate(
			@PathVariable("idBudget") String idBudget, @RequestParam(value="uptodateto", required=false) Long uptodateto) {

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
	 */
	@Operation(method="POST",description="Mise à jour de l'{état} d'un budget mensuel (ouvert/cloturé) ; {etat} : indique si le budget est ouvert ou cloturé.", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "500", description = "Opération en échec")
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
	 */
	@Operation(method="POST",description="Met à jour le flag de l'opération comme dernière opération réalisée", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Budget mis à jour"),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
	})
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE)
	public ResponseEntity<Boolean> setAsDerniereOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation)  {

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
	 * @throws DataNotFoundException données non trouvées
	 * @throws BudgetNotFoundException  budget non trouvé
	 */
	@Operation(method="POST",description="Mise à jour d'une opération", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération mise à jour",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),	
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "423", description = "Compte clos")
	})
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION)
	public @ResponseBody ResponseEntity<BudgetMensuel> createOrUpdateOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation,
			@RequestBody LigneOperation operation) throws DataNotFoundException, BudgetNotFoundException{

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
	 */
	@Operation(method="POST",description="Mise à jour d'une opération Intercomptes", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération mise à jour",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),	
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "423", description = "Compte clos")
	})
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE)
	public @ResponseBody ResponseEntity<BudgetMensuel> createOperationIntercomptes(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation,
			@PathVariable("idCompte") String idCompte,
			@RequestBody LigneOperation operation) throws DataNotFoundException, BudgetNotFoundException{

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
	 * @throws DataNotFoundException données non trouvées
	 * @throws BudgetNotFoundException  budget non trouvé
	 * @throws CompteClosedException  compte clos
	 */
	@Operation(method="DELETE",description="Suppression d'une opération", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération supprimée",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
			@ApiResponse(responseCode = "204", description = "Opération supprimée"),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
			@ApiResponse(responseCode = "403", description = "Opération non autorisée"),	
			@ApiResponse(responseCode = "404", description = "Données introuvables"),
			@ApiResponse(responseCode = "405", description = "Compte clos")
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
	@Operation(method="GET",description="Intervalles des budgets pour un compte", tags={"Budget"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = IntervallesCompteAPIObject.class))}),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
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
	 */
	@Operation(method="GET",description="Libelles des opérations des budgets de l'année pour un compte", tags={"Opérations"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LibellesOperationsAPIObject.class))}),
			@ApiResponse(responseCode = "204", description = "Aucune donnée"),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
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

