package com.terrier.finances.gestion.services.comptes.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.spi.mongodb.AbstractDatabaseServiceProvider;

/**
 * Service de données en MongoDB fournissant les infos des comptes
 * Adapteur du SPI {@link com.terrier.finances.gestion.services.comptes.business.ports.IComptesRepository}
 * @author vzwingma
 *
 */
@Repository
public class ComptesDatabaseAdaptor extends AbstractDatabaseServiceProvider<CompteBancaire> implements IComptesRepository {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesDatabaseAdaptor.class);

	/**
	 * Chargement des comptes
	 * @param idUtilisateur utilisateur
	 * @return liste des comptes associés
	 * @throws DataNotFoundException erreur dans la connexion
	 */
	public List<CompteBancaire> chargeComptes(String idUtilisateur) throws DataNotFoundException{
		List<CompteBancaire>  listeComptes = new ArrayList<>();
		try{
			LOGGER.info("[idUser={}] Chargement des comptes de l'utilisateur", idUtilisateur);
			Query queryBudget = new Query().addCriteria(Criteria.where("proprietaire.login").is(idUtilisateur));

			listeComptes = findByQuery(queryBudget)
					.stream()
					.sorted((compte1, compte2) -> Integer.compare(compte1.getOrdre(), compte2.getOrdre()))
					.collect(Collectors.toList());
			LOGGER.info("{} comptes chargés : {} ", listeComptes.size(), listeComptes);
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors du chargement des comptes", idUtilisateur, e);
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
			if(compte.getProprietaire().getLogin().equalsIgnoreCase(idUtilisateur)){
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
}
