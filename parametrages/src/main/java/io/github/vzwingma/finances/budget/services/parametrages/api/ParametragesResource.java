package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIInterceptors;
import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContextKeyEnum;
import io.github.vzwingma.finances.budget.services.parametrages.api.enums.ParametragesAPIEnum;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageAppProvider;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
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
 * Adapteur du port {@link IParametrageAppProvider}
 * @author vzwingma
 *
 */
@Path(ParametragesAPIEnum.PARAMS_BASE)
public class ParametragesResource extends AbstractAPIInterceptors {

    private static final Logger LOG = LoggerFactory.getLogger(ParametragesResource.class);
    @Inject
    IParametrageAppProvider paramsServices;

    @Context
    SecurityContext securityContext;

    /**
     * @return la liste des catégories d'opérations
     **/
    @Operation(description = "Liste les catégories d'opérations", summary = "Liste des catégories d'opérations")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategorieOperations.class)) }),
            @APIResponse(responseCode = "401", description = "L'action n'est pas authentifiée"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @PermitAll
    @Path(ParametragesAPIEnum.PARAMS_CATEGORIES)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<CategorieOperations>> getCategories() {

        BusinessTraceContext.getclear().put(BusinessTraceContextKeyEnum.USER, securityContext.getUserPrincipal().getName());
        return paramsServices.getCategories()
                .invoke(listeCategories -> LOG.info("Chargement des {} Categories", listeCategories != null ? listeCategories.size() : "-1"))
                .invoke(l -> BusinessTraceContext.get().remove(BusinessTraceContextKeyEnum.USER));
    }




    /**
     * @return catégorie d'opérations correspondant à l'id
     **/
    @Operation(description = "Catégorie d'opérations par son id", summary = "Catégories d'opérations par son id")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategorieOperations.class)) }),
            @APIResponse(responseCode = "401", description = "L'action n'est pas authentifiée"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @PermitAll
    @Path(ParametragesAPIEnum.PARAMS_CATEGORIES + ParametragesAPIEnum.PARAMS_CATEGORIE_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CategorieOperations> getCategorieById(@RestPath String idCategorie) {
        BusinessTraceContext.getclear().put(BusinessTraceContextKeyEnum.USER, securityContext.getUserPrincipal().getName());

        return paramsServices.getCategorieById(idCategorie)
                .invoke(categorie -> LOG.info("[idCategorie={}] Chargement de la {}catégorie : {}", idCategorie, categorie != null && categorie.isCategorie() ? "" : "sous-", categorie))
                .invoke(l -> BusinessTraceContext.get().remove(BusinessTraceContextKeyEnum.USER));
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