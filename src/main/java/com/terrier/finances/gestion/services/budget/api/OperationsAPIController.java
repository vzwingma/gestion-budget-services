package com.terrier.finances.gestion.services.budget.api;

import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idCompte", required=true, value="Id du compte", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="mois", required=true, value="No de mois", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=Integer.class, name="annee", required=true, value="No de l'année", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path")			
	})	
	@GetMapping(value=BudgetApiUrlEnum.BUDGET_QUERY)
	public  @ResponseBody ResponseEntity<BudgetMensuel> getBudget(
			@RequestParam("idCompte") String idCompte, 
			@RequestParam("mois") String mois, 
			@RequestParam("annee") String annee, 
			@RequestParam("idUtilisateur") String idUtilisateur) throws BudgetNotFoundException, DataNotFoundException {
		LOGGER.info("[API][idUser={}][idCompte={}] getBudget {}/{}", idUtilisateur, idCompte, mois, annee);
		
		if(mois != null && annee != null){
			try{
				Month month = Month.of(Integer.parseInt(mois));
				return getEntity(operationService.chargerBudgetMensuel(idUtilisateur, idCompte, month, Integer.parseInt(annee)));
			}
			catch(NumberFormatException e){
				throw new DataNotFoundException("Erreur dans les paramètres en entrée");
			}
		}
		throw new BudgetNotFoundException("Erreur dans les paramètres en entrée");
	}
}
