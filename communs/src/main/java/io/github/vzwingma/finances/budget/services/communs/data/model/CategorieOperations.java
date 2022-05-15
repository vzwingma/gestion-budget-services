package io.github.vzwingma.finances.budget.services.communs.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.vzwingma.finances.budget.services.communs.data.abstrait.AbstractAPIObjectModel;
import io.github.vzwingma.finances.budget.services.communs.data.enums.IdsCategoriesEnum;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

/**
 * Catégorie d'opérations
 * @author vzwingma
 *
 */
@MongoEntity(collection = "categoriesoperations")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CategorieOperations extends AbstractAPIObjectModel implements Comparable<CategorieOperations> { //

	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * Identifiant
	 */
	//@Id
	@Schema(description = "Identifiant")
	private String id;
	/**
	 * Libelle
	 */
	@EqualsAndHashCode.Include
	@Schema(description = "Libellé")
	private String libelle;
	/**
	 * Actif
	 */
	@Schema(description = "Etat d'activité")
	private boolean actif;
	/**
	 * Liste des sous catégories
	 */
	@Schema(description = "Liste des sous catégories")
	private Set<CategorieOperations> listeSSCategories;

	/**
	 * Catégorie
	 */
	@JsonIgnore
	private CategorieOperations categorieParente;

	/**
	 * Est ce une catégorie ?
	 */
	@Schema(description = "Est ce une catégorie")
	private boolean categorie = true;


	/**
	 * Constructeur pour Spring Data MongSB
	 */
	public CategorieOperations(){
		this.id = UUID.randomUUID().toString();
	}
	
	/**
	 * Constructeur pour le clone
	 * @param typeCategorie id du parent
	 */
	public CategorieOperations(IdsCategoriesEnum typeCategorie){
		this.id = typeCategorie.getId();
	}

	/**
	 * @return the listeSSCategories
	 */
	public Set<CategorieOperations> getListeSSCategories() {
		return listeSSCategories;
	}

	/**
	 * @param listeSSCategories the listeSSCategories to set
	 */
	public void setListeSSCategories(Set<CategorieOperations> listeSSCategories) {
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
	public int compareTo(CategorieOperations o) {
		if(o != null){
			return this.libelle.compareTo(o.getLibelle());
		}
		return 0;
	}
}
