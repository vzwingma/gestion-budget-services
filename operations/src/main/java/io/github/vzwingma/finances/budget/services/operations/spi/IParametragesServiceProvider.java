package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.operations.api.enums.ParametragesApiUrlEnum;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Service Provider Interface de {@link }
 */
@Path(ParametragesApiUrlEnum.PARAMS_BASE)
@RegisterRestClient(configKey = "parametrages-service")
public interface IParametragesServiceProvider {

    /**
     * Recherche d'une catégorie
     * @param idCategorie de la catégorie
     * @return catégorie correspondante. Null sinon
     */
   @GET
   @Path(ParametragesApiUrlEnum.PARAMS_CATEGORIES + ParametragesApiUrlEnum.PARAMS_CATEGORIE_ID)
   Uni<CategorieOperations> getCategorieParId(String idCategorie);

    /**
     * Liste des catégories
     * @return liste de catégories
     */
    @GET
    @Path(ParametragesApiUrlEnum.PARAMS_CATEGORIES)
    Uni<List<CategorieOperations>> getCategories();
}
