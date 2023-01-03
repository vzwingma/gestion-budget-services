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
			LOGGER.info("Recherche de l'utilisateur [{}]", login);
			return find("login", login)
					.singleResultOptional()
					.map(utilisateur -> utilisateur.orElse(null));
					//.onItem();.ifNull().failWith(new DataNotFoundException("Utilisateur non trouvé"));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la connexion à la BDD", e);
			return Uni.createFrom().failure(new DataNotFoundException("Erreur lors de la recherche d'utilisateur " + login));
		}
	}


	/**
	 * Met à jour l'utilisateur en BDD
	 */
	public void majUtilisateur(Utilisateur utilisateur){
		try{
			LOGGER.info("Mise à jour de l'utilisateur [{}]", utilisateur.getLogin());
			update(utilisateur)
					.subscribe().with(item -> LOGGER.info("Utilisateur [{}] mis à jour", utilisateur.getLogin()));
		}
		catch(Exception e){
			LOGGER.error("Erreur lors de la sauvegarde de l'utilisateur", e);
		}
	}
}
