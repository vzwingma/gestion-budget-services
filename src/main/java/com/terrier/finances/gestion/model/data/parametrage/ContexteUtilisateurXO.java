package com.terrier.finances.gestion.model.data.parametrage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;

/**
 * Contexte d'un utilisateur
 * 
 * @author vzwingma
 * 
 */
public class ContexteUtilisateurXO {

	private Utilisateur utilisateur;

	private List<CompteBancaire> comptes;

	private Map<String, Date> mapMinDateCompte = new HashMap<String, Date>();
	private Map<String, Date> mapMaxDateCompte = new HashMap<String, Date>();

	/**
	 * @return the utilisateur
	 */
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	/**
	 * @param utilisateur
	 *            the utilisateur to set
	 */
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	/**
	 * @return the comptes
	 */
	public List<CompteBancaire> getComptes() {
		return comptes;
	}

	/**
	 * @param comptes
	 *            the comptes to set
	 */
	public void setComptes(List<CompteBancaire> comptes) {
		this.comptes = comptes;
	}

	/**
	 * @return the mapMinDateCompte
	 */
	public Map<String, Date> getMapMinDateCompte() {
		return mapMinDateCompte;
	}

	/**
	 * @param mapMinDateCompte
	 *            the mapMinDateCompte to set
	 */
	public void setMapMinDateCompte(Map<String, Date> mapMinDateCompte) {
		this.mapMinDateCompte = mapMinDateCompte;
	}

	/**
	 * @return the mapMaxDateCompte
	 */
	public Map<String, Date> getMapMaxDateCompte() {
		return mapMaxDateCompte;
	}

	/**
	 * @param mapMaxDateCompte
	 *            the mapMaxDateCompte to set
	 */
	public void setMapMaxDateCompte(Map<String, Date> mapMaxDateCompte) {
		this.mapMaxDateCompte = mapMaxDateCompte;
	}

	/**
	 * @param compte
	 * @param minDate
	 * @param maxDate
	 */
	public void setIntervalleCompte(CompteBancaire compte, Calendar minDate,
			Calendar maxDate) {
		this.mapMinDateCompte.put(compte.getId(), minDate.getTime());
		this.mapMaxDateCompte.put(compte.getId(), maxDate.getTime());
	}
}
