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


	/**
	 * Validation login/mdp
	 * @param login login
	 * @param motPasseBasicAuth mdp
	 * @return si valide
	 */
	public Utilisateur authenticate(String login, String motPasseBasicAuth){

		LOGGER.info("Tentative d'authentification de {}", login);
		String mdpHashed = hashPassWord(motPasseBasicAuth);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBParams.chargeUtilisateur(login, mdpHashed);
		} catch (DataNotFoundException e) {
			utilisateur = null;
		}

		if(utilisateur != null){
			// Enregistrement de la date du dernier accès à maintenant
			Date dernierAcces = utilisateur.getDernierAcces();
			dataDBParams.majUtilisateur(utilisateur);
			dernierAcces = Calendar.getInstance().getTime();
			utilisateur.setDernierAcces(dernierAcces);

			if(utilisateur.getCleChiffrementDonnees() == null){
				LOGGER.warn("Clé de chiffrement nulle : Initialisation");
				BasicTextEncryptor encryptorCle = new BasicTextEncryptor();
				encryptorCle.setPassword(motPasseBasicAuth);
				String cleChiffrementDonneesChiffree = encryptorCle.encrypt(motPasseBasicAuth);
				//LOGGER.warn("Clé de chiffrement chiffrée avec le mot de passe : {}", cleChiffrementDonneesChiffree);
				utilisateur.setCleChiffrementDonnees(cleChiffrementDonneesChiffree);
				dataDBParams.majUtilisateur(utilisateur);
				utilisateur.initEncryptor(motPasseBasicAuth);
			}
			else{
				//LOGGER.debug("> Clé chiffrée de chiffrement des données : {}", utilisateur.getCleChiffrementDonnees());
				BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
				decryptorCle.setPassword(motPasseBasicAuth);
				String cleChiffrementDonnees = decryptorCle.decrypt(utilisateur.getCleChiffrementDonnees());
				utilisateur.initEncryptor(cleChiffrementDonnees);
			}

			return utilisateur;
		}
		else{
			LOGGER.error("Erreur lors de l'authentification");
		}
		return null;
	}

}
