package io.github.vzwingma.finances.budget.services.parametrages.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.smallrye.mutiny.Uni;

import java.util.List;

/**
 *  Application Provider Interface de Paramétrages
 */
public interface IParametrageRequest {


    /**
     * @return liste des catégories depuis le service (hexagone business)
     */
    Uni<List<CategorieOperation>> getCategories();
}
