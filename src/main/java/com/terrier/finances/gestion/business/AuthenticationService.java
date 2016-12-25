package com.terrier.finances.gestion.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.business.auth.PasswordEncoder;
import com.terrier.finances.gestion.data.ParametragesDatabaseService;
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
	private ParametragesDatabaseService dataDBParams;


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
				Date dernierAcces = utilisateur.getDernierAcces();
				dataDBParams.majUtilisateur(utilisateur);
				dernierAcces = Calendar.getInstance().getTime();
				utilisateur.setDernierAcces(dernierAcces);

				if(utilisateur.getCleChiffrementDonnees() == null){
					LOGGER.warn("Clé de chiffrement nulle : Initialisation");
					BasicTextEncryptor encryptorCle = new BasicTextEncryptor();
					encryptorCle.setPassword(motPasseEnClair);
					String cleChiffrementDonneesChiffree = encryptorCle.encrypt(motPasseEnClair);
					//LOGGER.warn("Clé de chiffrement chiffrée avec le mot de passe : {}", cleChiffrementDonneesChiffree);
					utilisateur.setCleChiffrementDonnees(cleChiffrementDonneesChiffree);
					dataDBParams.majUtilisateur(utilisateur);
					utilisateur.initEncryptor(motPasseEnClair);
				}
				else{
					//LOGGER.debug("> Clé chiffrée de chiffrement des données : {}", utilisateur.getCleChiffrementDonnees());
					BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
					decryptorCle.setPassword(motPasseEnClair);
					String cleChiffrementDonnees = decryptorCle.decrypt(utilisateur.getCleChiffrementDonnees());
					utilisateur.initEncryptor(cleChiffrementDonnees);
				}

				return utilisateur;
			}
			else{
				LOGGER.error("Erreur 1 lors de l'authentification");
			}
		}
		else{
			LOGGER.error("Erreur 2 lors de l'authentification");
		}
		return null;
	}
	

	/**
	 * @param password mot de passe
	 * @return password hashé en 256
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String hashPassWord(String password){

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NullPointerException | NoSuchAlgorithmException | UnsupportedEncodingException e){
			return null;
		}
	}


}
