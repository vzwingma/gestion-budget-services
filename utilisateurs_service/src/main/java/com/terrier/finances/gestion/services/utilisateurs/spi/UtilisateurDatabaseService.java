package com.terrier.finances.gestion.services.utilisateurs.spi;

import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;

/**
 * Service de données en MongoDB fournissant les infos des utilisateurs et comptes
 * @author vzwingma
 *
 */
@Repository
public class UtilisateurDatabaseService extends AbstractDatabaseService<Utilisateur> implements IUtilisateursRepository {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurDatabaseService.class);

	/**
	 * @param login : login utilisateur
	 * @return Utilisateur
	 */
	public Utilisateur chargeUtilisateur(String login) throws DataNotFoundException{
		try{
			LOGGER.info("[idUser=?] Recherche de l'utilisateur [{}]", login);
			Query queryUser = new Query().addCriteria(Criteria.where("login").is(login));
			return findOneByQuery(queryUser);
		}
		catch(Exception e){
			LOGGER.error("[idUser=?] Erreur lors de la recherche de l'utilisateur [{}]", login, e);
			throw new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + login);
		}
	}


	/**
	 * @return la liste des catégories
	 */
	public Utilisateur chargeUtilisateurById(String id) throws DataNotFoundException{
		try{
			LOGGER.info("[idUser={}] Recherche de l'utilisateur", id);
			return findById(id);
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors de la recherche de l'utilisateur", id, e);
			throw new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + id);
		}
	}
	/**
	 * Met à jour l'utilisateur en BDD
	 */
	public void majUtilisateur(Utilisateur utilisateur){
		try{
			save(utilisateur);
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors de la sauvegarde de l'utilisateur", utilisateur.getId(), e);
		}
	}
}
