package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Catégorie de dépense
 * @author vzwingma
 *
 */
@Document(collection = "ParamCategoriesDepenses")
public class CategorieDepense implements Serializable, Comparable<CategorieDepense> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1755547774539137070L;
	/**
	 * Identifiant
	 */
	@Id
	private String id;
	/**
	 * Libelle
	 */
	private String libelle;
	/**
	 * Actif
	 */
	private boolean actif;
	/**
	 * Liste des sous catégories
	 */
	@Transient
	private Set<CategorieDepense> listeSSCategories = new HashSet<>();
	
	/**
	 * Liste des id des sous catégories
	 */
	private Set<String> listeIdsSSCategories = new HashSet<>();
	/**
	 * Catégorie
	 */
	@Transient
	private CategorieDepense categorieParente;
	
	/**
	 * Catégorie parente
	 */
	private String idCategorieParente;
	/**
	 * Est ce une catégorie ?
	 */
	private boolean categorie = true;

	
	/**
	 * Constructeur pour Spring Data MongSB
	 */
	public CategorieDepense(){
		this.id = UUID.randomUUID().toString();
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
	 * @return the listeSSCategories
	 */
	public Set<CategorieDepense> getListeSSCategories() {
		return listeSSCategories;
	}


	/**
	 * @return the categorieParente
	 */
	public CategorieDepense getCategorieParente() {
		return categorieParente;
	}



	/**
	 * @param listeIdsSSCategories the listeIdsSSCategories to set
	 */
	public void setListeIdsSSCategories(Set<String> listeIdsSSCategories) {
		this.listeIdsSSCategories = listeIdsSSCategories;
	}




	/**
	 * @param idCategorieParente the idCategorieParente to set
	 */
	public void setIdCategorieParente(String idCategorieParente) {
		this.idCategorieParente = idCategorieParente;
	}




	/**
	 * @return the categorie
	 */
	public boolean isCategorie() {
		return categorie;
	}

	/**
	 * @param categorie the categorie to set
	 */
	public void setCategorie(boolean categorie) {
		this.categorie = categorie;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.libelle;
	}


	/**
	 * @return the idf
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param idf the idf to set
	 */
	public void setId(String id) {
		this.id = id;
	}




	/**
	 * @return the actif
	 */
	public boolean isActif() {
		return actif;
	}


	


	/**
	 * @param categorieParente the categorieParente to set
	 */
	public void setCategorieParente(CategorieDepense categorieParente) {
		this.categorieParente = categorieParente;
	}





	/**
	 * @return the listeIdsSSCategories
	 */
	public Set<String> getListeIdsSSCategories() {
		return listeIdsSSCategories;
	}





	/**
	 * @return the idCategorieParente
	 */
	public String getIdCategorieParente() {
		return idCategorieParente;
	}





	/**
	 * @param actif the actif to set
	 */
	public void setActif(boolean actif) {
		this.actif = actif;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((libelle == null) ? 0 : libelle.hashCode());
		return result;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CategorieDepense)) {
			return false;
		}
		CategorieDepense other = (CategorieDepense) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (libelle == null) {
			if (other.libelle != null) {
				return false;
			}
		} else if (!libelle.equals(other.libelle)) {
			return false;
		}
		return true;
	}





	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CategorieDepense o) {
		if(o != null){
			return this.libelle.compareTo(o.getLibelle());
		}
		return 0;
	}
}
