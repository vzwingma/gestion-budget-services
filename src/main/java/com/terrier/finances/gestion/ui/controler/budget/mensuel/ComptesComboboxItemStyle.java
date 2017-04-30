/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel;

import java.util.List;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ComboBox.ItemStyleGenerator;

/**
 * @author vzwingma
 *
 */
public class ComptesComboboxItemStyle implements ItemStyleGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719540462663954289L;
	// Comptes
	private List<CompteBancaire> comptes;
	
	public ComptesComboboxItemStyle( List<CompteBancaire> comptes){
		this.comptes = comptes;
	}
	/* (non-Javadoc)
	 * @see com.vaadin.ui.ComboBox.ItemStyleGenerator#getStyle(com.vaadin.ui.ComboBox, java.lang.Object)
	 */
	@Override
	public String getStyle(ComboBox source, Object itemId) {
		if(comptes != null && !comptes.isEmpty()){
			for (CompteBancaire compteBancaire : comptes) {
				if(((String)itemId).equals(compteBancaire.getId()) && !compteBancaire.isActif()){
					return "barre";
				}
			}
		}
		return null;
	}

}
