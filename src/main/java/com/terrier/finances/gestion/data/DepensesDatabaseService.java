package com.terrier.finances.gestion.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.data.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

/**
 * DAO Dépenses vers MongoDB
 * @author vzwingma
 *
 */
@Repository
public class DepensesDatabaseService extends AbstractDatabaseService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DepensesDatabaseService.class);

	@Autowired @Qualifier("dataTransformerBudget")
	private DataTransformerBudget dataTransformerBudget;
	/**
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuel(String idCompte, int mois, int annee) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget du compte {} du {}/{}", idCompte, mois, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("compteBancaire.id").is(idCompte).and("mois").is(mois).and("annee").is(annee));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException();
		}
		LOGGER.debug("	> Réception du DTO : {}", budgetDTO.getId());
		BudgetMensuel budgetMensuel = dataTransformerBudget.transformDTOtoBO(budgetDTO);
		return budgetMensuel;
	}

	
	
	/**
	 * Lecture de la date de mise à jour du budget
	 * @return date de mise à jour
	 */
	public Date getDateMiseAJourBudget(String idBudget) {
		
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is(idBudget));
		queryBudget.limit(1);
		
		BudgetMensuelDTO budgetMensuelDTO = mongoTemplate.findOne(queryBudget, BudgetMensuelDTO.class);
		LOGGER.info("{}", budgetMensuelDTO);
		if(budgetMensuelDTO != null){
			return budgetMensuelDTO.getDateMiseAJour();
		}
		return null;
	}
	
	/**
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuelById(String idBudget) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget d'id {}", idBudget);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is(idBudget));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException();
		}
		BudgetMensuel budgetMensuel = dataTransformerBudget.transformDTOtoBO(budgetDTO);
		return budgetMensuel;
	}
	/**
	 * Chargement du compte
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel du compte pour le mois et l'année
	 */
	public BudgetMensuelDTO chargeBudgetMensuelDTO(String idCompte, int mois, int annee) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget du compte {} du {}/{}", idCompte, mois, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("compteBancaire.id").is(idCompte).and("mois").is(mois).and("annee").is(annee));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException();
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
		queryBudget.addCriteria(Criteria.where("compteBancaire.id").is(idCompte));
		try{
			return getMongoOperation().find(queryBudget, BudgetMensuelDTO.class);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
			throw new DataNotFoundException("Erreur lors du chargement");
		}
	}

	
	
	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param compte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public Calendar[] getDatePremierDernierBudgets(String compte) throws DataNotFoundException{
		Query query1erBudget = new Query();
		query1erBudget.addCriteria(Criteria.where("compteBancaire.id").is(compte));
		query1erBudget.with(new Sort(Sort.Direction.ASC, "annee")).with(new Sort(Sort.Direction.ASC, "mois"));
		query1erBudget.limit(1);
		
		Query querydernierBudget = new Query();
		querydernierBudget.addCriteria(Criteria.where("compteBancaire.id").is(compte));
		querydernierBudget.with(new Sort(Sort.Direction.DESC, "annee")).with(new Sort(Sort.Direction.DESC, "mois"));
		querydernierBudget.limit(1);
		try{
			BudgetMensuelDTO premierbudget = getMongoOperation().findOne(query1erBudget, BudgetMensuelDTO.class);
			Calendar premier = Calendar.getInstance();
			premier.set(Calendar.MONTH, premierbudget.getMois());
			premier.set(Calendar.YEAR, premierbudget.getAnnee());
			
			BudgetMensuelDTO dernierbudget = getMongoOperation().findOne(querydernierBudget, BudgetMensuelDTO.class);
			Calendar dernier = Calendar.getInstance();
			dernier.set(Calendar.MONTH, dernierbudget.getMois());
			dernier.set(Calendar.YEAR, dernierbudget.getAnnee());
			dernier.add(Calendar.MONTH, 1);
			return new Calendar[]{premier, dernier};
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
			throw new DataNotFoundException("Erreur lors du chargement");
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
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is(idBudget));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class);
			return budgetDTO.getListeDepenses();
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
			throw new DataNotFoundException("Erreur lors du chargement");
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
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budgetDTO.getCompteBancaire().getLibelle(), budgetDTO.getMois(), budgetDTO.getAnnee());
		try{
			getMongoOperation().save(budgetDTO);
			LOGGER.info("Budget {} sauvegardé ", budgetDTO.getId());
			return budgetDTO.getId();
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la sauvegarde du compte", e);
			return null;
		}
	}



	/**
	 * @return the dataTransformerBudget
	 */
	public DataTransformerBudget getDataTransformerBudget() {
		return dataTransformerBudget;
	}



	/**
	 * @param dataTransformerBudget the dataTransformerBudget to set
	 */
	public void setDataTransformerBudget(DataTransformerBudget dataTransformerBudget) {
		this.dataTransformerBudget = dataTransformerBudget;
	}
}
