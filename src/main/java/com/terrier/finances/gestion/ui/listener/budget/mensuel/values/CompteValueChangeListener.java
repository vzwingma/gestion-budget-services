/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;

/**
 * @author vzwingma
 *
 */
public class CompteValueChangeListener implements ValueChangeListener<CompteBancaire> {

	private static final long serialVersionUID = 4952920319582904789L;

	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(CompteValueChangeListener.class);
	
	
	private BudgetMensuelController controleur;

	/**
	 * Constructeur avec le controleurs
	 * @param controleur
	 */
	public CompteValueChangeListener(BudgetMensuelController controleur){
		this.controleur = controleur;
	}

	@Override
	public void valueChange(ValueChangeEvent<CompteBancaire> event) {
		LOGGER.info("Changement du compte : {}->{}", event.getOldValue().getId(), event.getValue().getId());
		// Modification du compte
		this.controleur.initRangeDebutFinMois(event.getValue().getId());
		this.controleur.miseAJourVueDonnees();
	}
}
