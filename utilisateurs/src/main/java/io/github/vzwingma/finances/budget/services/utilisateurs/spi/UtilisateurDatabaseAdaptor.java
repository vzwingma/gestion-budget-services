package io.github.vzwingma.finances.budget.services.utilisateurs.spi;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public Uni<Utilisateur> chargeUtilisateur(String login) {
		try{
			LOGGER.info("[idUser=?] Recherche de l'utilisateur [{}]", login);
			return find("login", login).singleResult();
		}
		catch(Exception e){
			LOGGER.error("[idUser=?] Erreur lors de la connexion à la BDD", login, e);
			return Uni.createFrom().failure(new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + login);
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
