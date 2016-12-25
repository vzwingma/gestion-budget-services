/**
 * 
 */
package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.business.auth.PasswordEncoder;
import com.terrier.finances.gestion.model.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurPrefsEnum;

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
	@JsonIgnore
	private String cleChiffrementDonnees;

	@JsonIgnore	
	private Date dernierAcces;
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
	private Map<UtilisateurPrefsEnum, Object> prefsUtilisateur = new HashMap<>();
	/**
	 * Droits
	 */
	private Map<UtilisateurDroitsEnum, Boolean> droits = new HashMap<>();
	
	
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
	 * @return the prefsUtilisateur
	 */
	public Map<UtilisateurPrefsEnum, Object> getPrefsUtilisateur() {
		return prefsUtilisateur;
	}

	/**
	 * @param prefsUtilisateur the prefsUtilisateur to set
	 */
	public void setPrefsUtilisateur(Map<UtilisateurPrefsEnum, Object> prefsUtilisateur) {
		this.prefsUtilisateur = prefsUtilisateur;
	}

	/**
	 * @return the preferences
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPreference(UtilisateurPrefsEnum clePreference, Class<T> typeAttendu) {
		return (T)prefsUtilisateur.get(clePreference);
	}

	
	
	/**
	 * @return the encryptor
	 */
	public BasicTextEncryptor getEncryptor() {
		return encryptor;
	}

	/**
	 * @param encryptor the encryptor to set
	 * @see initPBKDF2WithHmacSHA1Encryptor
	 */
	@Deprecated
	public void initEncryptor(String motDePasse) {
		this.encryptor = new BasicTextEncryptor();
		this.encryptor.setPassword(AuthenticationService.hashPassWord("#"+motDePasse+"#"));
	}

	/**
	 * InitEncryptor with new hash
	 * @param motDePasse
	 */
	public void initPBKDF2WithHmacSHA1Encryptor(String motDePasse){
		this.encryptor = new BasicTextEncryptor();
		this.encryptor.setPassword(PasswordEncoder.generateStrongPasswordHash(motDePasse));
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
	 * @return the dernierAcces
	 */
	public Date getDernierAcces() {
		return dernierAcces;
	}

	/**
	 * @param dernierAcces the dernierAcces to set
	 */
	public void setDernierAcces(Date dernierAcces) {
		this.dernierAcces = dernierAcces;
	}

	/**
	 * @return the droits
	 */
	public Map<UtilisateurDroitsEnum, Boolean> getDroits() {
		return droits;
	}

	/**
	 * @param droits the droits to set
	 */
	public void setDroits(Map<UtilisateurDroitsEnum, Boolean> droits) {
		this.droits = droits;
	}

	/**
	 * @param cleDroit
	 * @return le résultat
	 */
	public boolean isEnabled(UtilisateurDroitsEnum cleDroit){
		if(this.droits != null){
			Boolean droit = this.droits.get(cleDroit);
			return droit != null ? droit.booleanValue() : false;
		}
		return false;
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
		return this.login;
	}
	
	public String toFullString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Utilisateur [login=").append(login)
				.append(", dateDernierAcces=")
				.append(dernierAcces != null ? dernierAcces.getTime() : "nulle").append(", libelle=").append(libelle)
				.append("]");
		return builder.toString();
	}

}
