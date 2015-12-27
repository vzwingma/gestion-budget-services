package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparateur catégorie de dépense
 * @author vzwingma
 *
 */
public class CategorieDepenseComparator implements Comparator<CategorieDepense>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3725080243715272280L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CategorieDepense categorie1, CategorieDepense categorie2) {
		return categorie1.getLibelle().compareTo(categorie2.getLibelle());
	}
}
