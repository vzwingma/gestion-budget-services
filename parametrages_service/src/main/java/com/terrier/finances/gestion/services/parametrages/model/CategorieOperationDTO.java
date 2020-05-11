package com.terrier.finances.gestion.services.parametrages.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Catégorie de dépense
 * @author vzwingma
 *
 */
@Document(collection = "categoriesoperations")
@Deprecated
public class CategorieOperationDTO {

	/**
	 * Identifiant
	 */
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
	 * Liste des id des sous catégories
	 */
	private Set<CategorieOperationDTO> listeSSCategories = new HashSet<>();
	/**
	 * Est ce une catégorie ?
	 */
	private boolean categorie = true;

	
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
	public Set<CategorieOperationDTO> getListeSSCategories() {
		return listeSSCategories;
	}

	/**
	 * @param listeSSCategories the listeSSCategories to set
	 */
	public void setListeSSCategories(Set<CategorieOperationDTO> listeSSCategories) {
		this.listeSSCategories = listeSSCategories;
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
	 * @param actif the actif to set
	 */
	public void setActif(boolean actif) {
		this.actif = actif;
	}
}
