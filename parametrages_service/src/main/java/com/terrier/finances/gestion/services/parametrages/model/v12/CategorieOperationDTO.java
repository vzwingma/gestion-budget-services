package com.terrier.finances.gestion.services.parametrages.model.v12;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * Catégorie de dépense
 * @author vzwingma
 *
 */
@Document(collection = "categoriesoperations")
@Getter @Setter
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
	 * @return the listeSSCategories
	 */
	public Set<CategorieOperationDTO> getListeSSCategories() {
		if(isCategorie()) {
			return listeSSCategories;
		}
		else {
			return null;
		}
	}

	/**
	 * @param listeSSCategories the listeSSCategories to set
	 */
	public void setListeSSCategories(Set<CategorieOperationDTO> listeSSCategories) {
		if(isCategorie()) {
			this.listeSSCategories = listeSSCategories;
		}
		else {
			this.listeSSCategories = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.libelle;
	}
}
