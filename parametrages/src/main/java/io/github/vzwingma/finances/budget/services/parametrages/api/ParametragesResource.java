package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRequest;
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
import java.util.List;
/**
 * Controleur REST -
 * Adapteur du port {@link io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRequest}
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.PARAMS_BASE)
public class ParametragesResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParametragesResource.class);


    @Inject
    private IParametrageRequest paramsServices;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return "Hello Parametrages";
    }


    /**
     * @return la liste des catégories d'opérations
     **/
     // @throws DataNotFoundException erreur données non trouvées
    @Operation(description = "Liste les catégories d'opérations", summary = "Liste des catégories d'opérations")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategorieOperation.class)) }),
            @APIResponse(responseCode = "401", description = "L'action n'est pas authentifiée"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Session introuvable")
    })
    @GET
    @Path(BudgetApiUrlEnum.PARAMS_CATEGORIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategorieOperation> getCategories() {

        List<CategorieOperation> listeCategories = paramsServices.getCategories();

        LOG.info("Chargement des {} Categories", listeCategories != null ? listeCategories.size() : "-1");
        return listeCategories;
    }

}