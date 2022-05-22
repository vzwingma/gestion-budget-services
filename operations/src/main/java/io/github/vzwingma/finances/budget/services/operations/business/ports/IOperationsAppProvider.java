package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.TotauxCategorie;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

/**
 *  Application Provider Interface des opérations
 */
public interface IOperationsAppProvider {

    /**
     * Calcul du résumé
     *
     * @param operations opérations
     * @param soldes soldes
     * @param totauxCategorieMap map des totaux par catégorie
     * @param totauxSsCategoriesMap map des totaux par sous catégorie
     */
    void calculSoldes(List<LigneOperation> operations, BudgetMensuel.Soldes soldes, Map<String, TotauxCategorie> totauxCategorieMap, Map<String, TotauxCategorie> totauxSsCategoriesMap);
    /**
     * Réinjection des catégories dans les opérations du budget
     * @param operation opération
     * @param categories liste des catégories
     */
    void completeCategoriesOnOperation(LigneOperation operation, List<CategorieOperations> categories);
    /**
     * Charge les libelles des opérations
     * @param idCompte identifiant du compte
     * @param annee annee du compte
     * @return liste des libelles opérations
     */
    Multi<String> getLibellesOperations(String idCompte, int annee);
    /**
     * Ajout d'une ligne transfert intercompte
     * @param ligneOperation ligne de dépense de transfert
     * @param idCompteDestination compte créditeur
     * @param idProprietaire auteur de l'action
     */
    Uni<BudgetMensuel> createOperationIntercompte(String idBudget, LigneOperation ligneOperation, String idCompteDestination, String idProprietaire);
    /**
     * Mise à jour de la ligne comme dernière opération
     * @param ligneId identifiant de ligne
     */
    Uni<Boolean> setLigneAsDerniereOperation(String idBudget, String ligneId, String idProprietaire);

    /**
     * Suppression d'une opération
     * @param idBudget identifiant de budget
     * @param idOperation ligne opération
     * @param idProprietaire userSession

     */
    Uni<BudgetMensuel> deleteOperation(String idBudget, String idOperation, String idProprietaire);

    /**
     * Mise à jour d'une ligne de dépense
     * @param idBudget identifiant de budget
     * @param ligneOperation ligne de dépense
     * @param idProprietaire idProprietaire

     */
    Uni<BudgetMensuel> updateOperationInBudget(String idBudget, final LigneOperation ligneOperation, String idProprietaire) ;
}
