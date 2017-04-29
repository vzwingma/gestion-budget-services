package com.terrier.finances.gestion.model.business.budget;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;

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
	// Notes
	private String notes;
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
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LigneDepense.class);
	
	// Constructeur
	public LigneDepense(){}
	

	
	/**
	 * Constructeur
	 * @param ssCategorie Catégorie
	 * @param libelle libellé
	 * @param typeDepense type de dépense
	 * @param valeur valeur
	 * @param etat état
	 */
	public LigneDepense(CategorieDepense ssCategorie, String libelle, TypeDepenseEnum typeDepense, float valeur, EtatLigneDepenseEnum etat, boolean periodique){
		this.id = UUID.randomUUID().toString();
		setSsCategorie(ssCategorie);
		this.libelle = libelle;
		this.typeDepense = typeDepense;
		this.valeur = valeur;
		this.etat = etat;
		this.dateOperation = Calendar.getInstance().getTime();
		this.periodique = periodique;
		this.derniereOperation = false;
	}
	
	/**
	 * Invoke dynamique de setter sur ligne dépense
	 * @param ligneId id de la ligne
	 * @param propertyId propriété
	 * @param propClass classe
	 * @param value nouvelle valeur
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean updateProperty(String ligneId, String propertyId, Class propClass, Object value){
		Class ldClass = this.getClass();
		boolean res = false; 
		
		String setMethodName = "set"+propertyId;
		String getMethodName = "get"+propertyId; 
		
		if(propertyId.equals("Periodique")){
			getMethodName = "is" + propertyId;
		}
		else if(propertyId.equals("LibelleView")){
			return true;
		}
		Method g = null;
		Method s = null;
		try {
			g = ldClass.getMethod(getMethodName);
			s = ldClass.getMethod(setMethodName, propClass);
			
			Object getted = g.invoke(this);
			
			if((value != null && !value.equals(getted)) || (value == null && getted != null)){
				s.invoke(this, value); // field value	
				LOGGER.debug("Modification de la ligne [{}] : {}={}", ligneId, propertyId, value);
				res = true;
			}
		}
		catch (NoSuchMethodException nsme) {
			LOGGER.error("Erreur getMethod ", nsme);
		}
		catch (IllegalAccessException iae) {
			LOGGER.error("Erreur ivoke ", iae);
		}
		catch (InvocationTargetException ite) {
			LOGGER.error("Erreur ivoke ", ite);
		}
		return res;
	}
	
	

	/**
	 * @return Ligne dépense clonée
	 * @throws CloneNotSupportedException
	 */
	public LigneDepense cloneDepenseToMoisSuivant() {
		LigneDepense ligneDepenseClonee = new LigneDepense();
		ligneDepenseClonee.setId(UUID.randomUUID().toString());
		ligneDepenseClonee.setLibelle(this.libelle);
		ligneDepenseClonee.setNotes(this.notes);
		ligneDepenseClonee.setSsCategorie(this.ssCategorie);
		ligneDepenseClonee.setDateMaj(Calendar.getInstance().getTime());
		ligneDepenseClonee.setDateOperation(null);
		ligneDepenseClonee.setEtat(EtatLigneDepenseEnum.PREVUE);
		ligneDepenseClonee.setPeriodique(this.periodique);
		ligneDepenseClonee.setTypeDepense(this.typeDepense);
		ligneDepenseClonee.setValeur(this.valeur);
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
	 * @param valeur the valeur to set
	 */
	public void setValeur(float valeur) {
		this.valeur = valeur;
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
		LOGGER.trace("> MAJ de la catégorie de la dépense : {}", ssCategorie);
		this.ssCategorie = ssCategorie;
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

	/**
	 * @return the categorie
	 */
	public CategorieDepense getCategorie() {
		return this.ssCategorie != null ? this.ssCategorie.getCategorieParente() : null;
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
	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(Float valeur) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LigneDepense [id=" + id + ", ssCategorie=" + ssCategorie.getId() + " (" + ssCategorie.getLibelle()
				+ "), libelle=" + libelle + ", typeDepense=" + typeDepense
				+ ", etat=" + etat + ", dateOperation="
				+ dateOperation + ", dateMaj=" + dateMaj + ", auteur=" + auteur
				+ ", periodique=" + periodique + "]";
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
