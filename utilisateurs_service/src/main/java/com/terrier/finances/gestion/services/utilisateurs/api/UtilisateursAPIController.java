/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;
import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controleur REST pour récupérer les utilisateurs
 * Adapteur du Port {@link com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest}
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.USERS_BASE)
//Api(consumes=MediaType.APPLICATION_JSON_VALUE, protocols="https", value="Utilisateurs", tags={"Utilisateurs"})
public class UtilisateursAPIController extends AbstractAPIController {


	@Autowired
	private IUtilisateursRequest utilisateursService;

	/**
	 * Date de dernier accès utilisateur
	 * @return date de dernier accès
	 * @throws DataNotFoundException données non trouvées
	 */
	@Operation(method = "GET", description = "Fournit la date de dernier accès d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = UtilisateurPrefsAPIObject.class)) }),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être identifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.USERS_ACCESS_DATE)
	public ResponseEntity<UtilisateurPrefsAPIObject> getLastAccessDateUtilisateur() throws DataNotFoundException{
		String idProprietaire = getIdProprietaire();
		if(idProprietaire != null){
			LocalDateTime lastAccess = utilisateursService.getLastAccessDate(idProprietaire);
			logger.info("LastAccessTime : {}", lastAccess);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idProprietaire);
			prefs.setLastAccessTime(BudgetDateTimeUtils.getSecondsFromLocalDateTime(lastAccess));
			return getEntity(prefs);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}



	/**
	 * Préférences d'un utilisateur
	 * @return préférences
	 * @throws DataNotFoundException données non trouvées
	 */
	@Operation(method="GET", description = "Préférences d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie"),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être identifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.USERS_PREFS)
	public ResponseEntity<UtilisateurPrefsAPIObject> getPreferencesUtilisateur() throws DataNotFoundException{
		String idProprietaire = getIdProprietaire();
		if(idProprietaire != null){
			Utilisateur utilisateur = utilisateursService.getUtilisateur(idProprietaire);
			Map<UtilisateurPrefsEnum, String> prefsUtilisateur = utilisateur.getPrefsUtilisateur();
			Map<UtilisateurDroitsEnum, Boolean> droitsUtilisateur = utilisateur.getDroits();
			logger.info("Preferences Utilisateur : {} | {}", prefsUtilisateur, droitsUtilisateur);
			UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
			prefs.setIdUtilisateur(idProprietaire);
			prefs.setPreferences(prefsUtilisateur);
			prefs.setDroits(droitsUtilisateur);
			return getEntity(prefs);
		}
		throw new DataNotFoundException("[token=?] Impossible de trouver l'utilisateur");
	}
}
