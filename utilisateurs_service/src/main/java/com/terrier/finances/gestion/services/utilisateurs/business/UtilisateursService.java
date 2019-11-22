package com.terrier.finances.gestion.services.utilisateurs.business;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Service Utilisateurs
 * @author vzwingma
 *
 */
@Service
public class UtilisateursService extends AbstractBusinessService implements UserDetailsService {

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
	/**
	 * Session coté Business
	 */
	private Map<String, UserBusinessSession> businessSessions = new HashMap<>();

	/**
	 * Tentative d'auth sur les API
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String login) {

		LOGGER.info("[idUser=?] Tentative d'authentification de {}", login);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBUsers.chargeUtilisateur(login);
			if(utilisateur != null){
				LOGGER.info("[idUser={}] Utilisateur [{}] trouvé", utilisateur.getId(), login);
				List<GrantedAuthority> grantedAuthorities = utilisateur.getDroits()
						.entrySet()
						.stream()
						.filter(Entry::getValue)
						.map(e -> new SimpleGrantedAuthority(e.getKey().name()))
						.collect(Collectors.toList());

				LOGGER.info(" Droits {}", grantedAuthorities); 
				return new User(utilisateur.getLogin(), utilisateur.getPassword(), grantedAuthorities);
			}
			else{
				LOGGER.error("[idUser=?] Erreur 2 : Utilisateur {} inconnu", login);
			}

		} catch (DataNotFoundException e) {
			// Uti
			LOGGER.error("[idUser=?] Erreur 3 : Données introuvables pour l'utilisateur {}", login);
		}
		return null;
	}


	/**
	 * Une fois l'authentification réalisée, enregistrement de la session business
	 * @param auth authentification
	 * @throws DataNotFoundException 
	 */
	
	public Utilisateur successfullAuthentication(Authentication auth) throws DataNotFoundException{
		Utilisateur utilisateur = dataDBUsers.chargeUtilisateur(auth.getName());
		registerUserBusinessSession(utilisateur);
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
		registerUserBusinessSession(utilisateur);
	}

	/**
	 * Injection de la masterkey sur l'encryptor associé à l'utilisateur
	 * @param utilisateur
	 * @param masterKeyClear
	 */
	public void registerUserBusinessSession(Utilisateur utilisateur){
		LOGGER.debug("[idUser={}] Enregistrement de la BusinessSession", utilisateur.getId());
		if(this.businessSessions.containsKey(utilisateur.getId())){
			deconnexionBusinessSession(this.businessSessions.get(utilisateur.getId()));
		}
		this.businessSessions.putIfAbsent(utilisateur.getId(), new UserBusinessSession(utilisateur));
	}


	/**
	 * @param idSession
	 * @return date de dernier accès
	 */
	public LocalDateTime getLastAccessDate(String idSession){
		if(this.businessSessions.get(idSession) != null){
			return this.businessSessions.get(idSession).getUtilisateur().getDernierAcces();
		}
		return null;
	}


	public UserBusinessSession getBusinessSession(String idSession){
		return this.businessSessions.get(idSession);
	}


	/**
	 * Déconnexion de la session Business
	 * @param idSession
	 * @return résultat de la déconnexion
	 */
	public boolean deconnexionBusinessSession(UserBusinessSession userSession ){
		if(userSession != null){
			return this.businessSessions.remove(userSession.getUtilisateur().getId()) != null;
		}
		return false;
	}
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Utilisateurs");
	}
	
}
