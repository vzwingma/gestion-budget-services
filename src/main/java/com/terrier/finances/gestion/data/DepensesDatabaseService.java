package com.terrier.finances.gestion.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.data.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.UISessionManager;

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
		BudgetMensuel budgetMensuel = dataTransformerBudget.transformDTOtoBO(budgetDTO, UISessionManager.getSession().getUtilisateurCourant().getEncryptor());
		LOGGER.debug("	> Transformation en BO : {}", budgetMensuel);
		return budgetMensuel;
	}

	/**
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel
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
	 * @return résultat de la sauvegarde
	 */
	public boolean sauvegardeBudgetMensuel(BudgetMensuel budgetBO){
		if(budgetBO == null){
			return false;
		}
		BudgetMensuelDTO budgetDTO = dataTransformerBudget.transformBOtoDTO(budgetBO, UISessionManager.getSession().getUtilisateurCourant().getEncryptor());
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budgetDTO.getCompteBancaire().getLibelle(), budgetDTO.getMois(), budgetDTO.getAnnee());
		try{
			getMongoOperation().save(budgetDTO);
			LOGGER.info("Budget sauvegardé");
			return true;
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la sauvegarde du compte", e);
			return false;
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
