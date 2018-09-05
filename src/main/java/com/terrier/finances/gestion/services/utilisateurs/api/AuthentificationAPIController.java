/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.utilisateur.model.Authentification;
import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;

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
@RequestMapping(value="/rest/authentification")
@Api(consumes="application/json", protocols="https", value="Authentification", tags={"Authentification"})
public class AuthentificationAPIController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationAPIController.class);

	@Autowired
	private AuthenticationService authService;

	/**
	 * Authentification
	 * @param login login de l'utilisateur
	 * @param motPasse motPasse
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="POST", produces="application/text", protocols="HTTPS", value="Authentification d'un utilisateur", response=String.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentification réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="login", required=true, value="Login de l'utilisateur", paramType="body"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="motPasse", required=true, value="Mot de passe de l'utilisateur", paramType="body"),
	})
	@PostMapping(value="/v1/authenticate")
	public String password(@RequestBody Authentification auth){
		String idUtilisateur = authService.authenticate(auth.getLogin(), auth.getMotDePasse());
		if(idUtilisateur != null){
			return idUtilisateur;
		}
		return null;
	}
	
}
