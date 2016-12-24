/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIComponent;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableSuiviDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
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
public class ActionValiderAnnulerEditionDepenseListener extends AbstractComponentListener implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823872638217135776L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionValiderAnnulerEditionDepenseListener.class);


	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Button editer = event.getButton();
		boolean state = editer.getCaption().contains("Valider");

		BudgetMensuelPage page  = AbstractUIComponent.getParentComponent(editer, BudgetMensuelPage.class);
		if(page != null){
			getControleur(BudgetMensuelController.class).setTableOnEditableMode(false);
			if(state){
				refreshModele(page.getTableSuiviDepense());

				/**
				 * Recalcul du budget
				 */
				getControleur(BudgetMensuelController.class).getServiceDepense().calculBudgetEtSauvegarde(getBudgetMensuelCourant());
				/**
				 * MAJ des tableaux
				 */
				getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
				Notification.show("Les dépenses ont bien été mises à jour", Notification.Type.TRAY_NOTIFICATION);
			}
		}
	}



	/**
	 * Refresh
	 * @param table
	 */
	@SuppressWarnings("rawtypes")
	private void refreshModele(TableSuiviDepense table){
		BudgetMensuel budgetCourant = getBudgetMensuelCourant();
		String auteur = getUtilisateurCourant().getLibelle();

		for (Iterator iterator = table.getItemIds().iterator(); iterator.hasNext();) {
			String itemId = (String) iterator.next();

			for (Iterator iteratorP = table.getItem(itemId).getItemPropertyIds().iterator(); iteratorP.hasNext();){
				String propId = (String) iteratorP.next();

				LOGGER.trace(" Refresh modèle {} de {} : {}", propId, itemId, table.getItem(itemId).getItemProperty(propId).getValue());
				/**
				 * Pour les actions, on transforme ActionsLigneBudget en booléen
				 */
				if(propId.equals(EntetesTableSuiviDepenseEnum.ACTIONS.getId())){
					/**
					 * Mise à jour de la dépense
					 */
					ActionsLigneBudget actions = (ActionsLigneBudget)table.getItem(itemId).getItemProperty(propId).getValue();
					if(actions != null){
						EtatLigneDepenseEnum etat = EtatLigneDepenseEnum.ANNULEE;
						if(!actions.getButtonAnnuler().isVisible()){
							etat = EtatLigneDepenseEnum.ANNULEE;
						}
						else if(!actions.getButtonPrevue().isVisible()){
							etat = EtatLigneDepenseEnum.PREVUE;
						}
						else if(!actions.getButtonReel().isVisible()){
							etat = EtatLigneDepenseEnum.REALISEE;
						}
						else if(!actions.getButtonReporter().isVisible()){
							etat = EtatLigneDepenseEnum.REPORTEE;
						}
						try{
							getControleur(BudgetMensuelController.class).getServiceDepense().majLigneDepense(budgetCourant, itemId,  "Etat", EtatLigneDepenseEnum.class, etat, auteur);
						}
						catch(DataNotFoundException e){
							Notification.show("La dépense ["+itemId+"] est introuvable ou n'a pas été enregistrée", Type.ERROR_MESSAGE);
						}
					}
				}
				else{
					/**
					 * Mise à jour de la dépense
					 */
					try{
						getControleur(BudgetMensuelController.class).getServiceDepense().majLigneDepense(budgetCourant, itemId,  propId, table.getItem(itemId).getItemProperty(propId).getType(), table.getItem(itemId).getItemProperty(propId).getValue(), auteur);
					}
					catch(DataNotFoundException e){
						Notification.show("La dépense ["+itemId+"] est introuvable ou n'a pas été enregistrée", Type.ERROR_MESSAGE);
					}
				}
			}

		}
	}
}

