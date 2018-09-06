/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginRestObject;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthResponseRestObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
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
@RequestMapping(value=BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.USERS_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Authentification", tags={"Authentification"})
public class UtilisateursAPIController extends AbstractAPIController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateursAPIController.class);

	@Autowired
	private UtilisateursService authService;

	/**
	 * Authentification
	 * @param login login de l'utilisateur
	 * @param motPasse motPasse
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="POST", produces=MediaType.APPLICATION_JSON_VALUE, protocols="HTTPS", value="Authentification d'un utilisateur", response=AuthResponseRestObject.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentification réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="login", required=true, value="Login de l'utilisateur", paramType="body"),
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="motPasse", required=true, value="Mot de passe de l'utilisateur", paramType="body"),
	})
	@PostMapping(value=BudgetApiUrlEnum.USERS_AUTHENTICATE, consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<AuthResponseRestObject> authenticate(@RequestBody AuthLoginRestObject auth) throws UserNotAuthorizedException{
		LOGGER.info("[API] Authenticate : {}", auth);
		String idUtilisateur = authService.authenticate(auth.getLogin(), auth.getMotDePasse());
		if(idUtilisateur != null){
			AuthResponseRestObject response = new AuthResponseRestObject();
			response.setIdUtilisateur(idUtilisateur);
			return getEntity(response);
		}
		throw new UserNotAuthorizedException();
	}
	
	/**
	 * Authentification
	 * @param login login de l'utilisateur
	 * @param motPasse motPasse
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="POST",protocols="HTTPS", value="Déconnexion d'un utilisateur")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Déconnexion réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idSession", required=true, value="Id de l'utilisateur", paramType="path"),
	})
	
	@PostMapping(value=BudgetApiUrlEnum.USERS_DISCONNECT+"/{idSession}")
	public ResponseEntity<?> disconnect(@PathVariable("idSession") String idSession) throws DataNotFoundException{
		LOGGER.info("[API] Disconnect : {}", idSession);
		if(authService.deconnexionBusinessSession(idSession)){
			return ResponseEntity.ok().build();
		}
		throw new DataNotFoundException("Impossible de déconnecter l'utilisateur : " + idSession);
	}
	
	/**
	 * Authentification
	 * @param login login de l'utilisateur
	 * @param motPasse motPasse
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Date de dernier accès d'un utilisateur")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idSession", required=true, value="Id de l'utilisateur", paramType="path"),
	})
	
	@GetMapping(value=BudgetApiUrlEnum.USERS_ACCESS_DATE+"/{idSession}")
	public ResponseEntity<LocalDateTime> lastAccessDate(@PathVariable("idSession") String idSession) throws DataNotFoundException{
		LOGGER.info("[API] LastAccessTime : {}", idSession);
		if(authService.getBusinessSession(idSession) != null){
			return ResponseEntity.ok(authService.getBusinessSession(idSession).getUtilisateur().getDernierAcces());
		}
		throw new DataNotFoundException("Impossible de trouver l'utilisateur : " + idSession);
	}
}
