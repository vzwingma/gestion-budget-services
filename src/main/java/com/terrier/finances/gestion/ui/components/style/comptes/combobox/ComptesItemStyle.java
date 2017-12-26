/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style.comptes.combobox;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.vaadin.ui.StyleGenerator;

/**
 * @author vzwingma
 *
 */
public class ComptesItemStyle implements StyleGenerator<CompteBancaire> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 682039604448023201L;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.StyleGenerator#apply(java.lang.Object)
	 */
	@Override
	public String apply(CompteBancaire compteBancaire) {
		if(!compteBancaire.isActif()){
			return "barre";
		}
		return null;
	}
}
