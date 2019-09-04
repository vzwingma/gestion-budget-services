package com.terrier.finances.gestion.services.utilisateurs.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;

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
			LOGGER.info("[idUser=?] Recherche de l'utilisateur [{}]", login);
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where("login").is(login));
			return getMongoOperation().findOne(queryUser, Utilisateur.class);
		}
		catch(Exception e){
			LOGGER.error("[idUser=?] Erreur lors de la recherche de l'utilisateur [{}]", login, e);
			throw new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + login);
		}
	}


	/**
	 * Met à jour l'utilisateur en BDD
	 */
	public void majUtilisateur(Utilisateur utilisateur){
		try{
			getMongoOperation().save(utilisateur);
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors de la sauvegarde de l'utilisateur", utilisateur.getId(), e);
		}
	}
}