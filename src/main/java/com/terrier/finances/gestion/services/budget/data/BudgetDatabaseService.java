package com.terrier.finances.gestion.services.budget.data;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.budget.model.LigneDepenseDTO;
import com.terrier.finances.gestion.services.budget.model.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.services.communs.data.AbstractDatabaseService;

/**
 * DAO Dépenses vers MongoDB
 * @author vzwingma
 *
 */
@Repository
public class BudgetDatabaseService extends AbstractDatabaseService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetDatabaseService.class);

	@Autowired @Qualifier("dataTransformerBudget")
	private DataTransformerBudget dataTransformerBudget;

	private static final String COLLECTION_BUDGET = "budget_";
	private static final String ATTRIBUT_COMPTE_ID = "compteBancaire.id";
	private static final String ATTRIBUT_ANNEE = "annee";
	private static final String ATTRIBUT_MOIS = "mois";
	
	/**
	 * @param annee année
	 * @return le nom de la collection
	 */
	protected String getBudgetCollectionName(int annee){
		StringBuilder collectionName = new StringBuilder(COLLECTION_BUDGET).append(annee);
		LOGGER.trace("Utilisation de la collection [{}]", collectionName);
		return  collectionName.toString();
	}
	/**
	 * @param annee année
	 * @return le nom de la collection
	 */
	protected String getBudgetCollectionName(String idBudget){
		if(idBudget != null){
			String[] idParts = idBudget.split("_");
			StringBuilder collectionName = new StringBuilder(COLLECTION_BUDGET).append(idParts[1]);
			LOGGER.debug("Utilisation de la collection [{}]", collectionName);
			return  collectionName.toString();	
		}
		LOGGER.error("Erreur lors de la recheche du nom de la collection associée à [{}]", idBudget);

		return null;
	}


	/**
	 * Chargement des libellés des dépenses
	 * @param annee année du budget
	 * @param idCompte id du compte
	 * @return liste des libellés
	 */
	public Set<String> chargeLibellesDepenses(String idCompte, int annee, BasicTextEncryptor decryptor) {
		LOGGER.info("Chargement des libellés des dépenses du compte {} de {}", idCompte, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(idCompte).and(ATTRIBUT_ANNEE).is(annee));
		Set<String> libellesDepenses = new HashSet<>();
		try{
			List<BudgetMensuelDTO> budgetsDTO = getMongoOperation().find(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(annee));
			if(budgetsDTO != null){

				budgetsDTO
				.parallelStream()
				// liste dépenses transformées 
				.map(budgetDTO -> getDataTransformerBudget().transformDTOtoBO(budgetDTO, decryptor))
				.forEach(budget -> {
					if(budget != null && budget.getListeOperations() != null && !budget.getListeOperations().isEmpty()){
						budget.getListeOperations()
						.parallelStream()
						.forEach(operation -> {
							if(operation != null){
								libellesDepenses.add(operation.getLibelle());
							}
						});
					}
				});
			}
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des libellés des dépenses du compte {} de {}", idCompte, annee, e);
		}
		return libellesDepenses;
	}



	/**
	 * Chargement du budget mensuel
	 * @param mois mois du budget
	 * @param annee année du budget
	 * @return budget mensuel
	 */
	public BudgetMensuel chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee, BasicTextEncryptor decryptor) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget du compte {} du {}/{}", compte.getId(), mois, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte.getId()).and(ATTRIBUT_MOIS).is(mois.getValue() -1).and(ATTRIBUT_ANNEE).is(annee));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(annee));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget mensuel", e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(compte.getId()).append(" du ").append(mois).append("/").append(annee));
		}
		LOGGER.debug("	> Réception du DTO : {}", budgetDTO.getId());
		return dataTransformerBudget.transformDTOtoBO(budgetDTO, decryptor);
	}


	/**
	 * Activité Budget
	 * @param compte
	 * @param mois
	 * @param annee
	 * @return budget actif
	 */
	public boolean isBudgetActif(CompteBancaire compte, Month mois, int annee){

		boolean actif = false;
		try {
			BudgetMensuelDTO budgetMensuel = chargeBudgetMensuelDTO(compte, mois, annee);
			actif = budgetMensuel != null && budgetMensuel.isActif();
		} catch (BudgetNotFoundException e) {
			actif = false;
		}
		LOGGER.debug("Activité du budget {} de {}/{} : {}", compte, mois, annee, actif);
		return actif;
	}


	/**
	 * Lecture de la date de mise à jour du budget
	 * @param idBudget identifiant du budget
	 * @return date de mise à jour
	 */
	public Date getDateMiseAJourBudget(String idBudget, BasicTextEncryptor decryptor) {
		try {
			BudgetMensuel budgetMensuel = chargeBudgetMensuelById(idBudget, decryptor);
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
	public BudgetMensuel chargeBudgetMensuelById(String idBudget, BasicTextEncryptor decryptor) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget d'id {}", idBudget);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is(idBudget));
		queryBudget.limit(1);
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(idBudget));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget d'id {}", idBudget, e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du budget ").append(idBudget));
		}
		return  dataTransformerBudget.transformDTOtoBO(budgetDTO, decryptor);
	}
	/**
	 * Chargement du compte
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel du compte pour le mois et l'année
	 */
	public BudgetMensuelDTO chargeBudgetMensuelDTO(CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException{
		LOGGER.info("Chargement du budget du compte {} du {}/{}", compte.getId(), mois, annee);
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte.getId()).and(ATTRIBUT_MOIS).is(mois.getValue() - 1).and(ATTRIBUT_ANNEE).is(annee));
		BudgetMensuelDTO budgetDTO = null;
		try{
			budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(annee));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du budget du compte {} du {}/{}", compte.getId(), mois, annee, e);
		}
		if(budgetDTO == null){
			throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du budget du compte ").append(compte.getId()).append(" du ").append(mois).append("/").append(annee));
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

		// Année courante
		Calendar annee = Calendar.getInstance();
		for (int a = 2014; a <= annee.get(Calendar.YEAR); a++) {
			try{
				budgets.addAll(getMongoOperation().find(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a)));
			}
			catch(Exception e){
				LOGGER.error("Erreur lors du chargement des budgets du compte {}", idCompte, e);
			}
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
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param compte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public BudgetMensuelDTO[] getDatePremierDernierBudgets(String compte) throws DataNotFoundException{
		Query query1erBudget = new Query();
		query1erBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
		query1erBudget.with(new Sort(Sort.Direction.ASC, ATTRIBUT_ANNEE)).with(new Sort(Sort.Direction.ASC, ATTRIBUT_MOIS));
		query1erBudget.limit(1);

		Query querydernierBudget = new Query();
		querydernierBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
		querydernierBudget.with(new Sort(Sort.Direction.DESC, ATTRIBUT_ANNEE)).with(new Sort(Sort.Direction.DESC, ATTRIBUT_MOIS));
		querydernierBudget.limit(1);
		try{
			BudgetMensuelDTO premierbudget = null;
			for (int a = 2014; a <= Calendar.getInstance().get(Calendar.YEAR); a++) {
				premierbudget = getMongoOperation().findOne(query1erBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a));
				if(premierbudget != null){
					break;
				}				
			}

			LOGGER.debug("Premier budget trouvé : {}", premierbudget);


			BudgetMensuelDTO dernierbudget = null;
			for (int a = Calendar.getInstance().get(Calendar.YEAR); a >= 2014; a--) {
				dernierbudget = getMongoOperation().findOne(querydernierBudget, BudgetMensuelDTO.class, getBudgetCollectionName(a));
				if(dernierbudget != null){
					break;
				}				
			}
			LOGGER.debug("Dernier budget trouvé : -> {}", dernierbudget);
			return new BudgetMensuelDTO[]{premierbudget, dernierbudget};
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement de la date du premier budget de {}", compte, e);
			throw new DataNotFoundException("Erreur lors du chargement de la date du premier budget de " + compte);
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
		Query queryBudget = new Query().addCriteria(Criteria.where("id").is(idBudget));
		try{
			BudgetMensuelDTO budgetDTO = getMongoOperation().findOne(queryBudget, BudgetMensuelDTO.class, getBudgetCollectionName(idBudget));
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
	public String sauvegardeBudgetMensuel(BudgetMensuel budgetBO, BasicTextEncryptor encryptor){
		if(budgetBO == null){
			return null;
		}
		BudgetMensuelDTO budgetDTO = dataTransformerBudget.transformBOtoDTO(budgetBO, encryptor);
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budgetDTO.getCompteBancaire().getLibelle(), budgetDTO.getMois() + 1, budgetDTO.getAnnee());
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
