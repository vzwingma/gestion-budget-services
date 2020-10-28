package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

/**
 *  Application Provider Interface de Comptes
 */
public interface IOperationsRequest {

    /**
     * Chargement du budget du mois courant
     * @param idCompte compte
     * @param mois mois
     * @param annee année
     * @return budget mensuel chargé et initialisé à partir des données précédentes
     */
    public BudgetMensuel chargerBudgetMensuel(String idCompte, Month mois, int annee, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException;
    /**
     * Charger budget
     * @param idProprietaire
     * @param idBudget
     * @return budget correspondant aux paramètres
     * @throws UserNotAuthorizedException utilisateur non autorisé
     * @throws BudgetNotFoundException budget introuvable
     * @throws DataNotFoundException données introuvables
     */
    public BudgetMensuel chargerBudgetMensuel(String idBudget, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException;
    /**
     * Réinitialiser un budget mensuel
     * @param idBudget budget mensuel
     * @param idProprietaire propriétaire du budget
     * @throws DataNotFoundException  erreur sur les données
     * @throws BudgetNotFoundException budget introuvable
     */
    public BudgetMensuel reinitialiserBudgetMensuel(String idBudget, String idProprietaire) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException;

    /**
     * Chargement de l'état du budget du mois courant en consultation
     * @param idBudget id budget
     * @return budget mensuel chargé et initialisé à partir des données précédentes
     * @throws BudgetNotFoundException budget introuvable
     */
    public boolean isBudgetMensuelActif(String idBudget) throws BudgetNotFoundException;

        /**
         * Lock/unlock d'un budget
         *
         * @param budgetActif etat du budget
         * @throws BudgetNotFoundException erreur budget introuvable
         */
    public BudgetMensuel setBudgetActif(String idBudgetMensuel, boolean budgetActif, String idProprietaire) throws BudgetNotFoundException;
    /**
     * Indique si l'IHM est out of date
     * @param idBudget identifiant du budget
     * @param dateSurIHM Date affichée
     * @return si le budget doit être mis à jour
     */
    public boolean isBudgetIHMUpToDate(String idBudget, Long dateSurIHM);

    /**
     * Réinjection des catégories dans les opérations du budget
     * @param operation opération
     * @param categories liste des catégories
     */
    public void completeCategoriesOnOperation(LigneOperation operation, List<CategorieOperation> categories);
    /**
     * Charge les libelles des opérations
     * @param idCompte
     * @param annee
     * @return liste des libelles opérations
     */
    public Set<String> getLibellesOperations(String idCompte, int annee);
    /**
     * Ajout d'une ligne transfert intercompte
     * @param ligneOperation ligne de dépense de transfert
     * @param idCompteDestination compte créditeur
     * @param idProprietaire auteur de l'action
     * @throws BudgetNotFoundException erreur budget introuvable
     * @throws DataNotFoundException erreur données
     * @throws CompteClosedException  compte clos
     */
    public BudgetMensuel createOperationIntercompte(String idBudget, LigneOperation ligneOperation, String idCompteDestination, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException, CompteClosedException;
        /**
         * Calcul du résumé
         *
         * @param budget budget à calculer
         */
    public void calculBudget(BudgetMensuel budget);

    /**
     * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
     * @param idCompte id du compte
     * @return la date du premier budget décrit pour cet utilisateur
     */
    public LocalDate[] getIntervallesBudgets(String idCompte) throws DataNotFoundException;
    /**
     * Mise à jour de la ligne comme dernière opération
     * @param ligneId
     */
    public boolean setLigneAsDerniereOperation(String idBudget, String ligneId, String idProprietaire);

    /**
     * Suppression d'une opération
     * @param idBudget identifiant de budget
     * @param idOperation ligne opération
     * @param idProprietaire userSession
     * @throws DataNotFoundException
     * @throws BudgetNotFoundException
     * @throws CompteClosedException compte clos
     */
    public BudgetMensuel deleteOperation(String idBudget, String idOperation, String idProprietaire) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException;

    /**
     * Mise à jour d'une ligne de dépense
     * @param idBudget identifiant de budget
     * @param ligneOperation ligne de dépense
     * @param idProprietaire idProprietaire
     * @throws DataNotFoundException
     * @throws BudgetNotFoundException
     * @throws CompteClosedException compte clos
     */
    public BudgetMensuel updateOperationInBudget(String idBudget, final LigneOperation ligneOperation, String idProprietaire) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException;
}