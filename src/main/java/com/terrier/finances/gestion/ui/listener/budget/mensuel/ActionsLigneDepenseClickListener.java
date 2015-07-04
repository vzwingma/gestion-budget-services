/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel;


import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.ui.UISessionManager;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableSuiviDepense;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog;
import com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

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
		actions = (ActionsLigneBudget)event.getButton().getParent().getParent().getParent();
		if(event.getButton().getId().equals("buttonReel")){
			LOGGER.info("Activation");
			etat = EtatLigneDepenseEnum.REALISEE;
		}
		else if(event.getButton().getId().equals("buttonAnnuler")){
			LOGGER.info("Annulation");
			etat = EtatLigneDepenseEnum.ANNULEE;
		}
		else if(event.getButton().getId().equals("buttonReporter")){
			LOGGER.info("Reporter");
			etat = EtatLigneDepenseEnum.REPORTEE;
		}
		else if(event.getButton().getId().equals("buttonPrevue")){
			LOGGER.info("Prevue");
			etat = EtatLigneDepenseEnum.PREVUE;
		}
		else if(event.getButton().getId().equals("buttonSupprimer")){
			LOGGER.info("Supprimé");
			etat = null;
			// Confirmation
			ConfirmDialog confirm = new ConfirmDialog("Suppression de la dépense", 
					"Voulez-vous supprimer la dépense ?", "Oui", "Non", this);
			confirm.setWidth("400px");
			confirm.setHeight("150px");
			UISessionManager.getSession().setPopupModale(confirm);			

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
			updateLigne(etat, UISessionManager.getSession().getUtilisateurCourant().getLibelle());
		}
	}

	
	/**
	 * Mise à jour de la ligne
	 */
	private void updateLigne(EtatLigneDepenseEnum etat, String auteur){

		// Mise à jour de l'état
		
		actions.getControleur().miseAJourEtatLigne(etat);
		
		TableSuiviDepense tableauDepense = (TableSuiviDepense)actions.getParent();
		LOGGER.info("Mode Edition ? {}", tableauDepense.isEditable());
		// Si en mode éditable. Pas de mise à jour. Seulement lors de la validation
		if(!tableauDepense.isEditable()){
			/**
			 * Recalcul du budget
			 */
			BudgetMensuel budget = UISessionManager.getSession().getBudgetMensuelCourant();
			getControleur(BudgetMensuelController.class).getServiceDepense().majEtatLigneDepense(budget, actions.getControleur().getIdDepense(), etat, auteur);
			/**
			 * MAJ des tableaux
			 */
			if(etat == null){
				// Ligne supprimée
				tableauDepense.removeItem(actions.getControleur().getIdDepense());
			}
			getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.confirm.ConfirmDialog.ConfirmationDialogCallback#response(boolean)
	 */
	@Override
	public void response(boolean ok) {
		if(ok){
			updateLigne(null, UISessionManager.getSession().getUtilisateurCourant().getLibelle());
			
		}
	}
}
