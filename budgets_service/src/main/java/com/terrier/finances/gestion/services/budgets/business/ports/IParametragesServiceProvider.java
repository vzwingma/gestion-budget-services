package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.services.budgets.spi.ComptesServiceHTTPAdaptator;
import com.terrier.finances.gestion.services.communs.business.ports.IServiceProvider;

import java.util.List;

/**
 * Service Provider Interface de {@link ComptesServiceHTTPAdaptator}
 */
public interface IParametragesServiceProvider extends IServiceProvider {

    /**
     * Recherche d'une catégorie
     * @param id de la catégorie
     * @return catégorie correspondante. Null sinon
     */
    public CategorieOperation getCategorieParId(String id);

    /**
     * Liste des catégories
     * @return liste de catégories
     */
    public List<CategorieOperation> getCategories();
}
