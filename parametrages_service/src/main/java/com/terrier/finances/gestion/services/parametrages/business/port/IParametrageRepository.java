package com.terrier.finances.gestion.services.parametrages.business.port;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;

import java.util.List;
import java.util.stream.Collectors;
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
