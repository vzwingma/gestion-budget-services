package io.github.vzwingma.finances.budget.services.communs.data.parametrages.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.vzwingma.finances.budget.services.communs.data.abstrait.AbstractAPIObjectModel;
import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.enums.IdsCategoriesEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * Catégorie d'opérations
 * @author vzwingma
 *
 */
// @Document(collection = "categoriesoperations")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CategorieOperation extends AbstractAPIObjectModel implements Comparable<CategorieOperation> { //

	/**
	 * 
	 */
	private static final long serialVersionUID = -1755547774539137070L;
	/**
	 * Identifiant
	 */
	// @Id
	// @Schema(description = "Identifiant")
	private String id;
	/**
	 * Libelle
	 */
	@EqualsAndHashCode.Include
	// @Schema(description = "Libellé")
	private String libelle;
	/**
	 * Actif
	 */
	// @Schema(description = "Etat d'activité")
	private boolean actif;
	/**
	 * Liste des sous catégories
	 */
	// @Schema(description = "Liste des sous catégories")
	private Set<CategorieOperation> listeSSCategories;

	/**
	 * Catégorie
	 */
	@JsonIgnore
	private CategorieOperation categorieParente;

	/**
	 * Est ce une catégorie ?
	 */
	// @Schema(description = "Est ce une catégorie")
	private boolean categorie = true;


	/**
	 * Constructeur pour Spring Data MongSB
	 */
	public CategorieOperation(){
		this.id = UUID.randomUUID().toString();
	}
	
	/**
	 * Constructeur pour le clone
	 * @param typeCategorie id du parent
	 */
	public CategorieOperation(IdsCategoriesEnum typeCategorie){
		this.id = typeCategorie.getId();
	}

	/**
	 * @return the listeSSCategories
	 */
	public Set<CategorieOperation> getListeSSCategories() {
		return listeSSCategories;
	}

	/**
	 * @param listeSSCategories the listeSSCategories to set
	 */
	public void setListeSSCategories(Set<CategorieOperation> listeSSCategories) {
		if(isCategorie()) {
			this.listeSSCategories = listeSSCategories;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.libelle;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CategorieOperation o) {
		if(o != null){
			return this.libelle.compareTo(o.getLibelle());
		}
		return 0;
	}
}
