package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;

/**
 * Service Provider Interface de {@link com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient}
 */
public interface IParametragesServiceProvider {

    /**
     * Recherche d'une catégorie
     * @param id de la catégorie
     * @return catégorie correspondante. Null sinon
     */
    public CategorieOperation getCategorieParId(String id);
}
