package io.github.vzwingma.finances.budget.services.operations.business.ports;

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
     * Charge les libelles des opérations
     * @param idCompte identifiant du compte
     * @param annee annee du compte
     * @return liste des libelles opérations
     */
    Multi<String> getLibellesOperations(String idCompte, int annee);
    /**
     * Ajout d'une ligne transfert intercompte
     *
     * @param operations            liste des opérations à mettre à jour budget
     * @param ligneOperationSource  ligne de dépense, source, pour créer une nouvelle opération
     * @param libelleOperationCible libelle de la nouvelle opération
     * @param auteur                auteur de l'action
     * @return liste des opérations à mettre à jour dans le budget, avec l'intercompte
     */
    List<LigneOperation> addOperationIntercompte(List<LigneOperation> operations, LigneOperation ligneOperationSource, String libelleOperationCible, String auteur);
    /**
     * Mise à jour de la ligne comme dernière opération
     *
     * @param ligneId identifiant de ligne
     */
    Uni<Boolean> setLigneAsDerniereOperation(String idBudget, String ligneId);

    /**
     * Suppression d'une opération
     *
     * @param operations  identifiant de budget
     * @param idOperation ligne opération
     */
    void deleteOperation(List<LigneOperation> operations, String idOperation);

    /**
     * Mise à jour d'une ligne de dépense dans la liste d'un budget
     *
     * @param operations     liste des opérations à mettre à jour budget
     * @param auteur         auteur de l'action
     * @param ligneOperation ligne de dépense
     */
    List<LigneOperation> addOrReplaceOperation(List<LigneOperation> operations, LigneOperation ligneOperation, String auteur);

    /**
     * Ajout d'une opération de remboursement
     *
     * @param operationSource     opération source du remboursement
     * @param auteur         auteur de l'action
     */
    Uni<LigneOperation> createOperationRemboursement(LigneOperation operationSource, String auteur);
}
