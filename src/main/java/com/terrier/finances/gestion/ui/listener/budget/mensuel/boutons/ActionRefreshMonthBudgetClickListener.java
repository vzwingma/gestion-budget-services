/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Refresh du mois courant
 * @author vzwingma
 *
 */
public class ActionRefreshMonthBudgetClickListener extends AbstractComponentListener implements ClickListener, ConfirmDialog.ConfirmationDialogCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823872638217135776L;

	private BudgetMensuelPage page;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Button refreshMont = event.getButton();
		page  = (BudgetMensuelPage)refreshMont.getParent().getParent().getParent().getParent().getParent();

		Calendar c = Calendar.getInstance();
		BudgetMensuel budgetMensuelCourant = getBudgetMensuelCourant();
		
		c.set(Calendar.MONTH, budgetMensuelCourant.getMois());
		SimpleDateFormat sfd = new SimpleDateFormat("MMMM YYYY", Locale.FRENCH);

		/** Alerte **/
		String warnMoisActif = "";
		int moisPrecedent = 0;
		int anneePrecedente = budgetMensuelCourant.getAnnee();
		if(budgetMensuelCourant.getMois() == Calendar.JANUARY){
			moisPrecedent = Calendar.DECEMBER;
			anneePrecedente = budgetMensuelCourant.getAnnee() - 1;
		}
		else{
			moisPrecedent = budgetMensuelCourant.getMois() - 1;
		}
		Boolean budgetPrecedentActif = page.getControleur().getServiceDepense().isBudgetMensuelActif(
				budgetMensuelCourant.getCompteBancaire().getId(), 
				moisPrecedent, anneePrecedente);
		if(budgetPrecedentActif){
			warnMoisActif = "<span style=\"color: red;\"><br> Attention : Le mois précédent n'est pas clos !</span>";
		}


		// Confirmation
		ConfirmDialog confirm = new ConfirmDialog("Réinitialisation du budget mensuel courant", 
				"Voulez vous réinitialiser le budget du mois de "+ sfd.format(c.getTime())+" ? " +
						warnMoisActif, "Oui", "Non", this);
		confirm.setWidth("400px");
		confirm.setHeight("150px");
		setPopupModale(confirm);		
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback#response(boolean)
	 */
	@Override
	public void response(boolean ok) {
		if(ok){
			page.getControleur().reinitialiserBudgetCourant();
		}
	}


}

