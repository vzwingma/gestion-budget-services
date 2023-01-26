package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIInterceptors;
import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContextKeyEnum;
import io.github.vzwingma.finances.budget.services.comptes.api.enums.ComptesAPIEnum;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Controleur REST -
 * Adapteur du port {@link IComptesAppProvider}
 * @author vzwingma
 *
 */
@Path(ComptesAPIEnum.COMPTES_BASE)
public class ComptesResource extends AbstractAPIInterceptors {

    private static final Logger LOG = LoggerFactory.getLogger(ComptesResource.class);


    @Inject
    IComptesAppProvider services;

    @Context
    SecurityContext securityContext;
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
    @RolesAllowed({ ComptesAPIEnum.COMPTES_ROLE })
    @Path(ComptesAPIEnum.COMPTES_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<CompteBancaire>> getComptesUtilisateur() {

        String proprietaire = securityContext.getUserPrincipal().getName();
        BusinessTraceContext.getclear().remove(BusinessTraceContextKeyEnum.COMPTE).put(BusinessTraceContextKeyEnum.USER, proprietaire);
        LOG.info("getComptes");
        return this.services.getComptesUtilisateur(proprietaire)
                .invoke(listeComptes -> LOG.info("{} comptes chargés", listeComptes != null ? listeComptes.size() : "-1"));
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
    @Path(ComptesAPIEnum.COMPTES_ID)
    @RolesAllowed({ ComptesAPIEnum.COMPTES_ROLE })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CompteBancaire> getCompteUtilisateur(@RestPath String idCompte) {

        String proprietaire = securityContext.getUserPrincipal().getName();
        BusinessTraceContext.getclear().put(BusinessTraceContextKeyEnum.USER, proprietaire).put(BusinessTraceContextKeyEnum.COMPTE, idCompte);

        LOG.info("getCompte");
        return this.services.getCompteById(idCompte, proprietaire)
                .invoke(compte -> LOG.info("Compte chargé : [{}]", compte != null ? compte.getLibelle() : "-1"));
    }

    @Override
    @ServerRequestFilter(preMatching = true)
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        super.preMatchingFilter(requestContext);
    }
    @Override
    @ServerResponseFilter
    public void postMatchingFilter(ContainerResponseContext responseContext) { super.postMatchingFilter(responseContext); }
}