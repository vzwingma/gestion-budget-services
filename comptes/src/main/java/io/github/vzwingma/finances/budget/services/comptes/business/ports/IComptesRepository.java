package io.github.vzwingma.finances.budget.services.comptes.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;

/**
 * Service Provider Interface pour fournir les paramètres
 * @author vzwingma
 *
 */
public interface IComptesRepository extends ReactivePanacheMongoRepository<CategorieOperations> {


}
