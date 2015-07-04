/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.UISessionManager;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableSuiviDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.event.Action;

/**
 * Controleur du menu du tableau des résumés
 * @author vzwingma
 *
 */
public class TableSuiviDepensesActionMenuHandler extends AbstractComponentListener implements Action.Handler{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4256635378437805758L;


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TableSuiviDepensesActionMenuHandler.class);


	private static final Action SET_LAST_DEPENSE = new Action("Marque comme dernière opération relevée sur le compte");

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
			if(UISessionManager.getSession().getBudgetMensuelCourant().isActif()){
				return new Action[]{ SET_LAST_DEPENSE };
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
			String idDepense = (String) target;
			LOGGER.info("Marquage de la dépense {} comme dernière action relevée", idDepense);
			tableSuivi.getControleur().getServiceDepense().setLigneDepenseAsDerniereOperation(UISessionManager.getSession().getBudgetMensuelCourant(), idDepense);
			getControleur(BudgetMensuelController.class).miseAJourVueDonnees();
		}		
	}
}
