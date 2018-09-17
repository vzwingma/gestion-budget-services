/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
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
@RequestMapping(value=BudgetApiUrlEnum.USERS_BASE)
@Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Utilisateurs", tags={"Utilisateurs"})
public class UtilisateursAPIController extends AbstractAPIController {


	@Autowired
	private UtilisateursService authService;

	/**
	 * Authentification
	 * @param login login de l'utilisateur
	 * @param motPasse motPasse
	 * @return résultat de l'opération
	 */
	@ApiOperation(httpMethod="POST", produces=MediaType.APPLICATION_JSON_VALUE, protocols="HTTPS", value="Authentification d'un utilisateur", response=String.class)
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentification réussie"),
            @ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
            @ApiResponse(code = 404, message = "Ressource introuvable")
    })
	@ApiImplicitParams(value={
			@ApiImplicitParam(allowEmptyValue=false, allowMultiple=false, dataTypeClass=AuthLoginAPIObject.class, name="auth", required=true, value="Authentification de l'utilisateur", paramType="body")
	})
	@PostMapping(value=BudgetApiUrlEnum.USERS_AUTHENTICATE, consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<String> authenticate(@RequestBody AuthLoginAPIObject auth) throws UserNotAuthorizedException{
		logger.warn("[API][idUser=?] Service Authenticate implémenté via le Filter (JwtUsernameAndPasswordAuthenticationFilter). Cette méthode ne doit pas être appelée et renvoie une exception");
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
	@PostMapping(value=BudgetApiUrlEnum.USERS_DISCONNECT)
	public ResponseEntity<String> disconnect(@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth) throws DataNotFoundException{
		String idUtilisateur = getIdUtilisateur(auth);
		if(authService.deconnexionBusinessSession(idUtilisateur)){
			logger.info("[API][idUser={}] Disconnect : true", idUtilisateur);
			return ResponseEntity.noContent().build();
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
	@GetMapping(value=BudgetApiUrlEnum.USERS_ACCESS_DATE)
	public ResponseEntity<UtilisateurPrefsAPIObject> getLastAccessDateUtilisateur(@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth) throws DataNotFoundException{
		String idUtilisateur = getIdUtilisateur(auth);
		if(authService.getBusinessSession(idUtilisateur) != null){
			LocalDateTime lastAccess = authService.getBusinessSession(idUtilisateur).getUtilisateur().getDernierAcces();
			logger.info("[API][idUser={}] LastAccessTime : {}", idUtilisateur, lastAccess);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idUtilisateur);
			prefs.setLastAccessTime(BudgetDateTimeUtils.getLongFromLocalDateTime(lastAccess));
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
	@GetMapping(value=BudgetApiUrlEnum.USERS_PREFS)
	public ResponseEntity<UtilisateurPrefsAPIObject> getPreferencesUtilisateur(@RequestHeader(JwtConfig.JWT_AUTH_HEADER) String auth) throws DataNotFoundException{
		String idUtilisateur = getIdUtilisateur(auth);
		if(authService.getBusinessSession(idUtilisateur) != null){
			Map<UtilisateurPrefsEnum, String> prefsUtilisateur = authService.getBusinessSession(idUtilisateur).getUtilisateur().getPrefsUtilisateur();
			logger.info("[API][idUser={}] Preferences Utilisateur : {}", idUtilisateur, prefsUtilisateur);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idUtilisateur);
			prefs.setPreferences(prefsUtilisateur);
			return getEntity(prefs);
		}
		throw new DataNotFoundException("[API][idUser="+idUtilisateur+"] Impossible de trouver l'utilisateur");
	}
}
