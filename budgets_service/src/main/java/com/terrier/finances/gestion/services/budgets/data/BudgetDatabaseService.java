package com.terrier.finances.gestion.services.budgets.data;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budgets.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.budgets.model.LigneDepenseDTO;
import com.terrier.finances.gestion.services.budgets.model.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;

/**
 * DAO Dépenses vers MongoDB
 * @author vzwingma
 *
 */
@Repository
public class BudgetDatabaseService extends AbstractDatabaseService<BudgetMensuelDTO> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetDatabaseService.class);

	@Autowired @Qualifier("dataTransformerBudget")
	private DataTransformerBudget dataTransformerBudget;

	private static final String ATTRIBUT_COMPTE_ID = "compteBancaire.id";
	private static final String ATTRIBUT_ANNEE = "annee";
	private static final String ATTRIBUT_MOIS = "mois";

	/**
	 * Chargement du budget mensuel
	 * @param mois mois du budget
	 * @param annee année du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget du compte {} du {}/{}", compte.getId(), mois, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte.getId()).and(ATTRIBUT_MOIS).is(mois.getValue() -1).and(ATTRIBUT_ANNEE).is(annee));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = findOneByQuery(queryBudget);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget mensuel", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(compte.getId()).append(" du ").append(mois).append("/").append(annee).toString());
		}
		LOGGER.debug("	> Réception du DTO : {}. {} opérations", budgetDTO.getId(), budgetDTO.getListeDepenses().size());
		return dataTransformerBudget.transformDTOtoBO(budgetDTO);
	}


	/**
	 * Activité Budget
	 * @param idBudget id budget
	 * @return budget actif
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public boolean isBudgetActif(String idBudget) throws BudgetNotFoundException{

		boolean actif = false;
		BudgetMensuelDTO budgetMensuel = chargeBudgetMensuelDTO(idBudget);
		actif = budgetMensuel != null && budgetMensuel.isActif();
		LOGGER.debug("Activité du budget {} : {}", idBudget, actif);
		return actif;
	}


	/**
	 * Chargement du budget par id
	 * @param idBudget identifiant du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuelById(String idBudget) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget d'id {}", idBudget);
		BudgetMensuelDTO budgetDTO = chargeBudgetMensuelDTO(idBudget);
		return  dataTransformerBudget.transformDTOtoBO(budgetDTO);
	}
	/**
	 * Chargement du compte
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel du compte pour le mois et l'année
	 */
	public BudgetMensuelDTO chargeBudgetMensuelDTO(String idBudget) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget [{}]", idBudget);
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = findById(idBudget);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget du compte {}", idBudget, e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du budget ").append(idBudget).toString());
		}
		LOGGER.debug("	> Réception du DTO : {}", budgetDTO.getId());
		return budgetDTO;
	}

	/**
	 * @param idCompte compte
	 * @return liste des budgets associés
	 * @throws DataNotFoundException erreur
	 */
	public List<BudgetMensuelDTO> chargeBudgetsMensuelsDTO(String idCompte) throws DataNotFoundException{
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(idCompte));

		List<BudgetMensuelDTO> budgets = new ArrayList<>();
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
	public List<LigneDepenseDTO> chargerLignesDepenses(String idBudget) throws DataNotFoundException{
		LOGGER.info("Chargement du budget {} ", idBudget);
		try{
			BudgetMensuelDTO budgetDTO = findById(idBudget);
			if(budgetDTO != null && budgetDTO.getListeDepenses() != null){
				return budgetDTO.getListeDepenses();
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
	public String sauvegardeBudgetMensuel(BudgetMensuel budgetBO){
		if(budgetBO == null){
			return null;
		}
		BudgetMensuelDTO budgetDTO = dataTransformerBudget.transformBOtoDTO(budgetBO);
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budgetDTO.getCompteBancaire().getLibelle(), budgetDTO.getMois() + 1, budgetDTO.getAnnee());
		try{
			save(budgetDTO);
			LOGGER.info("Budget {} sauvegardé ", budgetDTO.getId());
			return budgetDTO.getId();
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
	public BudgetMensuelDTO[] getPremierDernierBudgets(String compte) throws DataNotFoundException{
		try{
			Query query1erBudget = new Query();
			query1erBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
			query1erBudget.with(Sort.by(Sort.Direction.ASC, ATTRIBUT_ANNEE, ATTRIBUT_MOIS));
			query1erBudget.limit(1);
			
			BudgetMensuelDTO premierbudget = findOneByQuery(query1erBudget);
			LOGGER.debug("Premier budget trouvé -> {}", premierbudget);
			
			Query querydernierBudget = new Query();
			querydernierBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
			querydernierBudget.with(Sort.by(Sort.Direction.DESC, ATTRIBUT_ANNEE, ATTRIBUT_MOIS));
			querydernierBudget.limit(1);
			
			BudgetMensuelDTO dernierbudget = findOneByQuery(querydernierBudget);
			LOGGER.debug("Dernier budget trouvé -> {}", dernierbudget);
			return new BudgetMensuelDTO[]{premierbudget, dernierbudget};
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
					.flatMap(budgetDTO -> budgetDTO.getListeDepenses().stream().map(LigneDepenseDTO::getLibelle))
					.collect(Collectors.toSet());
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des libellés des dépenses du compte {} de {}", idCompte, annee, e);
		}
		return libellesDepenses;
	}
	
	/**
	 * @return the dataTransformerBudget
	 */
	public DataTransformerBudget getDataTransformerBudget() {
		return dataTransformerBudget;
	}
}
