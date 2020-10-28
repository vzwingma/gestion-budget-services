package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Provider Interface des Opérations
 */
public interface IOperationsRepository {

    /**
     * Chargement du budget mensuel
     * @param mois mois du budget
     * @param annee année du budget
     * @return budget mensuel
     */
    public BudgetMensuel chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException;

    /**
     * Activité Budget
     * @param idBudget id budget
     * @return budget actif
     * @throws BudgetNotFoundException budget introuvable
     */
    public boolean isBudgetActif(String idBudget) throws BudgetNotFoundException;


    /**
     * Chargement du budget par id
     * @param idBudget identifiant du budget
     * @return budget mensuel
     */
    public BudgetMensuel chargeBudgetMensuel(String idBudget) throws BudgetNotFoundException;

    /**
     * @param idCompte compte
     * @return liste des budgets associés
     * @throws DataNotFoundException erreur
     */
    public List<BudgetMensuel> chargeBudgetsMensuelsDTO(String idCompte) throws DataNotFoundException;


    /**
     *
     * @param idBudget
     * @return liste des dépenses du budget
     * @throws DataNotFoundException erreur
     */
    public List<LigneOperation> chargerLignesDepenses(String idBudget) throws DataNotFoundException;

    /**
     * Sauvegarde du budget mensuel
     * @param budget budget à sauvegarder
     * @return résultat de la sauvegarde: id du budget
     */
    public String sauvegardeBudgetMensuel(BudgetMensuel budget);


    /**
     * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
     * @param compte id du compte
     * @return la date du premier budget décrit pour cet utilisateur
     */
    public BudgetMensuel[] getPremierDernierBudgets(String compte) throws DataNotFoundException;

    /**
     * Chargement des libellés des dépenses
     * @param annee année du budget
     * @param idCompte id du compte
     * @return liste des libellés
     */
    public Set<String> chargeLibellesOperations(String idCompte, int annee);

}
