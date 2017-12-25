/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.values;

import java.util.Calendar;
import java.util.Date;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;

/**
 * @author vzwingma
 *
 */
public class CompteValueChangeListener implements ValueChangeListener<String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4952920319582904789L;

	private BudgetMensuelController controleur;

	public CompteValueChangeListener(BudgetMensuelController controleur){
		this.controleur = controleur;
	}

	@Override
	public void valueChange(ValueChangeEvent<String> event) {
		// Modification de la date
		boolean miseAJour = false;
		String idCompte = (String)this.controleur.getCompte().getValue();
		// Modification du compte
		miseAJour = true;
		this.controleur.initRangeDebutFinMois(idCompte);
		this.controleur.miseAJourVueDonnees();
	}

}
