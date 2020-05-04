package com.terrier.finances.gestion.services.utilisateurs.business;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.model.v12.Utilisateur;

/**
 * Service Utilisateurs
 * @author vzwingma
 *
 */
@Service
public class UtilisateursService extends AbstractBusinessService {


	/**
	 * Utilisateurs
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBUsers;

	/**
	 * @param idUtilisateur
	 * @return date de dernier accès
	 */
	public Map<UtilisateurPrefsEnum, String> getPrefsUtilisateur(String idUtilisateur){
		try {
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateurById(idUtilisateur);
			// Enregistrement de la date du dernier accès à maintenant
			LocalDateTime dernierAcces = utilisateur.getDernierAcces();
			utilisateur.setDernierAcces(LocalDateTime.now());
			dataDBUsers.majUtilisateur(utilisateur);
			utilisateur.setDernierAcces(dernierAcces);
			return utilisateur.getPrefsUtilisateur();
		} catch (DataNotFoundException e) {
			return null;
		}
	}
	/**
	 * @param idUtilisateur
	 * @return date de dernier accès
	 */
	public LocalDateTime getLastAccessDate(String idUtilisateur){
		try {
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(idUtilisateur);
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
