package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Controleur REST -
 * Adapteur du port {@link IComptesAppProvider}
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.COMPTES_BASE)
public class ComptesResource extends AbstractAPIResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComptesResource.class);


    @Inject
    IComptesAppProvider services;


    /**
     * Retour la liste des comptes
     * @return liste des comptes de l'utilisateur
     */
    @Operation(description="Comptes d'un utilisateur")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CompteBancaire.class))}),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @Path(BudgetApiUrlEnum.COMPTES_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<CompteBancaire>> getComptesUtilisateur() {
        LOG.info("getComptes");
        String proprietaire = "vzwingma";
        return this.services.getComptesUtilisateur(proprietaire)
                .invoke(listeComptes -> LOG.info("[idUser={}] Chargement des {} comptes", proprietaire, listeComptes != null ? listeComptes.size() : "-1"));
    }

    /**
     * Retourne le compte
     * @param idCompte id du compte
     * @return compte associé
     */
    @Operation(description="Compte d'un utilisateur")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CompteBancaire.class)) }),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(BudgetApiUrlEnum.COMPTES_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CompteBancaire> getCompteUtilisateur(@RestPath String idCompte) {
        String proprietaire = "vzwingma";
        LOG.info("[idCompte={}] getCompte", idCompte);
        return this.services.getCompteById(idCompte, proprietaire)
                .invoke(compte -> LOG.info("[idUser={}] Chargement du compte [{}]", proprietaire, compte != null ? compte.getLibelle() : "-1"));
    }
}