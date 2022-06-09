package io.github.vzwingma.finances.budget.services.utilisateurs.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContextKeyEnum;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.UserAccessForbiddenException;
import io.github.vzwingma.finances.budget.services.utilisateurs.api.enums.UtilisateursApiUrlEnum;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.UtilisateurDroitsEnum;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.UtilisateurPrefsAPIObject;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.UtilisateurPrefsEnum;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursAppProvider;
import io.smallrye.mutiny.Uni;
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
import java.util.Map;

/**
 * Controleur REST -
 * Adapteur du port
 * @author vzwingma
 *
 */
@Path(UtilisateursApiUrlEnum.USERS_BASE)
public class UtilisateursResource extends AbstractAPIResource {


    private static final Logger LOG = LoggerFactory.getLogger(UtilisateursResource.class);

    @Inject
    IUtilisateursAppProvider service;


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
    @Path(UtilisateursApiUrlEnum.USERS_ACCESS_DATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UtilisateurPrefsAPIObject> getLastAccessDateUtilisateur() throws UserAccessForbiddenException {
        String idProprietaire = "vzwingma"; //getIdProprietaire();
        BusinessTraceContext.get().clear().put(BusinessTraceContextKeyEnum.USER, idProprietaire);
        if(idProprietaire != null) {
            return service.getLastAccessDate(idProprietaire)
                    .onFailure()
                        .recoverWithUni(Uni.createFrom().failure(new UserAccessForbiddenException("Utilisateur introuvable")))
                    .onItem().transform(lastAccess -> {
                        LOG.info("LastAccessTime : {}", lastAccess);
                        UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
                        prefs.setIdUtilisateur(idProprietaire);
                        prefs.setLastAccessTime(BudgetDateTimeUtils.getSecondsFromLocalDateTime(lastAccess));
                        return prefs;
                    });
        }
        else {
            return Uni.createFrom().failure(new UserAccessForbiddenException("Propriétaire introuvable"));
        }
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
    @Path(UtilisateursApiUrlEnum.USERS_PREFS)
    public Uni<UtilisateurPrefsAPIObject> getPreferencesUtilisateur() {
        String idProprietaire ="vzwingma"; // getIdProprietaire();
        BusinessTraceContext.get().put(BusinessTraceContextKeyEnum.USER, idProprietaire);
        if(idProprietaire != null){
            return service.getUtilisateur(idProprietaire)
                    .onFailure()
                        .recoverWithUni(Uni.createFrom().failure(new UserAccessForbiddenException("Utilisateur introuvable")))
                    .map(utilisateur -> {
                        UtilisateurPrefsAPIObject prefs = new UtilisateurPrefsAPIObject();
                        prefs.setIdUtilisateur(idProprietaire);

                        if(utilisateur != null) {
                            Map<UtilisateurPrefsEnum, String> prefsUtilisateur = utilisateur.getPrefsUtilisateur();
                            Map<UtilisateurDroitsEnum, Boolean> droitsUtilisateur = utilisateur.getDroits();
                            LOG.info("Preferences Utilisateur : {} | {}", prefsUtilisateur, droitsUtilisateur);
                            prefs.setPreferences(prefsUtilisateur);
                            prefs.setDroits(droitsUtilisateur);
                        }
                        return prefs;
                    });
        }
        else {
            return Uni.createFrom().failure(new UserAccessForbiddenException("Propriétaire introuvable"));
        }
    }
}