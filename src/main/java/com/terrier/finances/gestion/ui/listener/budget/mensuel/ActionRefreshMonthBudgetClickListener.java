/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
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
		c.set(Calendar.MONTH, UISessionManager.getSession().getBudgetMensuelCourant().getMois());
		SimpleDateFormat sfd = new SimpleDateFormat("MMMM YYYY", Locale.FRENCH);
		
		/** Alerte **/
		String warnMoisActif = "";
		int moisPrecedent = 0;
		int anneePrecedente = UISessionManager.getSession().getBudgetMensuelCourant().getAnnee();
		if(UISessionManager.getSession().getBudgetMensuelCourant().getMois() == Calendar.JANUARY){
			moisPrecedent = Calendar.DECEMBER;
			anneePrecedente = UISessionManager.getSession().getBudgetMensuelCourant().getAnnee() - 1;
		}
		else{
			moisPrecedent = UISessionManager.getSession().getBudgetMensuelCourant().getMois() - 1;
		}
		try {
			BudgetMensuelDTO budgetPrecedent = page.getControleur().getServiceDepense().chargerBudgetMensuelConsultation(
					UISessionManager.getSession().getBudgetMensuelCourant().getCompteBancaire().getId(), 
					moisPrecedent, anneePrecedente);
			if(budgetPrecedent.isActif()){
				warnMoisActif = "<span style=\"color: red;\"><br> Attention : Le mois précédent n'est pas clos !</span>";
			}
		} catch (BudgetNotFoundException | DataNotFoundException e) { }


		// Confirmation
		ConfirmDialog confirm = new ConfirmDialog("Réinitialisation du budget mensuel courant", 
				"Voulez vous réinitialiser le budget du mois de "+ sfd.format(c.getTime())+" ? " +
						warnMoisActif, "Oui", "Non", this);
		confirm.setWidth("400px");
		confirm.setHeight("150px");
		UISessionManager.getSession().setPopupModale(confirm);		
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

