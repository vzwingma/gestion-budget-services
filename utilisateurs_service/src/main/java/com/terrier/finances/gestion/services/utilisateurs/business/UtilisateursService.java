package com.terrier.finances.gestion.services.utilisateurs.business;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Service Utilisateurs
 * @author vzwingma
 *
 */
@Service
public class UtilisateursService extends AbstractBusinessService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateursService.class);



	/**
	 * Utilisateurs
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBUsers;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	/**
	 * Tentative d'auth sur les API
	 * @param login login du user
	 * @param motPasse mot de passe
	 * @return token JWT
	 */
	public String authenticate(String login, String motPasse) {
		Utilisateur utilisateur = authenticateUser(login, motPasse);
		if(utilisateur != null) {
			return createToken(utilisateur);
		}
		return null;

	}

	/**
	 * @param login login du user
	 * @param motPasse mot de passe du user
	 * @return utilisateur authentifié
	 */
	private Utilisateur authenticateUser(String login, String motPasse) {

		LOGGER.info("[idUser=?] Tentative d'authentification de {}", login);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBUsers.chargeUtilisateur(login);
			if(utilisateur != null && this.passwordEncoder.matches(motPasse, utilisateur.getPassword())){
				LOGGER.info("[idUser={}] Utilisateur [{}] trouvé", utilisateur.getId(), login);
				LOGGER.info(" Droits {}", utilisateur.getDroits()); 
				return utilisateur;
			}
			else{
				LOGGER.error("[idUser=?] Erreur 2 : Utilisateur {} inconnu", login);
			}
		} catch (DataNotFoundException e) {
			LOGGER.error("[idUser=?] Erreur 3 : Données introuvables pour l'utilisateur {}", login);
		}
		return null;
	}


	/**
	 * @param utilisateur
	 * @return le token JWT correspondant
	 */
	private String createToken(Utilisateur utilisateur) {
		LOGGER.info("[idUser={}] Utilisateur [{}] authentifié", utilisateur.getId(), utilisateur.getLogin());
		Long now = Calendar.getInstance().getTimeInMillis();
		String token = Jwts.builder()
				.setSubject(utilisateur.getLogin())
				.setId(UUID.randomUUID().toString())
				// Convert to list of strings. 
				.claim(JwtConfigEnum.JWT_CLAIM_HEADER_AUTORITIES, 
						utilisateur.getDroits().entrySet().stream()
						.filter(e -> e.getValue())
						.map(e -> e.getKey()).collect(Collectors.toList()))
				.claim(JwtConfigEnum.JWT_CLAIM_HEADER_USERID, utilisateur.getId())
				.setIssuedAt(new Date(now))
				.setIssuer("Budget-Services")
				.setExpiration(new Date(now + JwtConfigEnum.JWT_EXPIRATION_S * 1000))  // in milliseconds
				.signWith(SignatureAlgorithm.HS512, JwtConfigEnum.JWT_SECRET_KEY.getBytes())
				.compact();
		LOGGER.info("[idUser={}] Token JWT [{}]", utilisateur.getId(), token);
		return token;
	}

	/**
	 * Une fois l'authentification réalisée, enregistrement de la session business
	 * @param auth authentification
	 * @throws DataNotFoundException 
	 */

	public Utilisateur successfullAuthentication(Authentication auth) throws DataNotFoundException{
		Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(auth.getName());
		// Enregistrement de la date du dernier accès à maintenant
		LocalDateTime dernierAcces = utilisateur.getDernierAcces();
		utilisateur.setDernierAcces(LocalDateTime.now());
		dataDBUsers.majUtilisateur(utilisateur);
		utilisateur.setDernierAcces(dernierAcces);
		return utilisateur;
	}
	/**
	 * @param utilisateur utlisateur à modifier
	 * @param oldPassword ancien mot de passe
	 * @param newPassword nouveau mot de passe
	 * @return résultat de l'opération
	 */
	public void changePassword(Utilisateur utilisateur, String oldPassword, String newPassword){

		LOGGER.info("[idUser={}] Changement du mot de passe pour {}, ", utilisateur.getId(), utilisateur.getId());
		String newHashPassword = this.passwordEncoder.encode(newPassword);
		LOGGER.info("[idUser={}] Nouveau hash du mot de passe : {}",utilisateur.getId(), newHashPassword);
		utilisateur.setPassword(newHashPassword);
		dataDBUsers.majUtilisateur(utilisateur);
	}

	/**
	 * @param idUtilisateur
	 * @return date de dernier accès
	 */
	public Map<UtilisateurPrefsEnum, String> getPrefsUtilisateur(String idUtilisateur){
		try {
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateurById(idUtilisateur);
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
			Utilisateur utilisateur = dataDBUsers.chargeUtilisateurById(idUtilisateur);
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
