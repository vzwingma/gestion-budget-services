package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.smallrye.mutiny.Uni;

import java.time.LocalDate;
import java.time.Month;

/**
 *  Application Provider Interface de Budgets
 */
public interface IBudgetAppProvider {


    /**
     * Chargement du budget du mois courant
     * @param idCompte compte
     * @param mois mois
     * @param annee année
     * @return budget mensuel chargé et initialisé à partir des données précédentes
     */
    Uni<BudgetMensuel> getBudgetMensuel(String idCompte, Month mois, int annee, String idProprietaire) ;
    /**
     * Charger budget
     * @param idProprietaire id du propriétaire
     * @param idBudget id du budget
     * @return budget correspondant aux paramètres
     */
    Uni<BudgetMensuel> getBudgetMensuel(String idBudget, String idProprietaire);
    /**
     * Réinitialiser un budget mensuel
     * @param idBudget budget mensuel
     * @param idProprietaire propriétaire du budget
     */
    Uni<BudgetMensuel> reinitialiserBudgetMensuel(String idBudget, String idProprietaire) ;

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
    Uni<BudgetMensuel> setBudgetActif(String idBudgetMensuel, boolean budgetActif, String idProprietaire);
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
    void calculBudget(BudgetMensuel budget);

    /**
     * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
     * @param idCompte id du compte
     * @return la date du premier budget décrit pour cet utilisateur
     */
    Uni<LocalDate[]> getIntervallesBudgets(String idCompte);

}
