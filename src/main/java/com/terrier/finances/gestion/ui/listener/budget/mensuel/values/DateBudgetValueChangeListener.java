package com.terrier.finances.gestion.ui.listener.budget.mensuel.values;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;

/**
 * Changement de la date du budget sur l'IHM
 * @author vzwingma
 *
 */
public class DateBudgetValueChangeListener implements ValueChangeListener<LocalDate>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5730762746251471137L;

	private BudgetMensuelController controleur;

	public DateBudgetValueChangeListener(BudgetMensuelController controleur){
		this.controleur = controleur;
	}


	@Override
	public void valueChange(ValueChangeEvent<LocalDate> event) {

		// Modification de la date
		String idCompte = (String)this.controleur.getCompte().getValue().getId();
		Instant dateBudget = event.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
		dateBudget.with(ChronoField.DAY_OF_MONTH, 1);
		this.controleur.setRangeFinMois(dateBudget.atZone(ZoneId.systemDefault()).toLocalDate(), idCompte);
		if(dateBudget.get(ChronoField.MONTH_OF_YEAR) != this.controleur.getOldMois() || dateBudget.get(ChronoField.YEAR) != this.controleur.getOldAnnee()){
			this.controleur.miseAJourVueDonnees();			
		}
	}
}
