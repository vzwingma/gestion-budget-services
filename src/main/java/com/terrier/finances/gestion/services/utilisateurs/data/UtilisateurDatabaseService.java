package com.terrier.finances.gestion.services.utilisateurs.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.exception.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.data.AbstractDatabaseService;

/**
 * Service de données en MongoDB fournissant les infos des utilisateurs et comptes
 * @author vzwingma
 *
 */
@Repository
public class UtilisateurDatabaseService extends AbstractDatabaseService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurDatabaseService.class);

	/**
	 * @return la liste des catégories
	 */
	public Utilisateur chargeUtilisateur(String login) throws DataNotFoundException{
		try{
			LOGGER.info("Recherche de l'utilisateur {}", login);
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where("login").is(login));
			return getMongoOperation().findOne(queryUser, Utilisateur.class);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la recherche de l'utilisateur {}", login, e);
			throw new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + login);
		}
	}


	/**
	 * @return la liste des catégories
	 */
	public void majUtilisateur(Utilisateur utilisateur){
		try{
			getMongoOperation().save(utilisateur);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la sauvegarde de l'utilisateur", e);
		}
	}



	/**
	 * Chargement des comptes
	 * @param utilisateur utilisateur 
	 * @return liste des comptes associés
	 * @throws DataNotFoundException erreur dans la connexion
	 */
	public List<CompteBancaire> chargeComptes(String idUtilisateur) throws DataNotFoundException{
		List<CompteBancaire>  listeComptes = new ArrayList<>();
		try{
			LOGGER.info("Chargement des comptes de [_id={}]", idUtilisateur);
			Query queryBudget = new Query().addCriteria(Criteria.where("listeProprietaires").elemMatch(Criteria.where("_id").is(idUtilisateur)));

			listeComptes = getMongoOperation().find(queryBudget, CompteBancaire.class)
					.stream()
					.sorted((compte1, compte2) -> Integer.compare(compte1.getOrdre(), compte2.getOrdre()))
					.collect(Collectors.toList());
			LOGGER.info(" {} comptes chargés : {} ", listeComptes.size(), listeComptes);
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement des comptes de {}", idUtilisateur, e);
			throw new DataNotFoundException("Erreur lors de la recherche des comptes");
		}
		return listeComptes;
	}


	/**
	 * Chargement d'un compte par un id
	 * @param idCompte id du compte
	 * @param utilisateur utilisateur associé
	 * @return compte
	 * @throws DataNotFoundException
	 */
	public CompteBancaire chargeCompteParId(String idCompte, String utilisateur) throws DataNotFoundException{
		try{
			LOGGER.info("Chargement du compte {}", idCompte);
			Query queryBudget = new Query();
			queryBudget
				.addCriteria(Criteria.where("id").is(idCompte));
			CompteBancaire compte =  getMongoOperation().findOne(queryBudget, CompteBancaire.class);		
			if(compte.getListeProprietaires().stream().anyMatch(u -> u.getLogin().equals(utilisateur))){
				return compte;
			}
			else{
				LOGGER.warn("Aucun compte {} n'existe pour l'utilisateur courant {}", idCompte, utilisateur);
				throw new DataNotFoundException("Aucun compte n'existe pour l'utilisateur courant");
			}
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du compte {}", idCompte, e);
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
			LOGGER.info("Compte {} est actif ? {}", idCompte, isActif);
			return isActif;
		}
		catch(Exception e){
			LOGGER.error("Erreur lors du chargement du compte {}", idCompte, e);
			throw new DataNotFoundException("Erreur lors de la recherche de compte");
		}
	}
}
