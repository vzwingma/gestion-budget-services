package com.terrier.finances.gestion.services.budget.api;

import java.time.Month;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
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
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Recherche d'un budget mensuel pour un compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
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
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth) throws UserNotAuthorizedException, BudgetNotFoundException, DataNotFoundException {
		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idCompte={}] getBudget {}/{}", idUtilisateur, idCompte, mois, annee);

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
	 * @throws BudgetNotFoundException 
	*/
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour d'un budget")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget mis à jour"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=BudgetMensuel.class, name="budget", required=true, value="Budget à mettre à joru", paramType="body"),
	})	
	 
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> updateBudget(
			@PathVariable("idBudget") String idBudget, 
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth, 
			@RequestBody BudgetMensuel budget) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException{
		
		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}] updateBudget",idUtilisateur, idBudget);
		if(budget != null && idBudget != null && idBudget.equals(budget.getId())){
			BudgetMensuel budgetUpdated = operationService.calculEtSauvegardeBudget(budget, idUtilisateur);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget");
	}


	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param operation opération à mettre à jour
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
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=LigneOperation.class, name="operation", required=true, value="Operation", paramType="path"),
	})	
	 
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATIONS_BASE)
	public @ResponseBody ResponseEntity<BudgetMensuel> createOrUpdateOperation(
			@PathVariable("idBudget") String idBudget,
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth, 
			@RequestBody LigneOperation operation) throws UserNotAuthorizedException, DataNotFoundException{
		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}] createOrUpdateOperation",idUtilisateur, idBudget);
		if(operation != null && idBudget != null){
			BudgetMensuel budgetUpdated = null; //operationService.calculEtSauvegardeBudget(idBudget, idUtilisateur);
			return getEntity(budgetUpdated);
		}
		throw new DataNotFoundException("Impossible de mettre à jour le budget");
	}
	/**
	 * Mise à jour du budget
	 * @param idBudget id du budget
	 * @param idUtilisateur idUtilisateur
	 * @param budget budget
	 * @return budget mis à jour
	 * @throws DataNotFoundException
	 */
	@ApiOperation(httpMethod="DELETE",protocols="HTTPS", value="Réinitialisation d'un budget")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget réinitialisé"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables"),
			@ApiResponse(code = 405, message = "Compte clos. Impossible de réinitialiser le budget")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
	})	
	
	@DeleteMapping(value=BudgetApiUrlEnum.BUDGET_ID)
	public @ResponseBody ResponseEntity<BudgetMensuel> reinitializeBudget(@PathVariable("idBudget") String idBudget, @RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth) throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}] reinitialisation", idUtilisateur, idBudget);
		if(idBudget != null){
			BudgetMensuel budgetUpdated = operationService.reinitialiserBudgetMensuel(idBudget, idUtilisateur);
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
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Retourne l'un des états d'un budget mensuel : {actif} ou {uptodate}", notes="{actif} : indique si le budget est actif ou pas. {uptodate} indique si le budget a été mis à jour en BDD par rapport à la date passée en paramètre")
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
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth,
			@RequestParam(value="actif", required=false, defaultValue="false") Boolean isActif,  @RequestParam(value="uptodateto", required=false) Long uptodateto) throws UserNotAuthorizedException, BudgetNotFoundException {

		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}] actif ? : {}, uptodateto ? {}",idUtilisateur, idBudget, isActif, uptodateto );

		if(isActif){
			boolean isBudgetActif = operationService.isBudgetMensuelActif(idBudget);
			logger.info("[API][idBudget={}] isActif ? : {}",idBudget, isBudgetActif );
			if(isBudgetActif){
				return ResponseEntity.ok(true);
			}
			else{
				return ResponseEntity.noContent().build();
			}
		}
		else if(uptodateto != null){
			boolean isUpToDate = operationService.isBudgetUpToDate(idBudget, new Date(uptodateto), idUtilisateur);
			logger.info("[API][idBudget={}] isUpToDateto {} ? : {}",idBudget, uptodateto, isUpToDate );
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
	 * Met à jour le flag de l'opération comme dernière opération réalisée
	 * @param idBudget id du compte
	 * @return résultat de l'action
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	*/
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Met à jour le flag de l'opération comme dernière opération réalisée", notes="{derniereOperation} : indique s'il s'agit de la dernière opération")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Budget mis à jour"),
			@ApiResponse(code = 204, message = "Budget non mis à jour"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Données introuvables")
	})
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idBudget", required=true, value="Id du budget", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="idOperation", required=true, value="Id de l'opération", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Boolean.class, name="derniereOperation", required=true, value="Activité du budget par rapport à la date en paramètres (en ms)", paramType="query"),
	})	
	 
	@PostMapping(value=BudgetApiUrlEnum.BUDGET_OPERATIONS_ID_ETAT)
	public ResponseEntity<Boolean> setAsDerniereOperation(
			@PathVariable("idBudget") String idBudget,
			@PathVariable("idOperation") String idOperation, 
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth,
			@RequestParam(value="derniereOperation", required=true, defaultValue="false") Boolean isDerniereOp) throws UserNotAuthorizedException, BudgetNotFoundException {

		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}][idOperation={}] setAsDerniereOperation {}",idUtilisateur, idBudget, idOperation, isDerniereOp );

		if(isDerniereOp){
			boolean resultat = operationService.setLigneDepenseAsDerniereOperation(idBudget, idOperation, idUtilisateur);
			if(resultat){
				return ResponseEntity.ok().build();
			}
		}
		return ResponseEntity.noContent().build();
	}


	/**
     * Met à jour le statut du budget
	 * @param idBudget id du compte
	 * @return statut du budget 
	 * @throws BudgetNotFoundException erreur données non trouvées
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Mise à jour de l'état d'un budget mensuel (ouvert/cloturé)")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
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
			@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth,
			@RequestParam(value="actif") Boolean setActif) throws UserNotAuthorizedException, BudgetNotFoundException {

		String idUtilisateur = getIdUtilisateur(auth);
		logger.info("[API][idUser={}][idBudget={}] set Actif : {}",idUtilisateur, idBudget, setActif );
		BudgetMensuel budgetActif = operationService.setBudgetActif(idBudget, setActif, idUtilisateur);
		return getEntity(budgetActif);
	}
}

