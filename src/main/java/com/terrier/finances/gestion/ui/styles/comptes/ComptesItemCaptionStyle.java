package com.terrier.finances.gestion.ui.styles.comptes;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.vaadin.ui.ItemCaptionGenerator;

/**
 * Style d'affichage de la combobox Compte
 * @author vzwingma
 *
 */
public class ComptesItemCaptionStyle implements ItemCaptionGenerator<CompteBancaire> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7142780309734425694L;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.ItemCaptionGenerator#apply(java.lang.Object)
	 */
	@Override
	public String apply(CompteBancaire item) {
		return item.getLibelle();
	}

}
