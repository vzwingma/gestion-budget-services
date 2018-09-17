package com.terrier.finances.gestion.services.utilisateurs.business;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	/**
	 * Session coté Business
	 */
	private Map<String, UserBusinessSession> businessSessions = new HashMap<>();

	/**
	 * Tentative d'auth sur les API
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

		LOGGER.info("[SEC][idUser=?] Tentative d'authentification de {}", login);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBUsers.chargeUtilisateur(login);
			if(utilisateur != null){
				LOGGER.info("[SEC][idUser={}] Utilisateur [{}] trouvé", utilisateur.getId(), login);
				String droits = utilisateur.getDroits()
						.entrySet()
						.stream()
						.filter(e -> e.getValue())
						.map(e -> e.getKey().name())
						.collect(Collectors.joining(";"));

				List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(droits);
				LOGGER.info("[SEC][idUser={}] Droits {}", utilisateur.getId(), droits); 

				if(utilisateur.getMasterCleChiffrementDonnees() == null){
					LOGGER.error("[SEC][idUser={}] Erreur 4 lors de l'authentification. Master key introuvable", utilisateur.getId());
					return null;
				}
				return new User(utilisateur.getLogin(), utilisateur.getPassword(), grantedAuthorities);
			}
			else{
				LOGGER.error("[SEC][idUser=?] Erreur 2 : Utilisateur {} inconnu", login);
			}

		} catch (DataNotFoundException e) {
			// Uti
			LOGGER.error("[SEC][idUser=?] Erreur 3 : Données introuvables pour l'utilisateur {}", login);
		}
		return null;
	}



	/**
	 * Une fois l'authentification réalisée, enregistrement de la session business
	 * @param auth authentification
	 * @param motPasseEnClair mot de passe en clair pour déchiffrer la masterKey
	 */
	public Utilisateur successfulAuthentication(Authentication auth, String motPasseEnClair){

		Utilisateur utilisateur;
		try {
			utilisateur = dataDBUsers.chargeUtilisateur(auth.getName());
			if(utilisateur != null){
				LOGGER.debug("[SEC][idUser={}] MasterKey chiffrée des données : {}", utilisateur.getId(), utilisateur.getMasterCleChiffrementDonnees());
				BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
				decryptorCle.setPassword(motPasseEnClair);
				String cleChiffrementDonnees = decryptorCle.decrypt(utilisateur.getMasterCleChiffrementDonnees());
				registerUserBusinessSession(utilisateur, cleChiffrementDonnees);

				// Enregistrement de la date du dernier accès à maintenant
				LocalDateTime dernierAcces = utilisateur.getDernierAcces();
				utilisateur.setDernierAcces(LocalDateTime.now());
				dataDBUsers.majUtilisateur(utilisateur);
				utilisateur.setDernierAcces(dernierAcces);
			}
		} catch (DataNotFoundException e) {
			utilisateur = null;
		}
		return utilisateur;
	}
	/**
	 * @param utilisateur utlisateur à modifier
	 * @param oldPassword ancien mot de passe
	 * @param newPassword nouveau mot de passe
	 * @return résultat de l'opération
	 */
	public void changePassword(Utilisateur utilisateur, String oldPassword, String newPassword){

		LOGGER.info("[SEC][idUser={}] Changement du mot de passe pour {}, ", utilisateur.getLogin(), utilisateur.getId());
		BasicTextEncryptor decryptorForMasterKey = new BasicTextEncryptor();
		decryptorForMasterKey.setPassword(oldPassword);
		String masterKeyClear = decryptorForMasterKey.decrypt(utilisateur.getMasterCleChiffrementDonnees());
		BasicTextEncryptor encryptorForMasterKey = new BasicTextEncryptor();
		encryptorForMasterKey.setPassword(newPassword);
		String masterKeyEncr = encryptorForMasterKey.encrypt(masterKeyClear);
		LOGGER.debug("[SEC][idUser={}]Rechiffrement MasterKey : {}" ,utilisateur.getId(), masterKeyEncr);
		utilisateur.setMasterCleChiffrementDonnees(masterKeyEncr);

		String newHashPassword = passwordEncoder().encode(newPassword);
		LOGGER.info("[SEC][idUser={}] Nouveau hash du mot de passe : {}",utilisateur.getId(), newHashPassword);
		utilisateur.setPassword(newHashPassword);
		dataDBUsers.majUtilisateur(utilisateur);
		registerUserBusinessSession(utilisateur, masterKeyClear);
	}

	/**
	 * Injection de la masterkey sur l'encryptor associé à l'utilisateur
	 * @param utilisateur
	 * @param masterKeyClear
	 */
	public void registerUserBusinessSession(Utilisateur utilisateur, String masterKeyClear){
		LOGGER.debug("[SEC][idUser={}] Enregistrement de la BusinessSession", utilisateur.getId());
		if(this.businessSessions.containsKey(utilisateur.getId())){
			deconnexionBusinessSession(utilisateur.getId());
		}
		this.businessSessions.putIfAbsent(utilisateur.getId(), new UserBusinessSession(utilisateur));
		this.businessSessions.get(utilisateur.getId()).getEncryptor().setPassword(masterKeyClear);

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

	@Override
	public UserBusinessSession getBusinessSession(String idSession){
		return this.businessSessions.get(idSession);
	}


	/**
	 * Déconnexion de la session Business
	 * @param idSession
	 * @return résultat de la déconnexion
	 */
	public boolean deconnexionBusinessSession(String idSession){
		UserBusinessSession userSession = this.businessSessions.get(idSession);
		if(userSession != null){
			userSession.deconnexion();
			return this.businessSessions.remove(idSession) != null;
		}
		return false;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
