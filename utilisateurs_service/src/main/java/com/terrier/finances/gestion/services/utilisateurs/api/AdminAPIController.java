/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.ADMIN_BASE)
@Api(protocols="https", value="Administration", tags={"Administration"},
	consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
public class AdminAPIController extends AbstractAPIController {



	@Autowired
	private UtilisateursService authService;

	/**
	 * Changement du mot de passe
	 * @param login login de l'utilisateur
	 * @param oldpassword ancien mot de passe
	 * @param newPassword nouveau mot de passe
	 * @return résultat de l'opération
	 * @throws UserNotAuthorizedException 
	 */
	@ApiOperation(httpMethod="GET", produces="application/text", protocols="HTTPS", value="Opération de changement de mot de passe", response=String.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Changement de passe réussi"),
            @ApiResponse(code = 401, message = "L'utilisateur doit être identifié"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="login", required=true, value="Login de l'utilisateur", paramType="header"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="oldpassword", required=true, value="Ancien mot de passe de l'utilisateur", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="newpassword", required=true, value="Nouveau mot de passe de l'utilisateur", paramType="path")
	})
	@GetMapping(value=BudgetApiUrlEnum.ADMIN_ACCESS)
	public String password(@PathVariable("oldpassword") String oldpassword, @PathVariable("newpassword") String newPassword) throws UserNotAuthorizedException{
		logger.info("Changement du mot de passe");
		Utilisateur utilisateur = null; //userSession.getUtilisateur();
		if(utilisateur != null){
			authService.changePassword(utilisateur, oldpassword, newPassword);
			String returnOK = "Le mot de passe de "+utilisateur.getLogin()+ " a bien été modifié : \n " + utilisateur.toFullString();
			logger.error(returnOK);
			return returnOK;
		}
		else{
			String returnErr = "L'utilisateur est introuvable ou le mot de passe est incorrect";
			logger.error(returnErr);
			return returnErr;
		}
	}

	@Override
	public List<AbstractHTTPClient> getHTTPClients() {
		return new ArrayList<AbstractHTTPClient>();
	}	
	
	
}
