/**
 * 
 */
package com.terrier.finances.gestion.services.admin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
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
@RequestMapping(value="/rest/admin")
@Api(consumes="application/json", protocols="https", value="Administration", tags={"Administration"})
public class AdminAPIController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminAPIController.class);

	@Autowired
	private UtilisateursService authService;

	/**
	 * Changement du mot de passe
	 * @param login login de l'utilisateur
	 * @param oldpassword ancien mot de passe
	 * @param newPassword nouveau mot de passe
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="GET", produces="application/text", protocols="HTTPS", value="Opération de changement de mot de passe", response=String.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Changement de passe réussi"),
            @ApiResponse(code = 401, message = "L'opération doit être identifiée"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="login", required=true, value="Login de l'utilisateur", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="oldpassword", required=true, value="Ancien mot de passe de l'utilisateur", paramType="path"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="newpassword", required=true, value="Nouveau mot de passe de l'utilisateur", paramType="path")
	})
	
	@GetMapping(value="/v1/password/{login}/{oldpassword}/{newpassword}")
	public String password(@PathVariable("login") String login, @PathVariable("oldpassword") String oldpassword, @PathVariable("newpassword") String newPassword){
		LOGGER.info("Changement du mot de passe pour {}", login);
		
		String idUtilisateur = authService.authenticate(login, oldpassword);
		Utilisateur utilisateur = authService.getBusinessSession(idUtilisateur).getUtilisateur();
		if(utilisateur != null){
			authService.changePassword(utilisateur, oldpassword, newPassword);
			String returnOK = "Le mot de passe de "+utilisateur.getLogin()+ " a bien été modifié : \n " + utilisateur.toFullString();
			LOGGER.error(returnOK);
			return returnOK;
		}
		else{
			String returnErr = "L'utilisateur est introuvable ou le mot de passe est incorrect";
			LOGGER.error(returnErr);
			return returnErr;
		}
	}	
}
