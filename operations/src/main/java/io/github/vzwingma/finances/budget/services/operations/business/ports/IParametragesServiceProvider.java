package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;

import java.util.List;

/**
 * Service Provider Interface de {@link }
 */
public interface IParametragesServiceProvider { //extends IServiceProvider {

    /**
     * Recherche d'une catégorie
     * @param id de la catégorie
     * @return catégorie correspondante. Null sinon
     */
    CategorieOperations getCategorieParId(String id);

    /**
     * Liste des catégories
     * @return liste de catégories
     */
    List<CategorieOperations> getCategories();
}
