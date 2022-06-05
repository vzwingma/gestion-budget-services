package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;

import java.time.LocalDate;
import java.time.Month;

/**
 *  Application Provider Interface de Budgets
 */
public interface IBudgetAppProvider {


    /**
     * Chargement du budget du mois courant
     *
     * @param idCompte compte
     * @param mois     mois
     * @param annee    année
     * @return budget mensuel chargé et initialisé à partir des données précédentes
     */
    Uni<BudgetMensuel> getBudgetMensuel(String idCompte, Month mois, int annee) ;
    /**
     * Charger budget
     *
     * @param idBudget id du budget
     * @return budget correspondant aux paramètres
     */
    Uni<BudgetMensuel> getBudgetMensuel(String idBudget);
    /**
     * Chargement du budget et du compte en double Uni
     * @param idBudget id du budget
     * @return tuple (budget, compte)
     */
    Uni<Tuple2<BudgetMensuel, CompteBancaire>> getBudgetAndCompte(String idBudget);

    /**
     * Mise à jour d'une ligne de dépense
     *
     * @param idBudget       identifiant de budget
     * @param ligneOperation ligne de dépense
     */
    Uni<BudgetMensuel> addOperationInBudget(String idBudget, final LigneOperation ligneOperation) ;

    /**
     * Suppression d'une opération
     *
     * @param idBudget    identifiant de budget
     * @param idOperation ligne opération
     */
    Uni<BudgetMensuel> deleteOperationInBudget(String idBudget, String idOperation);

    /**
     * Réinitialiser un budget mensuel
     *
     * @param idBudget budget mensuel
     */
    Uni<BudgetMensuel> reinitialiserBudgetMensuel(String idBudget) ;

    /**
     * Chargement de l'état du budget du mois courant en consultation
     * @param idBudget id budget
     * @return budget mensuel chargé et initialisé à partir des données précédentes
     */
    Uni<Boolean> isBudgetMensuelActif(String idBudget);

    /**
     * Lock/unlock d'un budget
     *
     * @param budgetActif etat du budget
     */
    Uni<BudgetMensuel> setBudgetActif(String idBudgetMensuel, boolean budgetActif);
    /**
     * Indique si l'IHM est out of date
     * @param idBudget identifiant du budget
     * @param dateSurIHM Date affichée
     * @return si le budget doit être mis à jour
     */
    Uni<Boolean> isBudgetIHMUpToDate(String idBudget, Long dateSurIHM);

    /**
     * Calcul du résumé
     *
     * @param budget budget à calculer
     */
    void recalculSoldes(BudgetMensuel budget);

    /**
     * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
     * @param idCompte id du compte
     * @return la date du premier budget décrit pour cet utilisateur
     */
    Uni<LocalDate[]> getIntervallesBudgets(String idCompte);

}