package com.terrier.finances.gestion.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.UISessionManager;

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
	@Autowired
	private ParametragesService serviceParametrages;
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
		} catch (NoSuchAlgorithmException e){
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}


	/**
	 * Validation login/mdp
	 * @param login login
	 * @param motPasseClair mdp
	 * @return si valide
	 */
	public boolean validate(String login, String motPasseClair){

		String mdpHashed = hashPassWord(motPasseClair);
		LOGGER.info("Tentative d'authentification de {}", login);
		Utilisateur utilisateur = getUtilisateur(login, mdpHashed);

		if(utilisateur != null){
			utilisateur.initEncryptor(motPasseClair);
			UISessionManager.getSession().registerUtilisateur(utilisateur);
			return true;
		}
		else{
			LOGGER.error("Erreur lors de l'authentification");
		}
		return false;
	}

	/**
	 * @param login
	 * @param motPasseClair
	 * @return utilisateur
	 */
	public Utilisateur getUtilisateur(String login, String mdpHashed){
		LOGGER.info("Tentative d'authentification de {}", login);
		String logAttempt;
		logAttempt = hashPassWord(login+"::"+mdpHashed);
		LOGGER.debug(">{}<", logAttempt);
		try {
			List<Utilisateur> listeUtilisateurs = dataDBParams.chargeUtilisateurs();
			for (Utilisateur utilisateur : listeUtilisateurs) {

				String logUser = hashPassWord(utilisateur.getLogin()+"::"+utilisateur.getHashMotDePasse());
				if(logUser != null && logAttempt != null && logUser.equals(logAttempt)){
					return utilisateur;
				}
			}
		} catch (DataNotFoundException e) {
		}

		LOGGER.error("Erreur lors de l'authentification");
		return null;
	}

	/**
	 * Retourne la valeur d'une préférence pour l'utilisateur cournat
	 * @param clePreference clé
	 * @param typeValeurPreference type de la préférence
	 * @return la valeur
	 */
	public <T> T getPreferenceUtilisateurCourant(String clePreference, Class<T> typeValeurPreference){
		return UISessionManager.getSession().getUtilisateurCourant().getPreference(clePreference, typeValeurPreference);
	}
}
