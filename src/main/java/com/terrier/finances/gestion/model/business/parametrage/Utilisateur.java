/**
 * 
 */
package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Utilisateur [id=" + id + ", login=" + login + ", libelle="
				+ libelle + "]";
	}
}
