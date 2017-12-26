/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridOperations;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.event.Action;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.components.grid.ItemClickListener;

/**
 * Controleur du menu du tableau des résumés
 * @author vzwingma
 *
 */
public class TableSuiviDepensesActionMenuHandler extends AbstractComponentListener implements Action.Handler, ItemClickListener<LigneDepense>{


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
		GridOperations tableSuivi = (GridOperations)sender;
		if(SET_LAST_DEPENSE.equals(action) && target != null){
			putIdAsLastDepense(tableSuivi, (LigneDepense)target);
		}	
		else if(EDIT_DEPENSE.equals(action) && target != null){
			editDepense(tableSuivi, (String)target);
		}
	}


	/**
	 * Set as last depense 
	 * @param tableSuivi table
	 * @param depense id
	 */
	private void putIdAsLastDepense(GridOperations tableSuivi, LigneDepense depense){
		LOGGER.info("Marquage de la dépense {} comme dernière action relevée", depense.getId());
		tableSuivi.getControleur().getServiceDepense().setLigneDepenseAsDerniereOperation(getBudgetMensuelCourant(), depense.getId());
		getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
	}
	
	/**
	 * Set as last depense 
	 * @param tableSuivi table
	 * @param idDepense id
	 */
	private void editDepense(GridOperations tableSuivi, String idDepense){
		LOGGER.info("Edition de la dépense {} : {}", idDepense, tableSuivi.getSelectedItems());
		
//		TableSuiviDepenseEditedFieldFactory factory = (TableSuiviDepenseEditedFieldFactory)tableSuivi.getTableFieldFactory();
//		factory.setIdLigneEditable(idDepense);
		getControleur(BudgetMensuelController.class).setTableOnEditableMode(true);
		// getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
	}

	@Override
	public void itemClick(ItemClick<LigneDepense> event) {
		if(event.getMouseEventDetails().isDoubleClick()){
			GridOperations tableSuivi = (GridOperations)event.getSource();
			putIdAsLastDepense(tableSuivi, event.getItem());
		}
		
	}
}
