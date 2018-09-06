package com.terrier.finances.gestion.services.utilisateurs.business;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Service d'authentification
 * @author vzwingma
 *
 */
@Service
public class AuthenticationService extends AbstractBusinessService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

	/**
	 * Paramétrages
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBUsers;

	/**
	 * Session coté Business
	 */
	private Map<String, UserBusinessSession> businessSessions = new HashMap<>();

	/**
	 * Validation login/mdp
	 * @param login login
	 * @param motPasseEnClair mdp
	 * @return si valide
	 */
	public String authenticate(String login, String motPasseEnClair){

		LOGGER.info("Tentative d'authentification de {}", login);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBUsers.chargeUtilisateur(login);
		} catch (DataNotFoundException e) {
			utilisateur = null;
		}

		if(utilisateur != null){
			// Vérification du mot de passe
			if(PasswordEncoder.validatePassword(motPasseEnClair, utilisateur.getHashMotDePasse())){
				if(utilisateur.getMasterCleChiffrementDonnees() == null){
					LOGGER.error("Erreur 3 lors de l'authentification. Master key introuvable");
					return null;
				}
				else{
					LOGGER.debug("> MasterKey chiffrée des données : {}", utilisateur.getMasterCleChiffrementDonnees());
					BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
					decryptorCle.setPassword(motPasseEnClair);
					String cleChiffrementDonnees = decryptorCle.decrypt(utilisateur.getMasterCleChiffrementDonnees());
					registerUserBusinessSession(utilisateur, cleChiffrementDonnees);
				}
				// Enregistrement de la date du dernier accès à maintenant
				LocalDateTime dernierAcces = utilisateur.getDernierAcces();
				utilisateur.setDernierAcces(LocalDateTime.now());
				dataDBUsers.majUtilisateur(utilisateur);
				utilisateur.setDernierAcces(dernierAcces);
				return utilisateur.getId();
			}
			else{
				LOGGER.error("Erreur 1 lors de l'authentification. Mot de passe incorrect");
			}
		}
		else{
			LOGGER.error("Erreur 2 lors de l'authentification. Utilisateur inconnu");
		}
		return null;
	}
	
	/**
	 * @param utilisateur utlisateur à modifier
	 * @param oldPassword ancien mot de passe
	 * @param newPassword nouveau mot de passe
	 * @return résultat de l'opération
	 */
	public void changePassword(Utilisateur utilisateur, String oldPassword, String newPassword){
		
		LOGGER.info("Changement du mot de passe pour {}, [id={}]", utilisateur.getLogin(), utilisateur.getId());
		BasicTextEncryptor decryptorForMasterKey = new BasicTextEncryptor();
		decryptorForMasterKey.setPassword(oldPassword);
		String masterKeyClear = decryptorForMasterKey.decrypt(utilisateur.getMasterCleChiffrementDonnees());
		BasicTextEncryptor encryptorForMasterKey = new BasicTextEncryptor();
		encryptorForMasterKey.setPassword(newPassword);
		String masterKeyEncr = encryptorForMasterKey.encrypt(masterKeyClear);
		LOGGER.debug("Rechiffrement MasterKey : {}" , masterKeyEncr);
		utilisateur.setMasterCleChiffrementDonnees(masterKeyEncr);
		
		String newHashPassword = PasswordEncoder.generateStrongPasswordHash(newPassword);
		LOGGER.info("Nouveau hash du mot de passe : {}", newHashPassword);
		utilisateur.setHashMotDePasse(PasswordEncoder.generateStrongPasswordHash(newPassword));
		registerUserBusinessSession(utilisateur, masterKeyClear);
		dataDBUsers.majUtilisateur(utilisateur);
	}
	
	/**
	 * Injection de la masterkey sur l'encryptor associé à l'utilisateur
	 * @param utilisateur
	 * @param masterKeyClear
	 */
	public void registerUserBusinessSession(Utilisateur utilisateur, String masterKeyClear){
		LOGGER.debug("Enregistrement de la BusinessSession [{}]", utilisateur.getId());
		this.businessSessions.putIfAbsent(utilisateur.getId(), new UserBusinessSession(utilisateur));
		this.businessSessions.get(utilisateur.getId()).getEncryptor().setPassword(masterKeyClear);	
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
	


	/**
	 * @param idCompte id du compte
	 * @return etat du compte
	 */
	public boolean isCompteActif(String idCompte){
		try {
			return dataDBUsers.isCompteActif(idCompte);
		} catch (DataNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Recherche du compte par id
	 * @param idCompte id du compte
	 * @param utilisateur utilisateur
	 * @return compteBancaire
	 * @throws DataNotFoundException
	 */
	public CompteBancaire getCompteById(String idCompte, String proprietaire) throws DataNotFoundException{
		return dataDBUsers.chargeCompteParId(idCompte, proprietaire);
	}



	/**
	 * Recherche des comptes d'un utilisateur
	 * @param utilisateur utilisateur
	 * @return liste des comptes bancaires
	 * @throws DataNotFoundException
	 */
	public List<CompteBancaire> getComptesUtilisateur(String idUtilisateur) throws DataNotFoundException{
		return dataDBUsers.chargeComptes(idUtilisateur);
	}
	
}
