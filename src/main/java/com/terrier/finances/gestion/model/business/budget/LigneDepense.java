package com.terrier.finances.gestion.model.business.budget;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

import com.terrier.finances.gestion.business.OperationsService;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
public class LigneDepense implements Comparable<LigneDepense>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2932267709864103657L;
	// Id
	private String id;
	// SS Catégorie
	@Transient
	private CategorieDepense ssCategorie;
	// Libellé
	private String libelle;
	// Type de dépense
	private TypeDepenseEnum typeDepense;
	// Etat de la ligne
	private EtatLigneDepenseEnum etat;
	// Valeur
	private float valeur;
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
	// Budget actif
	private boolean budgetIsActif;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LigneDepense.class);
	
	// Constructeur
	public LigneDepense(boolean budgetIsActif){
		this.budgetIsActif = budgetIsActif;
	}
	
	/**
	 * Constructeur
	 * @param ssCategorie Catégorie
	 * @param libelle libellé
	 * @param typeDepense type d'opération
	 * @param absValeur valeur montant en valeur absolue
	 * @param etat état
	 */
	public LigneDepense(CategorieDepense ssCategorie, String libelle, TypeDepenseEnum typeDepense, String absValeur, EtatLigneDepenseEnum etat, boolean periodique, boolean budgetActif){
		this.id = UUID.randomUUID().toString();
		setSsCategorie(ssCategorie);
		this.libelle = libelle;
		this.typeDepense = typeDepense;
		setValeurAbsStringToFloat(absValeur);
		this.etat = etat;
		this.dateOperation = Calendar.getInstance().getTime();
		this.periodique = periodique;
		this.derniereOperation = false;
		this.budgetIsActif = budgetActif;
	}
	

	/**
	 * @return Ligne dépense clonée
	 * @throws CloneNotSupportedException
	 */
	public LigneDepense cloneDepenseToMoisSuivant() {
		LigneDepense ligneDepenseClonee = new LigneDepense(this.budgetIsActif);
		ligneDepenseClonee.setId(UUID.randomUUID().toString());
		ligneDepenseClonee.setLibelle(this.libelle);
		ligneDepenseClonee.setSsCategorie(this.ssCategorie);
		ligneDepenseClonee.setDateMaj(Calendar.getInstance().getTime());
		ligneDepenseClonee.setDateOperation(null);
		ligneDepenseClonee.setEtat(EtatLigneDepenseEnum.PREVUE);
		ligneDepenseClonee.setPeriodique(this.periodique);
		ligneDepenseClonee.setTypeDepense(this.typeDepense);
		ligneDepenseClonee.setValeurAbsStringToFloat(Float.toString(Math.abs(this.valeur)));
		ligneDepenseClonee.setDerniereOperation(false);
		return ligneDepenseClonee;
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
	 * @return the ssCategorie
	 */
	public CategorieDepense getSsCategorie() {
		return ssCategorie;
	}

	/**
	 * @param ssCategorie the ssCategorie to set
	 */
	public void setSsCategorie(CategorieDepense ssCategorie) {
		LOGGER.trace("> MAJ de la catégorie de l'opération : {}", ssCategorie);
		this.ssCategorie = ssCategorie;
	}

	/**
	 * @return the categorie
	 */
	public CategorieDepense getCategorie() {
		return this.ssCategorie != null ? this.ssCategorie.getCategorieParente() : null;
	}
	
	public void setCategorie(CategorieDepense categorie){
		// Ne fais rien. Calculé par le set de Sous Categorie
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
	public TypeDepenseEnum getTypeDepense() {
		return typeDepense;
	}
	/**
	 * @param typeDepense the typeDepense to set
	 */
	public void setTypeDepense(TypeDepenseEnum typeDepense) {
		this.typeDepense = typeDepense;
	}
	/**
	 * @return the valeur
	 */
	public float getValeur() {
		return valeur;
	}

	public String getValeurAbsStringFromFloat() {
		return Float.toString(Math.abs(valeur));
	}

	public void setValeurAbsStringToFloat(String valeurS){
		if(valeurS != null){
			this.valeur = Math.abs(Float.parseFloat(valeurS)) * (TypeDepenseEnum.DEPENSE.equals(this.getTypeDepense()) ? -1 : 1);
		}
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
	public EtatLigneDepenseEnum getEtat() {
		return etat;
	}

	/**
	 * @param etat the etat to set
	 */
	public void setEtat(EtatLigneDepenseEnum etat) {
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
	 * @param periodique the periodique to set
	 */
	public void setPeriodique(Boolean periodique) {
		this.periodique = periodique;
	}

	
	
	/**
	 * @return the actionsOperation
	 */
	public ActionsLigneBudget getActionsOperation() {
		// Pas d'action pour les réserves
		ActionsLigneBudget actionsOperation = null;
		if(!OperationsService.ID_SS_CAT_RESERVE.equals(getSsCategorie().getId())
				&& !OperationsService.ID_SS_CAT_PREVISION_SANTE.equals(getSsCategorie().getId())
				&& budgetIsActif){
			actionsOperation = new ActionsLigneBudget();
			actionsOperation.getControleur().setIdOperation(getId());
			actionsOperation.getControleur().miseAJourEtatLigne(getEtat());
		}
		return actionsOperation;
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

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepense [id=").append(id).append(", ssCategorie=").append(ssCategorie).append(", libelle=")
				.append(libelle).append(", typeDepense=").append(typeDepense).append(", etat=").append(etat)
				.append(", valeur=").append(valeur).append(", dateOperation=").append(dateOperation)
				.append(", dateMaj=").append(dateMaj).append(", auteur=").append(auteur).append(", periodique=")
				.append(periodique).append(", derniereOperation=").append(derniereOperation).append("]");
		return builder.toString();
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		LigneDepense other = (LigneDepense) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LigneDepense o) {
		if(o != null){
			return this.getId().compareTo(o.getId());
		}
		return 0;
	}

}
