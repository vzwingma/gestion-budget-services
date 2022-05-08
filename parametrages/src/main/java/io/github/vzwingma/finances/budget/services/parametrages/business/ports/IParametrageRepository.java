package io.github.vzwingma.finances.budget.services.parametrages.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;

/**
 * Service Provider Interface pour fournir les paramètres
 * @author vzwingma
 *
 */
public interface IParametrageRepository extends ReactivePanacheMongoRepository<CategorieOperation> {


    /**
     * @return la liste des catégories
     */
    public Multi<CategorieOperation> chargeCategories();
}
