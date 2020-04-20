/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;

import io.swagger.annotations.Api;
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
	private UtilisateursService utilisateursService;

	/**
	 * Date de dernier accès utilisateur
	 * @return date de dernier accès
	 * @throws DataNotFoundException données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Date de dernier accès d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être identifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.USERS_ACCESS_DATE)
	public ResponseEntity<UtilisateurPrefsAPIObject> getLastAccessDateUtilisateur() throws DataNotFoundException{
		String idProprietaire = getIdProprietaire();
		if(idProprietaire != null){
			LocalDateTime lastAccess = utilisateursService.getLastAccessDate(idProprietaire);
			logger.info("LastAccessTime : {}", lastAccess);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idProprietaire);
			prefs.setLastAccessTime(BudgetDateTimeUtils.getLongFromLocalDateTime(lastAccess));
			return getEntity(prefs);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}



	/**
	 * Préférences d'un utilisateur
	 * @param idUtilisateur id Utilisateur
	 * @return préférences
	 * @throws DataNotFoundException données non trouvées
	 */
	@ApiOperation(httpMethod="GET",protocols="HTTPS", value="Préférences d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Opération réussie"),
			@ApiResponse(code = 401, message = "L'utilisateur doit être identifié"),
			@ApiResponse(code = 403, message = "L'opération n'est pas autorisée"),
			@ApiResponse(code = 404, message = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.USERS_PREFS)
	public ResponseEntity<UtilisateurPrefsAPIObject> getPreferencesUtilisateur() throws DataNotFoundException{
		String idProprietaire = getIdProprietaire();
		if(idProprietaire != null){
			Map<UtilisateurPrefsEnum, String> prefsUtilisateur = utilisateursService.getPrefsUtilisateur(idProprietaire);
			logger.info("Preferences Utilisateur : {}", prefsUtilisateur);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idProprietaire);
			prefs.setPreferences(prefsUtilisateur);
			return getEntity(prefs);
		}
		throw new DataNotFoundException("[token=?] Impossible de trouver l'utilisateur");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<AbstractHTTPClient> getHTTPClients() {
		return new ArrayList<>();
	}
}
