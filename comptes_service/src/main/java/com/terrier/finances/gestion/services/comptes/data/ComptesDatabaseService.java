package com.terrier.finances.gestion.services.comptes.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ComptesDatabaseService extends AbstractDatabaseService<CompteBancaire> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesDatabaseService.class);

	private static final String ATTRIBUT_COMPTE_ID = "compteBancaire.id";
	private static final String ATTRIBUT_ANNEE = "annee";
	
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

			listeComptes = findByQuery(queryBudget)
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
			CompteBancaire compte =  findOneByQuery(queryBudget);		
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
			boolean isActif = findOneByQuery(queryBudget) != null;
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
}
