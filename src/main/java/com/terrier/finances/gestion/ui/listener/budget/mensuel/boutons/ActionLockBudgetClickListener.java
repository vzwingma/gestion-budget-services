/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons;

import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Lock du mois
 * @author vzwingma
 *
 */
public class ActionLockBudgetClickListener extends AbstractComponentListener implements ClickListener, ConfirmDialog.ConfirmationDialogCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823872638217135776L;

	private BudgetMensuelPage page;
	
	private boolean setBudgetActif = false;
	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Button editer = event.getButton();
		page  = (BudgetMensuelPage)editer.getParent().getParent().getParent().getParent().getParent();
		
		boolean budgetActif = getBudgetMensuelCourant().isActif();
		
		// Confirmation
		ConfirmDialog confirm = new ConfirmDialog((budgetActif ? "Clôture" : "Ouverture") + " du budget mensuel", 
				"Voulez vous "+(budgetActif ? "cloturer" : "réouvrir")+" le budget mensuel ?", "Oui", "Non", this);
		confirm.setWidth("400px");
		confirm.setHeight("150px");
		setBudgetActif = !budgetActif;
		setPopupModale(confirm);
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback#response(boolean)
	 */
	@Override
	public void response(boolean ok) {
		if(ok){
			page.getControleur().lockBudget(setBudgetActif);
		}
	}
}

