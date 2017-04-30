package com.terrier.finances.gestion.model.business.budget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;

/**
 * Budget du mois
 * @author vzwingma
 *
 */
public class BudgetMensuel implements Serializable {

	public String id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4393433203514049021L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetMensuel.class);
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
	private Calendar dateMiseAJour;
	/**
	 * Compte bancaire
	 */
	private CompteBancaire compteBancaire;
	/**
	 * Résultat du mois précédent
	 */
	private Double resultatMoisPrecedent;

	private Double margeSecurite;
	private Double margeSecuriteFinMois;

	/**
	 * Liste des dépenses
	 */
	private List<LigneDepense> listeDepenses = new ArrayList<LigneDepense>();


	private Map<CategorieDepense, Double[]> totalParCategories = new HashMap<CategorieDepense, Double[]>();
	private Map<CategorieDepense, Double[]> totalParSSCategories = new HashMap<CategorieDepense, Double[]>();

	/**
	 * Totaux
	 */
	private Double nowArgentAvance;
	private Double nowCompteReel;
	private Double finArgentAvance;
	private Double finCompteReel;

	/**
	 * Raz calculs
	 */
	public void razCalculs(){
		totalParCategories.clear();
		totalParSSCategories.clear();
		LOGGER.debug("Raz des calculs du budget");
		nowArgentAvance = this.resultatMoisPrecedent;
		nowCompteReel = this.resultatMoisPrecedent + this.margeSecurite;
		finArgentAvance = this.resultatMoisPrecedent;
		this.margeSecuriteFinMois = this.margeSecuriteFinMois != null ? this.margeSecuriteFinMois : 0L;
		finCompteReel = this.resultatMoisPrecedent + this.margeSecuriteFinMois;
	}




	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the totalParCategories
	 */
	public Map<CategorieDepense, Double[]> getTotalParCategories() {
		return totalParCategories;
	}

	/**
	 * @return the totalParSSCategories
	 */
	public Map<CategorieDepense, Double[]> getTotalParSSCategories() {
		return totalParSSCategories;
	}


	/**
	 * @return the nowArgentAvance
	 */
	public double getNowArgentAvance() {
		return nowArgentAvance;
	}

	/**
	 * @return the nowCompteReel
	 */
	public double getNowCompteReel() {
		return nowCompteReel;
	}


	/**
	 * @return the finArgentAvance
	 */
	public double getFinArgentAvance() {
		return finArgentAvance;
	}

	/**
	 * @return the finCompteReel
	 */
	public double getFinCompteReel() {
		return finCompteReel;
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
	 * @param nowArgentAvance the nowArgentAvance to set
	 */
	public void ajouteANowArgentAvance(double nowArgentAvance) {
		this.nowArgentAvance += nowArgentAvance;
	}

	/**
	 * @param finArgentAvance the finArgentAvance to set
	 */
	public void ajouteAFinArgentAvance(double finArgentAvance) {
		this.finArgentAvance += finArgentAvance;
	}


	/**
	 * @param nowArgentAvance the nowArgentAvance to set
	 */
	public void ajouteANowCompteReel(double nowArgentAvance) {
		this.nowCompteReel += nowArgentAvance;
	}

	/**
	 * @param finArgentAvance the finArgentAvance to set
	 */
	public void ajouteAFinCompteReel(double finArgentAvance) {
		this.finCompteReel += finArgentAvance;
	}

	/**
	 * @return the resultatMoisPrecedent
	 */
	public Double getResultatMoisPrecedent() {
		return resultatMoisPrecedent;
	}

	/**
	 * @param resultatMoisPrecedent the resultatMoisPrecedent to set
	 */
	public void setResultatMoisPrecedent(Double resultatMoisPrecedent) {
		this.resultatMoisPrecedent = resultatMoisPrecedent;
		this.finArgentAvance = resultatMoisPrecedent;
		this.finCompteReel = resultatMoisPrecedent;
		this.nowArgentAvance = resultatMoisPrecedent;
		this.nowCompteReel = resultatMoisPrecedent;
	}

	/**
	 * @return the listeDepenses
	 */
	public List<LigneDepense> getListeDepenses() {
		return listeDepenses;
	}

	/**
	 * @param listeDepenses the listeDepenses to set
	 */
	public void setListeDepenses(List<LigneDepense> listeDepenses) {
		this.listeDepenses = listeDepenses;
	}


	/**
	 * @return the margeSecurite
	 */
	public Double getMargeSecurite() {
		return margeSecurite;
	}


	/**
	 * @param margeSecurite the margeSecurite to set
	 */
	public void setMargeSecurite(Double margeSecurite) {
		this.margeSecurite = margeSecurite;
	}


	/**
	 * @return the dateMiseAJour
	 */
	public Calendar getDateMiseAJour() {
		return dateMiseAJour;
	}


	/**
	 * @param dateMiseAJour the dateMiseAJour to set
	 */
	public void setDateMiseAJour(Calendar dateMiseAJour) {
		this.dateMiseAJour = dateMiseAJour;
	}



	/**
	 * @return the margeSecuriteFinMois
	 */
	public Double getMargeSecuriteFinMois() {
		return margeSecuriteFinMois;
	}


	/**
	 * @param margeSecuriteFinMois the margeSecuriteFinMois to set
	 */
	public void setMargeSecuriteFinMois(Double margeSecuriteFinMois) {
		this.margeSecuriteFinMois = margeSecuriteFinMois;
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
	 * @param totalParCategories the totalParCategories to set
	 */
	public void setTotalParCategories(
			Map<CategorieDepense, Double[]> totalParCategories) {
		this.totalParCategories = totalParCategories;
	}




	/**
	 * @param totalParSSCategories the totalParSSCategories to set
	 */
	public void setTotalParSSCategories(
			Map<CategorieDepense, Double[]> totalParSSCategories) {
		this.totalParSSCategories = totalParSSCategories;
	}




	/**
	 * @param nowArgentAvance the nowArgentAvance to set
	 */
	public void setNowArgentAvance(Double nowArgentAvance) {
		this.nowArgentAvance = nowArgentAvance;
	}




	/**
	 * @param nowCompteReel the nowCompteReel to set
	 */
	public void setNowCompteReel(Double nowCompteReel) {
		this.nowCompteReel = nowCompteReel;
	}




	/**
	 * @param finArgentAvance the finArgentAvance to set
	 */
	public void setFinArgentAvance(Double finArgentAvance) {
		this.finArgentAvance = finArgentAvance;
	}




	/**
	 * @param finCompteReel the finCompteReel to set
	 */
	public void setFinCompteReel(Double finCompteReel) {
		this.finCompteReel = finCompteReel;
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



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BudgetMensuel [mois=" + mois + ", annee=" + annee
				+ ", bugetActif=" + actif + ", dateMiseAJour="
				+ (dateMiseAJour != null ? dateMiseAJour.getTime() : "null") + ", compte=" + compteBancaire + "]";
	}
}
