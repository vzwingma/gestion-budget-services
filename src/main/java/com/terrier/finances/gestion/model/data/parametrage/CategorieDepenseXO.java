package com.terrier.finances.gestion.model.data.parametrage;

import java.util.HashSet;
import java.util.Set;

/**
 * Catégorie de dépense pour la réponse XML
 * @author vzwingma
 *
 */
public class CategorieDepenseXO extends CategorieDepenseDTO {
	/**
	 * Liste des sous catégories
	 */
	private Set<CategorieDepenseXO> listeSSCategories = new HashSet<CategorieDepenseXO>();

	/**
	 * @return the listeSSCategories
	 */
	public Set<CategorieDepenseXO> getListeSSCategoriesXOs() {
		return listeSSCategories;
	}
}
