package com.terrier.finances.gestion.ui.listener.budget.mensuel.values;

import java.util.Calendar;
import java.util.Date;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;

/**
 * Changement de la date du budget sur l'IHM
 * @author vzwingma
 *
 */
public class DateBudgetValueChangeListener implements ValueChangeListener<Date>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5730762746251471137L;

	private BudgetMensuelController controleur;

	public DateBudgetValueChangeListener(BudgetMensuelController controleur){
		this.controleur = controleur;
	}


	@Override
	public void valueChange(ValueChangeEvent<Date> event) {
		// Modification de la date
		String idCompte = (String)this.controleur.getCompte().getValue();
		Calendar d = Calendar.getInstance();
		d.setTime(event.getValue());
		d.set(Calendar.DAY_OF_MONTH, 1);
		this.controleur.setRangeFinMois(d, idCompte);
		if(d.get(Calendar.MONTH) != this.controleur.getOldMois() || d.get(Calendar.YEAR) != this.controleur.getOldAnnee()){
			this.controleur.miseAJourVueDonnees();			
		}
	}
}
