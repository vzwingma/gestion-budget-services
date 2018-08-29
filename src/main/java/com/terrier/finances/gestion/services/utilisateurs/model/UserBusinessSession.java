package com.terrier.finances.gestion.services.utilisateurs.model;

import org.jasypt.util.text.BasicTextEncryptor;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;

/**
 * Session Utilisateur coté Services
 * @author vzwingma
 *
 */
public class UserBusinessSession {

	// Utilisateur métier, associé à la session
	private Utilisateur utilisateur;
	// Encryptor pour les échanges avec la BDD
	private BasicTextEncryptor encryptor = new BasicTextEncryptor();


	public UserBusinessSession(Utilisateur utilisateur){
		this.utilisateur = utilisateur;
	}
	
	/**
	 * @return the encryptor
	 */
	public BasicTextEncryptor getEncryptor() {
		return encryptor;
	}
	
	
	
	/**
	 * @return the utilisateur
	 */
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void deconnexion(){
		this.utilisateur = null;
		this.encryptor = null;
	}
}
