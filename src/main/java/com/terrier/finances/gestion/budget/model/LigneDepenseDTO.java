package com.terrier.finances.gestion.budget.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
public class LigneDepenseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2932267709864103657L;
	// Id
	private String id;
	// SS Catégorie
	private String idSSCategorie;
	private String idCategorie;
	// Libellé
	private String libelle;
	// Notes
	private String notes;
	// Type de dépense
	private String typeDepense;
	// Etat de la ligne
	private String etat;
	// Valeur
	private String valeur;
	// Date operation
	private Date dateOperation;
	// Date mise à jour
	private Date dateMaj;
	// Auteur MAJ
	private String auteur;
	// Périodique
	private boolean periodique; 
	// tag comme dernière opération
	private boolean derniereOperation;

	// Constructeur
	public LigneDepenseDTO(){
		// Constructeur pour Spring
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
	 * @return the typeDepense
	 */
	public String getTypeDepense() {
		return typeDepense;
	}
	/**
	 * @param typeDepense the typeDepense to set
	 */
	public void setTypeDepense(String typeDepense) {
		this.typeDepense = typeDepense;
	}
	/**
	 * @return the valeur
	 */
	public String getValeur() {
		return valeur;
	}
	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	/**
	 * @return the dateOperation
	 */
	public Date getDateOperation() {
		return dateOperation;
	}

	/**
	 * @param dateOperation the dateOperation to set
	 */
	public void setDateOperation(Date dateOperation) {
		this.dateOperation = dateOperation;
	}

	/**
	 * @return the dateMaj
	 */
	public Date getDateMaj() {
		return dateMaj;
	}

	/**
	 * @param dateMaj the dateMaj to set
	 */
	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	/**
	 * @return the auteur
	 */
	public String getAuteur() {
		return auteur;
	}

	/**
	 * @param auteur the auteur to set
	 */
	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	/**
	 * @return the etat
	 */
	public String getEtat() {
		return etat;
	}

	/**
	 * @param etat the etat to set
	 */
	public void setEtat(String etat) {
		this.etat = etat;
	}

	/**
	 * @return the periodique
	 */
	public boolean isPeriodique() {
		return periodique;
	}

	/**
	 * @param periodique the periodique to set
	 */
	public void setPeriodique(boolean periodique) {
		this.periodique = periodique;
	}
	

	

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}


	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepenseDTO [id=").append(id)
				.append(", idSSCategorie=").append(idSSCategorie)
				.append(", idCategorie=").append(idCategorie)
				.append(", libelle=").append(libelle).append(", notes=")
				.append(notes).append(", typeDepense=").append(typeDepense)
				.append(", etat=").append(etat).append(", valeur=")
				.append(valeur).append(", dateOperation=")
				.append(dateOperation).append(", dateMaj=").append(dateMaj)
				.append(", auteur=").append(auteur).append(", periodique=")
				.append(periodique).append(", derniereOperation=")
				.append(derniereOperation).append("]");
		return builder.toString();
	}

	
	
	/**
	 * @return the idSSCategorie
	 */
	public String getIdSSCategorie() {
		return idSSCategorie;
	}

	/**
	 * @param idSSCategorie the idSSCategorie to set
	 */
	public void setIdSSCategorie(String idSSCategorie) {
		this.idSSCategorie = idSSCategorie;
	}

	/**
	 * @return the idCategorie
	 */
	public String getIdCategorie() {
		return idCategorie;
	}

	/**
	 * @param idCategorie the idCategorie to set
	 */
	public void setIdCategorie(String idCategorie) {
		this.idCategorie = idCategorie;
	}

	/**
	 * @return the derniereOperation
	 */
	public boolean isDerniereOperation() {
		return derniereOperation;
	}

	/**
	 * @param derniereOperation the derniereOperation to set
	 */
	public void setDerniereOperation(boolean derniereOperation) {
		this.derniereOperation = derniereOperation;
	}	
}
