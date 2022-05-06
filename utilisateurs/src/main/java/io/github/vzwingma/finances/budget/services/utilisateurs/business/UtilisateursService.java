package io.github.vzwingma.finances.budget.services.utilisateurs.business;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRequest;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;

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
public class UtilisateursService implements IUtilisateursRequest {


	/**
	 * Utilisateurs
	 */
	@Inject
	private IUtilisateursRepository dataDBUsers;

	/**
	 * Constructeur (pour les tests)
	 * @param spiUtilisateurs
	 */
	public UtilisateursService(IUtilisateursRepository spiUtilisateurs){
		this.dataDBUsers = spiUtilisateurs;
	}

	/**
	 * @param loginUtilisateur
	 * @return date de dernier accès
	 */
	public Uni<Utilisateur> getUtilisateur(String loginUtilisateur) throws DataNotFoundException {
		Uni<Utilisateur> utilisateur = dataDBUsers.chargeUtilisateur(loginUtilisateur);
		// Enregistrement de la date du dernier accès à maintenant
		final LocalDateTime[] dernierAcces = new LocalDateTime[1];

		Uni<Utilisateur> user = utilisateur
				.map(u -> {
					dernierAcces[0] = u.getDernierAcces();
					u.setDernierAcces(LocalDateTime.now());
					dataDBUsers.majUtilisateur(u);
					u.setDernierAcces(dernierAcces[0]);

				});
;

		return user;
	}
	/**
	 * Date de dernier accès
	 * @param login login de l'utilisateur
	 * @return date de dernier accès
	 * @throws DataNotFoundException données non trouvées
	 */
	public LocalDateTime getLastAccessDate(String login) throws DataNotFoundException{
		Uni<Utilisateur> utilisateur = dataDBUsers.chargeUtilisateur(login);
		return utilisateur.onItem().tragetDernierAcces();
	}
}
