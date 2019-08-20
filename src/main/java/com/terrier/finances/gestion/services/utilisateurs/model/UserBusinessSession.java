package com.terrier.finances.gestion.services.utilisateurs.model;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;

/**
 * Session Utilisateur coté Services
 * @author vzwingma
 *
 */
public class UserBusinessSession {

	// Utilisateur métier, associé à la session
	private Utilisateur utilisateur;

	public UserBusinessSession(Utilisateur utilisateur){
		this.utilisateur = utilisateur;
	}
	
	
	/**
	 * @return the utilisateur
	 */
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void deconnexion(){
		this.utilisateur = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(utilisateur).toString();
	}
}
