/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel;

import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIComponent;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
			// Activation du tableau
			page.getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.TYPE.getId(), false);
			page.getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), false);
			// Inversion du champ Libelle
			page.getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE.getId(), false);
			page.getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId(), true);
			page.getTableSuiviDepense().setEditable(true);
			page.getTableSuiviDepense().setColumnWidth(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId(), TableSuiviDepenseController.TAILLE_COLONNE_DATE_EDITEE);


			page.getButtonValider().setVisible(true);
			page.getButtonValider().setEnabled(true);
			page.getButtonAnnuler().setVisible(true);
			page.getButtonAnnuler().setEnabled(true);
			page.getButtonEditer().setVisible(false);
			page.getButtonEditer().setEnabled(false);
			page.getButtonCreate().setVisible(false);
			page.getButtonCreate().setEnabled(false);		
		}
		else{
			Notification.show("Erreur lors de la recherche de la page associée au bouton", Type.ERROR_MESSAGE);
		}
	}
}

