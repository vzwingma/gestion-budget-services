/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.components.abstrait.AbstractUIComponent;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridOperations;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;

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
			if(state){
				boolean validateForm = page.getGridOperations().getControleur().validateEditableForm();
				if(validateForm){
					refreshModele(page.getGridOperations());
					// Recalcul du budget
					getControleur(BudgetMensuelController.class).getServiceDepense().calculBudgetEtSauvegarde(getBudgetMensuelCourant());
					getControleur(BudgetMensuelController.class).setTableOnEditableMode(false);
					// MAJ des tableaux
					getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
					Notification.show("Les dépenses ont bien été mises à jour", Notification.Type.TRAY_NOTIFICATION);
				}
				else{
					LOGGER.warn("Les données sont incorrectes pas de mise à jour");
				}
			}
			else{
				getControleur(BudgetMensuelController.class).setTableOnEditableMode(false);
			}
		}
	}



	/**
	 * Refresh
	 * @param table
	 */
	private void refreshModele(GridOperations table){
		/*
		BudgetMensuel budgetCourant = getBudgetMensuelCourant();
		String auteur = getUtilisateurCourant().getLibelle();

		for (Iterator iterator = table.getItemIds().iterator(); iterator.hasNext();) {
			String itemId = (String) iterator.next();

			for (Iterator iteratorP = table.getItem(itemId).getItemPropertyIds().iterator(); iteratorP.hasNext();){
				String propId = (String) iteratorP.next();

				LOGGER.trace(" Refresh modèle {} de {} : {}", propId, itemId, table.getItem(itemId).getItemProperty(propId).getValue());
				// Pour les actions, on transforme ActionsLigneBudget en booléen
				if(propId.equals(EntetesTableSuiviDepenseEnum.ACTIONS.getId())){
					// Mise à jour de la dépense
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
					// Mise à jour de la dépense
					try{
						getControleur(BudgetMensuelController.class).getServiceDepense().majLigneDepense(budgetCourant, itemId,  propId, table.getItem(itemId).getItemProperty(propId).getType(), table.getItem(itemId).getItemProperty(propId).getValue(), auteur);
					}
					catch(DataNotFoundException e){
						Notification.show("La dépense ["+itemId+"] est introuvable ou n'a pas été enregistrée", Type.ERROR_MESSAGE);
					}
				}
			}

		}
		*/
	}
}

