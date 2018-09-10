package com.terrier.finances.gestion.services.budget.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;

/**
 * Budget du mois
 * @author vzwingma
 *
 */
@Document(collection = "budgets")
public class BudgetMensuelDTO implements Serializable {

	@Id
	public String id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4393433203514049021L;

	/**
	 * Mois du budget
	 */
	private int mois;
	private int annee;
	/**
	 * Budget actif
	 */
	private boolean actif = false;
	/**
	 * Date de mise à jour
	 */
	private Date dateMiseAJour;
	/**
	 * Compte bancaire
	 */
	private CompteBancaire compteBancaire;
	/**
	 * Résultat du mois précédent
	 */
	private String resultatMoisPrecedent;

	private String margeMoisPrecedent;

	/**
	 * Liste des dépenses
	 */
	@JsonIgnore
	private List<LigneDepenseDTO> listeDepenses = new ArrayList<>();

	/**
	 * Totaux
	 */
	private String nowArgentAvance;
	private String finArgentAvance;

	private Map<String, String[]> totalParCategories = new HashMap<>();
	private Map<String, String[]> totalParSSCategories = new HashMap<>();
	
	/**
	 * @return the id
	 */
	public String getId() {
		if(id == null){
			setId(null);
		}
		return id;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = this.getCompteBancaire().getId()+"_"+this.getAnnee()+"_"+(this.getMois()+1);
	}


	/**
	 * @return the nowArgentAvance
	 */
	public String getNowArgentAvance() {
		return nowArgentAvance;
	}

	/**
	 * @return the finArgentAvance
	 */
	public String getFinArgentAvance() {
		return finArgentAvance;
	}


	/**
	 * @return the mois
	 */
	public int getMois() {
		return mois;
	}


	/**
	 * @return the annee
	 */
	public int getAnnee() {
		return annee;
	}


	/**
	 * @param annee the annee to set
	 */
	public void setAnnee(int annee) {
		this.annee = annee;
	}


	/**
	 * @param mois the mois to set
	 */
	public void setMois(int mois) {
		this.mois = mois;
	}



	/**
	 * @return the resultatMoisPrecedent
	 */
	public String getResultatMoisPrecedent() {
		return resultatMoisPrecedent;
	}

	/**
	 * @param resultatMoisPrecedent the resultatMoisPrecedent to set
	 */
	public void setResultatMoisPrecedent(String resultatMoisPrecedent) {
		this.resultatMoisPrecedent = resultatMoisPrecedent;
	}

	
	
	/**
	 * @param nowArgentAvance the nowArgentAvance to set
	 */
	public void setNowArgentAvance(String nowArgentAvance) {
		this.nowArgentAvance = nowArgentAvance;
	}

	/**
	 * @param finArgentAvance the finArgentAvance to set
	 */
	public void setFinArgentAvance(String finArgentAvance) {
		this.finArgentAvance = finArgentAvance;
	}

	/**
	 * @return the listeDepenses
	 */
	@JsonIgnore
	public List<LigneDepenseDTO> getListeDepenses() {
		return listeDepenses;
	}

	/**
	 * @param listeDepenses the listeDepenses to set
	 */
	@JsonIgnore
	public void setListeDepenses(List<LigneDepenseDTO> listeDepenses) {
		this.listeDepenses = listeDepenses;
	}


	/**
	 * @return the margeSecurite
	 */
	public String getMargeMoisPrecedent() {
		return margeMoisPrecedent;
	}


	/**
	 * @param margeSecurite the margeSecurite to set
	 */
	public void setMargeMoisPrecedent(String margeSecurite) {
		this.margeMoisPrecedent = margeSecurite;
	}


	/**
	 * @return the dateMiseAJour
	 */
	public Date getDateMiseAJour() {
		return dateMiseAJour;
	}


	/**
	 * @param dateMiseAJour the dateMiseAJour to set
	 */
	public void setDateMiseAJour(Date dateMiseAJour) {
		this.dateMiseAJour = dateMiseAJour;
	}



	/**
	 * @return the compteBancaire
	 */
	public CompteBancaire getCompteBancaire() {
		return compteBancaire;
	}




	/**
	 * @param compteBancaire the compteBancaire to set
	 */
	public void setCompteBancaire(CompteBancaire compteBancaire) {
		this.compteBancaire = compteBancaire;
	}




	/**
	 * @return the actif
	 */
	public boolean isActif() {
		return actif;
	}


	/**
	 * @param actif the actif to set
	 */
	public void setActif(boolean actif) {
		this.actif = actif;
	}



	/**
	 * @return the totalParCategories
	 */
	public Map<String, String[]> getTotalParCategories() {
		return totalParCategories;
	}




	/**
	 * @param totalParCategories the totalParCategories to set
	 */
	public void setTotalParCategories(Map<String, String[]> totalParCategories) {
		this.totalParCategories = totalParCategories;
	}




	/**
	 * @return the totalParSSCategories
	 */
	public Map<String, String[]> getTotalParSSCategories() {
		return totalParSSCategories;
	}




	/**
	 * @param totalParSSCategories the totalParSSCategories to set
	 */
	public void setTotalParSSCategories(Map<String, String[]> totalParSSCategories) {
		this.totalParSSCategories = totalParSSCategories;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BudgetMensuelDTO [mois=" + mois + ", annee=" + annee
				+ ", bugetActif=" + actif + ", dateMiseAJour="
				+ (dateMiseAJour != null ? dateMiseAJour.getTime() : "null") + ", compte=" + compteBancaire + "]";
	}
}
