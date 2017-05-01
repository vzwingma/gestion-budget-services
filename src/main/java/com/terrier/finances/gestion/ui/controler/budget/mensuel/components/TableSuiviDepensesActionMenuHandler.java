/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableSuiviDepense;
import com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode.TableSuiviDepenseEditedFieldFactory;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.event.Action;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;

/**
 * Controleur du menu du tableau des résumés
 * @author vzwingma
 *
 */
public class TableSuiviDepensesActionMenuHandler extends AbstractComponentListener implements Action.Handler, ItemClickListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4256635378437805758L;


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TableSuiviDepensesActionMenuHandler.class);


	private static final Action SET_LAST_DEPENSE = new Action("Marque comme dernière opération relevée sur le compte");

	private static final Action EDIT_DEPENSE = new Action("Editer la dépense");
	
	/**
	 * Liste des actions du menu
	 * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Action[] getActions(Object target, Object sender) {
		if (target == null) {
			// Context menu in an empty space -> Aucune action
			return null;

		} else {
			if(getBudgetMensuelCourant().isActif()){
				return new Action[]{ EDIT_DEPENSE, SET_LAST_DEPENSE };
			}
			else{
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#handleAction(com.vaadin.event.Action, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void handleAction(Action action, Object sender, Object target) {
		TableSuiviDepense tableSuivi = (TableSuiviDepense)sender;
		if(SET_LAST_DEPENSE.equals(action) && target != null){
			putIdAsLastDepense(tableSuivi, (String)target);
		}	
		else if(EDIT_DEPENSE.equals(action) && target != null){
			editDepense(tableSuivi, (String)target);
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
	 */
	@Override
	public void itemClick(ItemClickEvent event) {
		if(event.isDoubleClick()){
			TableSuiviDepense tableSuivi = (TableSuiviDepense)event.getSource();
			putIdAsLastDepense(tableSuivi, (String)event.getItemId());
		}
	}
	
	/**
	 * Set as last depense 
	 * @param tableSuivi table
	 * @param idDepense id
	 */
	private void putIdAsLastDepense(TableSuiviDepense tableSuivi, String idDepense){
		LOGGER.info("Marquage de la dépense {} comme dernière action relevée", idDepense);
		tableSuivi.getControleur().getServiceDepense().setLigneDepenseAsDerniereOperation(getBudgetMensuelCourant(), idDepense);
		getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
	}
	
	/**
	 * Set as last depense 
	 * @param tableSuivi table
	 * @param idDepense id
	 */
	private void editDepense(TableSuiviDepense tableSuivi, String idDepense){
		LOGGER.info("Edition de la dépense {} : {}", idDepense, tableSuivi.getItem(idDepense));
		
		TableSuiviDepenseEditedFieldFactory factory = (TableSuiviDepenseEditedFieldFactory)tableSuivi.getTableFieldFactory();
		factory.setIdLigneEditable(idDepense);
		getControleur(BudgetMensuelController.class).setTableOnEditableMode(true);
		// getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
	}
}
