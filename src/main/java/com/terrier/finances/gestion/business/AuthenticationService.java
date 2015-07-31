package com.terrier.finances.gestion.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.business.rest.auth.RestSessionManager;
import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;

/**
 * Service d'authentification
 * @author vzwingma
 *
 */
@Service
public class AuthenticationService implements AuthenticationProvider {

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
		} catch (NullPointerException | NoSuchAlgorithmException | UnsupportedEncodingException e){
			return null;
		}
	}


	/**
	 * Validation login/mdp
	 * @param login login
	 * @param motPasseClair mdp
	 * @return si valide
	 */
	public Utilisateur authenticate(String login, String motPasseClair){

		LOGGER.info("Tentative d'authentification de {}:{}", login, motPasseClair);
		String mdpHashed = hashPassWord(motPasseClair);
		Utilisateur utilisateur;
		try {
			utilisateur = dataDBParams.chargeUtilisateur(login, mdpHashed);
		} catch (DataNotFoundException e) {
			utilisateur = null;
		}

		if(utilisateur != null){
			// Enregistrement de la date du dernier accès à maintenant
			Calendar dernierAcces = utilisateur.getDateDernierAcces();
			utilisateur.setDateDernierAcces(Calendar.getInstance());
			dataDBParams.majUtilisateur(utilisateur);
			utilisateur.setDateDernierAcces(dernierAcces);

			if(utilisateur.getCleChiffrementDonnees() == null){
				LOGGER.warn("Clé de chiffrement nulle : Initialisation");
				BasicTextEncryptor encryptorCle = new BasicTextEncryptor();
				encryptorCle.setPassword(motPasseClair);
				String cleChiffrementDonneesChiffree = encryptorCle.encrypt(motPasseClair);
				LOGGER.warn("Clé de chiffrement chiffrée avec le mot de passe : {}", cleChiffrementDonneesChiffree);
				utilisateur.setCleChiffrementDonnees(cleChiffrementDonneesChiffree);
				dataDBParams.majUtilisateur(utilisateur);
				utilisateur.initEncryptor(motPasseClair);
			}
			else{
				LOGGER.debug("> Clé de chiffrement des données : {}", utilisateur.getCleChiffrementDonnees());
				BasicTextEncryptor decryptorCle = new BasicTextEncryptor();
				decryptorCle.setPassword(motPasseClair);
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


	/**
	 * Retourne la valeur d'une préférence pour l'utilisateur cournat
	 * @param clePreference clé
	 * @param typeValeurPreference type de la préférence
	 * @return la valeur
	 */
	public <T> T getPreferenceUtilisateurCourant(String clePreference, Class<T> typeValeurPreference){
		return UISessionManager.getSession().getUtilisateurCourant().getPreference(clePreference, typeValeurPreference);
	}


	/**
	 * Authentification REST
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		Utilisateur utilisateur = authenticate(username, password);
		if (utilisateur == null) {
			throw new BadCredentialsException("Erreur d'authentification.");
		}
		else{
			RestSessionManager.getInstance().registerSession(getIdSession(username+":"+password), utilisateur);
		}
		return new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
	
	/**
	 * @param data données
	 * @return données en base64
	 */
	private String getIdSession(String data){
		String b64 = Base64.encodeBase64String(data.getBytes());
		LOGGER.info("b64 : [{}]", b64);
		return b64;
	}
}
