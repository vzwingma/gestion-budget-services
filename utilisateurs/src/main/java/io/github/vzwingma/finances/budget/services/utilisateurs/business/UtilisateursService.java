package io.github.vzwingma.finances.budget.services.utilisateurs.business;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRequest;
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
	public Utilisateur getUtilisateur(String loginUtilisateur) throws DataNotFoundException {
		Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(loginUtilisateur);
		// Enregistrement de la date du dernier accès à maintenant
		LocalDateTime dernierAcces = utilisateur.getDernierAcces();
		utilisateur.setDernierAcces(LocalDateTime.now());
		dataDBUsers.majUtilisateur(utilisateur);
		utilisateur.setDernierAcces(dernierAcces);
		return utilisateur;
	}
	/**
	 * Date de dernier accès
	 * @param login login de l'utilisateur
	 * @return date de dernier accès
	 * @throws DataNotFoundException données non trouvées
	 */
	public LocalDateTime getLastAccessDate(String login) throws DataNotFoundException{
		Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(login);
		return utilisateur.getDernierAcces();
	}
}
