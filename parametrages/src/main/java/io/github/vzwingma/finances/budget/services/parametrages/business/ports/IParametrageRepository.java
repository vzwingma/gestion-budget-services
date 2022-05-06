package io.github.vzwingma.finances.budget.services.parametrages.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;

import java.util.List;
/**
 * Service Provider Interface pour fournir les paramètres
 * @author vzwingma
 *
 */
public interface IParametrageRepository {


    /**
     * @return la liste des catégories
     */
    public List<CategorieOperation> chargeCategories();
}
