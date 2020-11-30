package com.terrier.finances.gestion.services.utilisateurs.business;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;
import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRepository;
import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service Utilisateurs
 * @author vzwingma
 *
 */
@Service
@NoArgsConstructor
public class UtilisateursService extends AbstractBusinessService implements IUtilisateursRequest {


	/**
	 * Utilisateurs
	 */
	@Autowired
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
	public Utilisateur getUtilisateur(String loginUtilisateur){
		try {
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(loginUtilisateur);
			// Enregistrement de la date du dernier accès à maintenant
			LocalDateTime dernierAcces = utilisateur.getDernierAcces();
			utilisateur.setDernierAcces(LocalDateTime.now());
			dataDBUsers.majUtilisateur(utilisateur);
			utilisateur.setDernierAcces(dernierAcces);
			return utilisateur;
		} catch (DataNotFoundException e) {
			return null;
		}
	}
	/**
	 * Date de dernier accès
	 * @param login login de l'utilisateur
	 * @return date de dernier accès
	 */
	public LocalDateTime getLastAccessDate(String login){
		try {
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(login);
			return utilisateur.getDernierAcces();
		} catch (DataNotFoundException e) {
			return null;
		}
	}

	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Utilisateurs");
	}
}
