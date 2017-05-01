/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIService;
import com.vaadin.v7.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;

/**
 * Style des cellules du tableau de dépense
 * @author vzwingma
 *
 */
public class TableDepensesDescriptionGenerator extends AbstractUIService implements ItemDescriptionGenerator {



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

		if(propertyId != null && 
				(((String)propertyId).equals(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId())
						||
						((String)propertyId).equals(EntetesTableSuiviDepenseEnum.LIBELLE.getId()))){

			BudgetMensuel budgetCourant = getBudgetMensuelCourant();
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
