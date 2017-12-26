/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons;


import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridOperations;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * Listener sur les actions sur la ligne de dépenses
 * @author vzwingma
 *
 */
public class ActionsLigneDepenseClickListener extends AbstractComponentListener implements Button.ClickListener, ConfirmationDialogCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9208265594447141871L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsLigneDepenseClickListener.class);


	private ActionsLigneBudget actions;

	@Override
	public void buttonClick(ClickEvent event) {
		EtatLigneDepenseEnum etat = EtatLigneDepenseEnum.PREVUE;
		actions = event.getButton().findAncestor(ActionsLigneBudget.class);
		if(event.getButton().getId().equals("buttonReel")){
			LOGGER.trace("Action : Activation");
			etat = EtatLigneDepenseEnum.REALISEE;
		}
		else if(event.getButton().getId().equals("buttonAnnuler")){
			LOGGER.trace("Action : Annulation");
			etat = EtatLigneDepenseEnum.ANNULEE;
		}
		else if(event.getButton().getId().equals("buttonReporter")){
			LOGGER.trace("Action : Reporter");
			etat = EtatLigneDepenseEnum.REPORTEE;
		}
		else if(event.getButton().getId().equals("buttonPrevue")){
			LOGGER.trace("Action : Prevue");
			etat = EtatLigneDepenseEnum.PREVUE;
		}
		else if(event.getButton().getId().equals("buttonSupprimer")){
			LOGGER.trace("Action : Supprimé");
			etat = null;
			// Confirmation
			ConfirmDialog confirm = new ConfirmDialog("Suppression de la dépense", 
					"Voulez-vous supprimer la dépense ?", "Oui", "Non", this);
			confirm.setWidth("400px");
			confirm.setHeight("150px");
			setPopupModale(confirm);			
		}		


		// Si non suppression mise à jour directe. Sinon, confirm dialog
		if(etat != null){
			// MISE A Jour des boutons. Désactivation du bouton cliqué
			for (Iterator<Component> iterator = ((CssLayout)(event.getButton().getParent())).iterator(); iterator.hasNext();) {
				Component type = (Component) iterator.next();
				if(type instanceof Button){
					type.setVisible(true);
				}

			};
			event.getButton().setVisible(false);	
			updateLigne(etat, getUtilisateurCourant().getLibelle());
		}
	}


	/**
	 * Mise à jour de la ligne
	 */
	private void updateLigne(EtatLigneDepenseEnum etat, String auteur){

		// Mise à jour de l'état

		actions.getControleur().miseAJourEtatLigne(etat);
		GridOperations tableauDepense = actions.findAncestor(GridOperations.class);

		//LOGGER.trace("Mode Edition ? {}", tableauDepense.isEditable());
		// Si en mode éditable. Pas de mise à jour. Seulement lors de la validation
		if(!false) { //tableauDepense.isEditable()){
			// Recalcul du budget
			BudgetMensuel budget = getBudgetMensuelCourant();

			try{
				getControleur(BudgetMensuelController.class).getServiceDepense().majEtatLigneDepense(budget, actions.getControleur().getIdOperation(), etat, auteur);
				// MAJ des tableaux
				if(etat == null){
					// Ligne supprimée
					//tableauDepense.getDataProvider().removeItem(actions.getControleur().getIdOperation());
				}
			}
			catch(DataNotFoundException|BudgetNotFoundException e){
				Notification.show("La dépense est introuvable ou n'a pas été enregistrée", Type.ERROR_MESSAGE);
			}
		//	getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
		}

	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback#response(boolean)
	 */
	@Override
	public void response(boolean ok) {
		if(ok){
			updateLigne(null, getUtilisateurCourant().getLibelle());
		}
	}
}
