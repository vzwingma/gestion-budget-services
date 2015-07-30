/**
 * 
 */
package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.business.AuthenticationService;

/**
 * Définition d'un utilisateur
 * @author vzwingma
 *
 */
@Document(collection = "utilisateurs")
public class Utilisateur implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5912920498104708791L;
	

	@Id
	private String id;
	// Login
	private String login;
	
	private String hashMotDePasse;

	// Clé de chiffrement des données. Le mot de passe du user permet de la déchiffrer
	private String cleChiffrementDonnees;
	
	private Calendar dateDernierAcces;
	/**
	 *  Encryptor
	 */
	@Transient
	@JsonIgnore
	private BasicTextEncryptor encryptor;
	
	// Libellé
	private String libelle;
	
	/**
	 * Préférences
	 */
	private Map<String, Object> preferences;
	
	public static final String PREFERENCE_TABLE_ODD_STYLE = "PREFERENCE_TABLE_ODD_STYLE";
	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	/**
	 * @return the hashMotDePasse
	 */
	public String getHashMotDePasse() {
		return hashMotDePasse;
	}

	/**
	 * @param hashMotDePasse the hashMotDePasse to set
	 */
	public void setHashMotDePasse(String hashMotDePasse) {
		this.hashMotDePasse = hashMotDePasse;
	}

	/**
	 * @return the preferences
	 */
	public Map<String, Object> getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences the preferences to set
	 */
	public void setPreferences(Map<String, Object> preferences) {
		this.preferences = preferences;
	}
	


	/**
	 * @return the preferences
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPreference(String clePreference, Class<T> typeAttendu) {
		return (T)preferences.get(clePreference);
	}

	
	
	/**
	 * @return the encryptor
	 */
	public BasicTextEncryptor getEncryptor() {
		return encryptor;
	}

	/**
	 * @param encryptor the encryptor to set
	 */
	public void initEncryptor(String motDePasse) {
		this.encryptor = new BasicTextEncryptor();
		this.encryptor.setPassword(AuthenticationService.hashPassWord("#"+motDePasse+"#"));
	}

	
	/**
	 * @return the cleChiffrementDonnees
	 */
	public String getCleChiffrementDonnees() {
		return cleChiffrementDonnees;
	}

	/**
	 * @param cleChiffrementDonnees the cleChiffrementDonnees to set
	 */
	public void setCleChiffrementDonnees(String cleChiffrementDonnees) {
		this.cleChiffrementDonnees = cleChiffrementDonnees;
	}
	
	

	/**
	 * @return the dateDernierAcces
	 */
	public Calendar getDateDernierAcces() {
		return dateDernierAcces;
	}

	/**
	 * @param dateDernierAcces the dateDernierAcces to set
	 */
	public void setDateDernierAcces(Calendar dateDernierAcces) {
		this.dateDernierAcces = dateDernierAcces;
	}
	
	

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Utilisateur [login=").append(login)
				.append(", cleChiffrementDonnees=")
				.append(cleChiffrementDonnees).append(", dateDernierAcces=")
				.append(dateDernierAcces != null ? dateDernierAcces.getTime() : "nulle").append(", libelle=").append(libelle)
				.append("]");
		return builder.toString();
	}

}
