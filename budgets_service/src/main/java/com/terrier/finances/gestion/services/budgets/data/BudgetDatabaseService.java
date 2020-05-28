package com.terrier.finances.gestion.services.budgets.data;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;

/**
 * DAO Dépenses vers MongoDB
 * @author vzwingma
 *
 */
@Repository
public class BudgetDatabaseService extends AbstractDatabaseService<BudgetMensuel> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetDatabaseService.class);

	private static final String ATTRIBUT_COMPTE_ID = "idCompteBancaire";
	private static final String ATTRIBUT_ANNEE = "annee";
	private static final String ATTRIBUT_MOIS = "mois";

	/**
	 * Chargement du budget mensuel
	 * @param mois mois du budget
	 * @param annee année du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget {}/{} du compte {} ", mois, annee, compte.getId());
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte.getId()).and(ATTRIBUT_MOIS).is(mois.toString()).and(ATTRIBUT_ANNEE).is(annee));
		BudgetMensuel budget = null;
		try{
			budget = findOneByQuery(queryBudget);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget mensuel", e);
		}
		if(budget == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(compte.getId()).append(" du ").append(mois).append("/").append(annee).toString());
		}
		LOGGER.debug("\t> Réception du budget {}. {} opérations", budget.getId(), budget.getListeOperations().size());
		return budget;
	}


	/**
	 * Activité Budget
	 * @param idBudget id budget
	 * @return budget actif
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public boolean isBudgetActif(String idBudget) throws BudgetNotFoundException{

		boolean actif = false;
		BudgetMensuel budgetMensuel = chargeBudgetMensuel(idBudget);
		actif = budgetMensuel != null && budgetMensuel.isActif();
		LOGGER.debug("Activité du budget {} : {}", idBudget, actif);
		return actif;
	}


	/**
	 * Chargement du budget par id
	 * @param idBudget identifiant du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuel(String idBudget) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget [{}]", idBudget);
		BudgetMensuel budgetDTO = null;
		try{
			budgetDTO = findById(idBudget);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget du compte {}", idBudget, e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du budget ").append(idBudget).toString());
		}
		LOGGER.debug("\t> Réception du Budget : {}", budgetDTO.getId());
		return budgetDTO;
	}

	/**
	 * @param idCompte compte
	 * @return liste des budgets associés
	 * @throws DataNotFoundException erreur
	 */
	public List<BudgetMensuel> chargeBudgetsMensuelsDTO(String idCompte) throws DataNotFoundException{
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(idCompte));

		List<BudgetMensuel> budgets = new ArrayList<>();
		try{
			budgets.addAll(findByQuery(queryBudget));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des budgets du compte {}", idCompte, e);
		}
		if(budgets.isEmpty()){
			LOGGER.error("Erreur lors du chargement des budgets du compte {} : la liste est vide", idCompte);
			throw new DataNotFoundException("Erreur lors du chargement des budgets");
		}
		else{
			return budgets;
		}

	}


	/**
	 * 
	 * @param idBudget
	 * @return liste des dépenses du budget
	 * @throws DataNotFoundException erreur
	 */
	public List<LigneOperation> chargerLignesDepenses(String idBudget) throws DataNotFoundException{
		LOGGER.info("Chargement du budget {} ", idBudget);
		try{
			BudgetMensuel budgetDTO = findById(idBudget);
			if(budgetDTO != null && budgetDTO.getListeOperations() != null){
				return budgetDTO.getListeOperations();
			}
			else{
				throw new DataNotFoundException("Aucune donnée trouvée");	
			}
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des opérations de {}", idBudget, e);
			throw new DataNotFoundException("Erreur lors du chargement des opérations de " + idBudget);
		}
	}


	/**
	 * Sauvegarde du budget mensuel
	 * @param budgetMensuelCourant budget à sauvegarder
	 * @param mois mois
	 * @param annee année
	 * @return résultat de la sauvegarde: id du budget
	 */
	public String sauvegardeBudgetMensuel(BudgetMensuel budget){
		if(budget == null){
			return null;
		}
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budget.getIdCompteBancaire(), budget.getMois(), budget.getAnnee());
		try{
			save(budget);
			LOGGER.info("Budget {} sauvegardé ", budget.getId());
			return budget.getId();
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la sauvegarde du compte", e);
			return null;
		}
	}


	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param compte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public BudgetMensuel[] getPremierDernierBudgets(String compte) throws DataNotFoundException{
		try{
			Query query1erBudget = new Query();
			query1erBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
			query1erBudget.with(Sort.by(Sort.Direction.ASC, ATTRIBUT_ANNEE, ATTRIBUT_MOIS));
			query1erBudget.limit(1);
			
			BudgetMensuel premierbudget = findOneByQuery(query1erBudget);
			LOGGER.debug("Premier budget trouvé -> {}", premierbudget);
			
			Query querydernierBudget = new Query();
			querydernierBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
			querydernierBudget.with(Sort.by(Sort.Direction.DESC, ATTRIBUT_ANNEE, ATTRIBUT_MOIS));
			querydernierBudget.limit(1);
			
			BudgetMensuel dernierbudget = findOneByQuery(querydernierBudget);
			LOGGER.debug("Dernier budget trouvé -> {}", dernierbudget);
			return new BudgetMensuel[]{premierbudget, dernierbudget};
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement de la date du premier budget de {}", compte, e);
			throw new DataNotFoundException("Erreur lors du chargement de la date du premier budget de " + compte);
		}
	}

	/**
	 * Chargement des libellés des dépenses
	 * @param annee année du budget
	 * @param idCompte id du compte
	 * @return liste des libellés
	 */
	public Set<String> chargeLibellesOperations(String idCompte, int annee) {
		LOGGER.info("Chargement des libellés des dépenses du compte {} de {}", idCompte, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(idCompte).and(ATTRIBUT_ANNEE).is(annee));
		Set<String> libellesDepenses = new HashSet<>();
		try{
			libellesDepenses = findByQuery(queryBudget)
					.parallelStream()
					// liste dépenses transformées 
					.flatMap(budgetDTO -> budgetDTO.getListeOperations().stream().map(LigneOperation::getLibelle))
					.collect(Collectors.toSet());
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des libellés des dépenses du compte {} de {}", idCompte, annee, e);
		}
		return libellesDepenses;
	}
}
