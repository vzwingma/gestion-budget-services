package com.terrier.finances.gestion.data;

import java.util.ArrayList;
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
	 * @param annee année
	 * @return le nom de la collection
	 */
	protected String getBudgetCollectionName(int annee){
		StringBuilder collectionName = new StringBuilder("budget_").append(annee);
		LOGGER.debug("Utilisation de la collection [{}]", collectionName);
		return  collectionName.toString();
	}
	/**
	 * @param annee année
	 * @return le nom de la collection
	 */
	protected String getBudgetCollectionName(String idBudget){
		if(idBudget != null){
			String[] idParts = idBudget.split("_");
			StringBuilder collectionName = new StringBuilder("budget_").append(idParts[1]);
			LOGGER.debug("Utilisation de la collection [{}]", collectionName);
			return  collectionName.toString();	
		}
		LOGGER.error("Erreur lors de la recheche du nom de la collection associée à [{}]", idBudget);

		return null;
	}


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
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(annee));
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
		try {
			BudgetMensuel budgetMensuel = chargeBudgetMensuelById(idBudget);
			if(budgetMensuel != null){
				return budgetMensuel.getDateMiseAJour() != null ? budgetMensuel.getDateMiseAJour().getTime() : null;
			}
		} catch (BudgetNotFoundException e) {
			LOGGER.error("Erreur lors de la recherche du budget [{}]", idBudget);
		}
		return null;
	}

	/**
	 * Chargement du budget par id
	 * @param idBudget identifiant du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuelById(String idBudget) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget d'id {}", idBudget);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is(idBudget));
		queryBudget.limit(1);
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(idBudget));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException();
		}
		return  dataTransformerBudget.transformDTOtoBO(budgetDTO);
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
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(annee));
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

		List<BudgetMensuelDTO> budgets = new ArrayList<BudgetMensuelDTO>();

		// Année courante
		Calendar annee = Calendar.getInstance();
		for (int a = 2014; a <= annee.get(Calendar.YEAR); a++) {
			try{
				budgets.addAll(getMongoOperation().find(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a)));
			}
			catch(Exception e){
				LOGGER.error("Erreur lors du chargement", e);
			}
		}
		if(budgets.isEmpty()){
			LOGGER.error("Erreur lors du chargement");
			throw new DataNotFoundException("Erreur lors du chargement des budgets");
		}
		else{
			return budgets;
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
			BudgetMensuelDTO premierbudget = null;
			for (int a = 2014; a <= Calendar.getInstance().get(Calendar.YEAR); a++) {
				premierbudget = getMongoOperation().findOne(query1erBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a));
				if(premierbudget != null){
					break;
				}				
			}
			Calendar premier = Calendar.getInstance();
			if(premierbudget != null){
				premier.set(Calendar.MONTH, premierbudget.getMois());
				premier.set(Calendar.YEAR, premierbudget.getAnnee());
			}
			BudgetMensuelDTO dernierbudget = null;

			for (int a = Calendar.getInstance().get(Calendar.YEAR); a >= 2014; a--) {
				dernierbudget = getMongoOperation().findOne(query1erBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a));
				if(dernierbudget != null){
					break;
				}				
			}
			Calendar dernier = Calendar.getInstance();
			if(dernierbudget != null){
				dernier.set(Calendar.MONTH, dernierbudget.getMois());
				dernier.set(Calendar.YEAR, dernierbudget.getAnnee());
			}
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
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(idBudget));
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
			getMongoOperation().save(budgetDTO, getBudgetCollectionName(budgetBO.getAnnee()));
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
