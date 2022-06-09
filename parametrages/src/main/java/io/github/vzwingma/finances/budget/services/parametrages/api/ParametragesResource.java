package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPILoggerInterceptor;
import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContextKeyEnum;
import io.github.vzwingma.finances.budget.services.parametrages.api.enums.ParametragesApiUrlEnum;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageAppProvider;
import io.smallrye.mutiny.Uni;
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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Controleur REST -
 * Adapteur du port {@link IParametrageAppProvider}
 * @author vzwingma
 *
 */
@Path(ParametragesApiUrlEnum.PARAMS_BASE)
public class ParametragesResource extends AbstractAPILoggerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ParametragesResource.class);
    @Inject
    IParametrageAppProvider paramsServices;


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
    @Path(ParametragesApiUrlEnum.PARAMS_CATEGORIES)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<CategorieOperations>> getCategories() {

        BusinessTraceContext.getclear().put(BusinessTraceContextKeyEnum.USER, "User");
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
    @Path(ParametragesApiUrlEnum.PARAMS_CATEGORIES + ParametragesApiUrlEnum.PARAMS_CATEGORIE_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CategorieOperations> getCategorieById(@RestPath String idCategorie) {

        BusinessTraceContext.getclear().put(BusinessTraceContextKeyEnum.USER, "User");

        return paramsServices.getCategorieById(idCategorie)
                .invoke(categorie -> LOG.info("[idCategorie={}] Chargement de la {}catégorie : {}", idCategorie, categorie != null && categorie.isCategorie() ? "" : "sous-", categorie))
                .invoke(l -> BusinessTraceContext.get().remove(BusinessTraceContextKeyEnum.USER));
    }

    @ServerRequestFilter(preMatching = true)
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        super.preMatchingFilter(requestContext);
    }
    @ServerResponseFilter
    public void postMatchingFilter(ContainerResponseContext responseContext) { super.postMatchingFilter(responseContext); }

}