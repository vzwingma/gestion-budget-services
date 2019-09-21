package com.terrier.finances.gestion.services.comptes.data;

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

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;
import com.terrier.finances.gestion.services.comptes.model.BudgetMensuelDTO;

/**
 * Service de données en MongoDB fournissant les infos des utilisateurs et comptes
 * @author vzwingma
 *
 */
@Repository
public class ComptesDatabaseService extends AbstractDatabaseService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesDatabaseService.class);

	private static final String ATTRIBUT_COMPTE_ID = "compteBancaire.id";
	private static final String ATTRIBUT_ANNEE = "annee";
	private static final String ATTRIBUT_MOIS = "mois";
	
	/**
	 * Chargement des comptes
	 * @param utilisateur utilisateur 
	 * @return liste des comptes associés
	 * @throws DataNotFoundException erreur dans la connexion
	 */
	public List<CompteBancaire> chargeComptes(String idUtilisateur) throws DataNotFoundException{
		List<CompteBancaire>  listeComptes = new ArrayList<>();
		try{
			LOGGER.info("[idUser={}] Chargement des comptes de l'utilisateur", idUtilisateur);
			Query queryBudget = new Query().addCriteria(Criteria.where("listeProprietaires").elemMatch(Criteria.where("_id").is(idUtilisateur)));

			listeComptes = getMongoOperation().find(queryBudget, CompteBancaire.class)
					.stream()
					.sorted((compte1, compte2) -> Integer.compare(compte1.getOrdre(), compte2.getOrdre()))
					.collect(Collectors.toList());
			LOGGER.info("{} comptes chargés : {} ", listeComptes.size(), listeComptes);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des comptes", idUtilisateur, e);
			throw new DataNotFoundException("Erreur lors de la recherche des comptes");
		}
		return listeComptes;
	}


	/**
	 * Chargement d'un compte par un id
	 * @param idCompte id du compte
	 * @param idUtilisateur utilisateur associé
	 * @return compte
	 * @throws DataNotFoundException
	 */
	public CompteBancaire chargeCompteParId(String idCompte, String idUtilisateur) throws DataNotFoundException{
		try{
			LOGGER.info("[idCompte={}] Chargement du compte", idCompte);
			Query queryBudget = new Query();
			queryBudget
			.addCriteria(Criteria.where("id").is(idCompte));
			CompteBancaire compte =  getMongoOperation().findOne(queryBudget, CompteBancaire.class);		
			if(compte.getListeProprietaires().stream().anyMatch(u -> u.getId().equals(idUtilisateur))){
				return compte;
			}
			else{
				LOGGER.warn("[idCompte={}] Aucun compte n'existe pour l'utilisateur", idCompte);
				throw new DataNotFoundException("Aucun compte n'existe pour l'utilisateur courant");
			}
		}
		catch(Exception e){
			LOGGER.error("[idCompte={}] Erreur lors du chargement du compte", idCompte, e);
			throw new DataNotFoundException("Erreur lors de la recherche de compte");
		}
	}


	/**
	 * Chargement d'un compte par un id
	 * @param idCompte id du compte
	 * @param utilisateur utilisateur associé
	 * @return compte
	 * @throws DataNotFoundException
	 */
	public boolean isCompteActif(String idCompte) throws DataNotFoundException{
		try{
			Query queryBudget = new Query();
			queryBudget
			.addCriteria(Criteria.where("id").is(idCompte))
			.addCriteria(Criteria.where("actif").is(true));
			boolean isActif = getMongoOperation().findOne(queryBudget, CompteBancaire.class) != null;
			LOGGER.info("[idCompte={}] Compte actif ? {}", idCompte, isActif);
			return isActif;
		}
		catch(Exception e){
			LOGGER.error("[idCompte={}] Erreur lors du chargement du compte ", idCompte, e);
			throw new DataNotFoundException("Erreur lors de la recherche de compte");
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
			List<BudgetMensuelDTO> budgetsDTO = getMongoOperation().find(queryBudget, BudgetMensuelDTO.class);

			libellesDepenses = budgetsDTO
					.parallelStream()
					// liste dépenses transformées 
					.flatMap(budgetDTO -> budgetDTO.getListeDepenses().stream().map(operation -> operation.getLibelle()))
					.collect(Collectors.toSet());
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des libellés des dépenses du compte {} de {}", idCompte, annee, e);
		}
		return libellesDepenses;
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
			query1erBudget.with(new Sort(Sort.Direction.ASC, ATTRIBUT_ANNEE)).with(new Sort(Sort.Direction.ASC, ATTRIBUT_MOIS));
			query1erBudget.limit(1);
			
			BudgetMensuelDTO premierbudget = getMongoOperation().findOne(query1erBudget, BudgetMensuelDTO.class);
			LOGGER.debug("Premier budget trouvé -> {}", premierbudget);
			
			Query querydernierBudget = new Query();
			querydernierBudget.addCriteria(Criteria.where(ATTRIBUT_COMPTE_ID).is(compte));
			querydernierBudget.with(new Sort(Sort.Direction.DESC, ATTRIBUT_ANNEE)).with(new Sort(Sort.Direction.DESC, ATTRIBUT_MOIS));
			querydernierBudget.limit(1);
			
			BudgetMensuelDTO dernierbudget = getMongoOperation().findOne(querydernierBudget, BudgetMensuelDTO.class);
			LOGGER.debug("Dernier budget trouvé -> {}", dernierbudget);
			return new BudgetMensuelDTO[]{premierbudget, dernierbudget};
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement de la date du premier budget de {}", compte, e);
			throw new DataNotFoundException("Erreur lors du chargement de la date du premier budget de " + compte);
		}
	}
}
