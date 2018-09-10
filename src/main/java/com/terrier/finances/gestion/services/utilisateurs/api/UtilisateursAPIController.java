/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import java.time.LocalDateTime;
import java.util.Map;

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

import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthResponseAPIObject;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.DataUtils;
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
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Utilisateurs", tags={"Utilisateurs"})
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
	@ApiOperation(httpMethod="POST", produces=MediaType.APPLICATION_JSON_VALUE, protocols="HTTPS", value="Authentification d'un utilisateur", response=AuthResponseAPIObject.class)
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
	public @ResponseBody ResponseEntity<AuthResponseAPIObject> authenticate(@RequestBody AuthLoginAPIObject auth) throws UserNotAuthorizedException{
		LOGGER.trace("[API][idUser=?] Authenticate : {}", auth);
		String idUtilisateur = authService.authenticate(auth.getLogin(), auth.getMotDePasse());
		if(idUtilisateur != null){
			AuthResponseAPIObject response = new AuthResponseAPIObject();
			Utilisateur utilisateur = authService.getBusinessSession(idUtilisateur).getUtilisateur();
			response.setIdUtilisateur(idUtilisateur);
			response.setDroits(utilisateur.getDroits());
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
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
	})
	
	@PostMapping(value=BudgetApiUrlEnum.USERS_DISCONNECT+"/{idUtilisateur}")
	public ResponseEntity<?> disconnect(@PathVariable("idUtilisateur") String idUtilisateur) throws DataNotFoundException{
		if(authService.deconnexionBusinessSession(idUtilisateur)){
			LOGGER.info("[API][idUser={}] Disconnect : true", idUtilisateur);
			return ResponseEntity.ok().build();
		}
		throw new DataNotFoundException("[API][idUser="+idUtilisateur+"] Impossible de déconnecter l'utilisateur");
	}
	
	/**
	 * Date de dernier accès utilisateur
	 * @param idUtilisateur id Utilisateur
	 * @return date de dernier accès
	 * @throws DataNotFoundException
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Date de dernier accès d'un utilisateur")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
	})
	
	@GetMapping(value=BudgetApiUrlEnum.USERS_ACCESS_DATE+"/{idUtilisateur}")
	public ResponseEntity<UtilisateurPrefsAPIObject> getLastAccessDateUtilisateur(@PathVariable("idUtilisateur") String idUtilisateur) throws DataNotFoundException{
		
		if(authService.getBusinessSession(idUtilisateur) != null){
			LocalDateTime lastAccess = authService.getBusinessSession(idUtilisateur).getUtilisateur().getDernierAcces();
			LOGGER.info("[API][idUser={}] LastAccessTime : {}", idUtilisateur, lastAccess);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idUtilisateur);
			prefs.setLastAccessTime(DataUtils.getLongFromLocalDateTime(lastAccess));
			return getEntity(prefs);
		}
		throw new DataNotFoundException("[API][idUser="+idUtilisateur+"] Impossible de trouver l'utilisateur");
	}
	
	

	/**
	 * Préférences d'un utilisateur
	 * @param idUtilisateur id Utilisateur
	 * @return préférences
	 * @throws DataNotFoundException
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Préférences d'un utilisateur")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Session introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=String.class, name="idUtilisateur", required=true, value="Id de l'utilisateur", paramType="path"),
	})
	
	@GetMapping(value=BudgetApiUrlEnum.USERS_PREFS+"/{idUtilisateur}")
	public ResponseEntity<UtilisateurPrefsAPIObject> getPreferencesUtilisateur(@PathVariable("idUtilisateur") String idUtilisateur) throws DataNotFoundException{
		
		if(authService.getBusinessSession(idUtilisateur) != null){
			Map<UtilisateurPrefsEnum, String> prefsUtilisateur = authService.getBusinessSession(idUtilisateur).getUtilisateur().getPrefsUtilisateur();
			LOGGER.info("[API][idUser={}] Preferences Utilisateur : {}", idUtilisateur, prefsUtilisateur);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idUtilisateur);
			prefs.setPreferences(prefsUtilisateur);
			return getEntity(prefs);
		}
		throw new DataNotFoundException("[API][idUser="+idUtilisateur+"] Impossible de trouver l'utilisateur");
	}
}
