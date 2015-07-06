/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * Changement d'un champ == Blur sur le champ description
 * @author vzwingma
 *
 */
public class BlurDescriptionValueChangeListener extends AbstractComponentListener implements ValueChangeListener{

	// Controleur
	private CreerDepenseController controleur;

	/**
	 * Constructeur
	 * @param controleur controleur associé
	 */
	public BlurDescriptionValueChangeListener(CreerDepenseController controleur){
		this.controleur = controleur;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7460353635366793837L;

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void valueChange(ValueChangeEvent event) {		
		this.controleur.blurOnAutocompleteField();
	}

}
