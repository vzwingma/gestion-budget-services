package com.terrier.finances.gestion.business;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.business.auth.PasswordEncoder;
import com.terrier.finances.gestion.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

/**
 * Service d'authentification
 * @author vzwingma
 *
 */
@Service
public class AuthenticationService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

	public AuthenticationService(){
		LOGGER.info("[INIT] Authentification Service");
	}

	/**
	 * Paramétrages
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBParams;

	
	private Map<Utilisateur, BasicTextEncryptor> encrypters = new HashMap<Utilisateur, BasicTextEncryptor>();

	/**
	 * Validation login/mdp
	 * @param login login
	 * @param motPasseEnClair mdp
	 * @return si valide
	 */
	public Utilisateur authenticate(String login, String motPasseEnClair){

		LOGGER.info("Tentative d'authentification de {}", login);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBParams.chargeUtilisateur(login);
		} catch (DataNotFoundException e) {
			utilisateur = null;
		}

		if(utilisateur != null){
			// Vérification du mot de passe
			if(PasswordEncoder.validatePassword(motPasseEnClair, utilisateur.getHashMotDePasse())){
				// Enregistrement de la date du dernier accès à maintenant
				dataDBParams.majUtilisateur(utilisateur);
				Date dernierAcces = Calendar.getInstance().getTime();
				utilisateur.setDernierAcces(dernierAcces);
				if(utilisateur.getMasterCleChiffrementDonnees() == null){
					LOGGER.error("Erreur 3 lors de l'authentification. Master key introuvable");
					return null;
				}
				else{
					LOGGER.debug("> MasterKey chiffrée des données : {}", utilisateur.getMasterCleChiffrementDonnees());
					BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
					decryptorCle.setPassword(motPasseEnClair);
					String cleChiffrementDonnees = decryptorCle.decrypt(utilisateur.getMasterCleChiffrementDonnees());
					updateEncryter(utilisateur, cleChiffrementDonnees);
				}
				return utilisateur;
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
		updateEncryter(utilisateur, masterKeyClear);
		dataDBParams.majUtilisateur(utilisateur);
	}
	
	/**
	 * Injection de la masterkey sur l'encryptor associé à l'utilisateur
	 * @param utilisateur
	 * @param masterKeyClear
	 */
	private void updateEncryter(Utilisateur utilisateur, String masterKeyClear){
		encrypters.putIfAbsent(utilisateur, new BasicTextEncryptor());
		encrypters.get(utilisateur).setPassword(masterKeyClear);	
	}
	
	/**
	 * @param utilisateur
	 * @return l'encryptor de l'utilisateur
	 */
	public BasicTextEncryptor getEncryptor(Utilisateur utilisateur){
		return this.encrypters.get(utilisateur);
	}
}
