/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;

/**
 * Style des cellules du tableau de dépense
 * @author vzwingma
 *
 */
public class TableDepensesDescriptionGenerator implements ItemDescriptionGenerator {



	/**
	 * 
	 */
	private static final long serialVersionUID = -1811163367277413727L;
	// Controleur
	private TableSuiviDepenseController controleur;

	/**
	 * Constructeur
	 * @param controleur ajout du controleur
	 */
	public TableDepensesDescriptionGenerator(TableSuiviDepenseController controleur){
		this.controleur = controleur;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator#generateDescription(com.vaadin.ui.Component, java.lang.Object, java.lang.Object)
	 */
	@Override
	public String generateDescription(Component source, Object itemId, Object propertyId) {

		if(propertyId != null && ((String)propertyId).equals(EntetesTableSuiviDepenseEnum.LIBELLE.getId())){

			BudgetMensuel budgetCourant = UISessionManager.getSession().getBudgetMensuelCourant();
			String idDepense = (String)itemId;
			if(controleur.getServiceDepense() != null && budgetCourant != null && idDepense != null && budgetCourant.getListeDepenses() != null){
				for (LigneDepense depense : budgetCourant.getListeDepenses()) {
					if(idDepense.equals(depense.getId()) && depense.getNotes() != null){
						return depense.getNotes();
					}
				}
			}
		}                                                                       
		return null;
	}
}
