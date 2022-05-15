package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;

/**
 * Service Provider Interface pour fournir les opérations
 * @author vzwingma
 *
 */
public interface IOperationRepository extends ReactivePanacheMongoRepository<CategorieOperations> {

}
