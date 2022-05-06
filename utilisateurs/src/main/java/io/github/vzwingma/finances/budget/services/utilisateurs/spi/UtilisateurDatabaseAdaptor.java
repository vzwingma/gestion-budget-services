package io.github.vzwingma.finances.budget.services.utilisateurs.spi;

import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRepository;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.spi.mongodb.AbstractDatabaseServiceProvider;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service Provider  données en MongoDB fournissant les infos des utilisateurs et comptes
 * @author vzwingma
 *
 */
@ApplicationScoped
public class UtilisateurDatabaseAdaptor implements IUtilisateursRepository {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurDatabaseAdaptor.class);

	/**
	 * @param login : login utilisateur
	 * @return Utilisateur
	 */
	public Utilisateur chargeUtilisateur(String login) throws DataNotFoundException {
		try{
			LOGGER.info("[idUser=?] Recherche de l'utilisateur [{}]", login);
			Query queryUser = new Query().addCriteria(Criteria.where("login").is(login));
			Utilisateur user = findOneByQuery(queryUser);
			if(user == null){
				throw new DataNotFoundException("Impossible de trouver l'utilisateur " + login);
			}
			else {
				return user;
			}
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
			persist(utilisateur);
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors de la sauvegarde de l'utilisateur", utilisateur.getId(), e);
		}
	}
}
