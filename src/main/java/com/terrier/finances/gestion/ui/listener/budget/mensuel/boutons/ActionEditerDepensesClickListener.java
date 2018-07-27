/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons;

import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIComponent;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author vzwingma
 *
 */
public class ActionEditerDepensesClickListener extends AbstractComponentListener implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823872638217135776L;



	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Button editer = event.getButton();
		BudgetMensuelPage page  = AbstractUIComponent.getParentComponent(editer, BudgetMensuelPage.class);
		if(page != null){
			getControleur(BudgetMensuelController.class).setTableOnEditableMode(true);
		}
		else{
			Notification.show("Erreur lors de la recherche de la page associée au bouton", Type.ERROR_MESSAGE);
		}
	}
}

