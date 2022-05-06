package io.github.vzwingma.finances.budget.services.utilisateurs.api;

import io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.enums.UtilisateurDroitsEnum;
import io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.enums.UtilisateurPrefsEnum;
import io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.model.UtilisateurPrefsAPIObject;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controleur REST -
 * Adapteur du port
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.PARAMS_BASE)
public class UtilisateursResource {


    private static final Logger LOG = LoggerFactory.getLogger(UtilisateursResource.class);

    @Inject
    private IUtilisateursRequest utilisateursService;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return "API Utilisateurs";
    }


    /**
     * Date de dernier accès utilisateur
     * @return date de dernier accès
     */
    @Operation(description = "Fournit la date de dernier accès d'un utilisateur", summary="Date de denier accès")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UtilisateurPrefsAPIObject.class)) }),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être identifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @Path(BudgetApiUrlEnum.USERS_ACCESS_DATE)
    @Produces(MediaType.APPLICATION_JSON)
    public UtilisateurPrefsAPIObject getLastAccessDateUtilisateur() throws DataNotFoundException {
        String idProprietaire = "vzwingma"; //getIdProprietaire();
        if(idProprietaire != null){
            LocalDateTime lastAccess = utilisateursService.getLastAccessDate(idProprietaire);
            LOG.info("LastAccessTime : {}", lastAccess);
            UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
            prefs.setIdUtilisateur(idProprietaire);
            prefs.setLastAccessTime(BudgetDateTimeUtils.getSecondsFromLocalDateTime(lastAccess));
            return prefs;
        }
        return null; //new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }



    /**
     * Préférences d'un utilisateur
     * @return préférences
     */
    @Operation(description = "Fournir les préférences d'affichage d'un utilisateur", summary="Préférences d'un utilisateur")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UtilisateurPrefsAPIObject.class))),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être identifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @Path(BudgetApiUrlEnum.USERS_PREFS)
    public UtilisateurPrefsAPIObject getPreferencesUtilisateur() throws DataNotFoundException {
        String idProprietaire ="vzwingma"; // getIdProprietaire();
        if(idProprietaire != null){
            Utilisateur utilisateur = utilisateursService.getUtilisateur(idProprietaire);
            Map<UtilisateurPrefsEnum, String> prefsUtilisateur = utilisateur.getPrefsUtilisateur();
            Map<UtilisateurDroitsEnum, Boolean> droitsUtilisateur = utilisateur.getDroits();
            LOG.info("Preferences Utilisateur : {} | {}", prefsUtilisateur, droitsUtilisateur);
            UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
            prefs.setIdUtilisateur(idProprietaire);
            prefs.setPreferences(prefsUtilisateur);
            prefs.setDroits(droitsUtilisateur);
            return prefs;
        }
        //throw new DataNotFoundException("[token=?] Impossible de trouver l'utilisateur");
        return null;
    }
}