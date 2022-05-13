package io.github.vzwingma.finances.budget.services.utilisateurs.business;

import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursAppProvider;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Service Utilisateurs
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor
public class UtilisateursService implements IUtilisateursAppProvider {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateursService.class);
	/**
	 * Utilisateurs
	 */
	@Inject
	IUtilisateursRepository dataDBUsers;

	/**
	 * Constructeur (pour les tests)
	 * @param spiUtilisateurs Service port Interface Utilisateurs
	 */
	public UtilisateursService(IUtilisateursRepository spiUtilisateurs){
		this.dataDBUsers = spiUtilisateurs;
	}

	/**
	 * @param loginUtilisateur login de l'utilisateur
	 * @return date de dernier accès
	 */
	public Uni<Utilisateur> getUtilisateur(String loginUtilisateur)  {
		// Enregistrement de la date du dernier accès à maintenant, en async
		return dataDBUsers.chargeUtilisateur(loginUtilisateur)
				.invoke(user -> {
					LOGGER.info("[idUser={}] {} accède à l'application", user.getId(), user.getLogin());
					updateUtilisateurLastConnection(user);
				});
	}

	/**
	 * Mise à jour de la date de dernier accès
	 * @param utilisateurUni utilisateur connecté
	 */
	private void updateUtilisateurLastConnection(Utilisateur utilisateurUni) {

		utilisateurUni.setDernierAcces(LocalDateTime.now());
		dataDBUsers.majUtilisateur(utilisateurUni);
	}
	/**
	 * Date de dernier accès
	 * @param login login de l'utilisateur
	 * @return date de dernier accès
	 */
	public Uni<LocalDateTime> getLastAccessDate(String login) {
		return getUtilisateur(login)
				.map(Utilisateur::getDernierAcces);
	}
}