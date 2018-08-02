/**
 * 
 */
package com.terrier.finances.gestion.ui.styles.comptes;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.IconGenerator;

/**
 * Icone des comptes bancaires
 * @author vzwingma
 *
 */
public class ComptesItemIconStyle implements IconGenerator<CompteBancaire> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4291126468642797635L;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.IconGenerator#apply(java.lang.Object)
	 */
	@Override
	public Resource apply(CompteBancaire compte) {
		return new ThemeResource(compte.getItemIcon());
	}

}
