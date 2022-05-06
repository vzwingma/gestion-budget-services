package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path(BudgetApiUrlEnum.PARAMS_BASE)
public class ParametragesResource {

    private static final Logger LOG = Logger.getLogger(ParametragesResource.class);

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

        List<CategorieOperation> listeCategories = new ArrayList<>(); // paramsServices.getCategories();
        CategorieOperation c1 = new CategorieOperation();
        c1.setId("1");
        c1.setLibelle("Categorie 1");
        listeCategories.add(c1);
        c1.setCategorie(true);
        LOG.infof("Chargement des %d Categories", listeCategories != null ? listeCategories.size() : "-1");
        return listeCategories;
    }

}