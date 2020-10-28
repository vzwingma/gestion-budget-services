package com.terrier.finances.gestion.services.parametrages.business.port;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;

import java.util.List;

/**
 * Interface API
 */
public interface IParametrageRequest {


    /**
     * @return liste des catégories
     */
    public List<CategorieOperation> getCategories();
}
