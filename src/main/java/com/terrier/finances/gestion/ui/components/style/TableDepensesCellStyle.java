/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;

import java.util.List;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIService;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CellStyleGenerator;

/**
 * Style des cellules du tableau de dépense
 * @author vzwingma
 *
 */
public class TableDepensesCellStyle extends AbstractUIService implements CellStyleGenerator {



	/**
	 * 
	 */
	private static final long serialVersionUID = -5413761571256496486L;
	/**
	 * Logger
	 */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TableDepensesCellStyle.class);


	private TableSuiviDepenseController controleur;

	public TableDepensesCellStyle(TableSuiviDepenseController controleur){
		this.controleur = controleur;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getStyle(Table source, Object idDepense, Object propertyId) {

		StringBuilder style = new StringBuilder();
		BudgetMensuel budgetCourant = getBudgetMensuelCourant();
		if(controleur.getServiceDepense() != null && budgetCourant != null && idDepense != null){
			List<LigneDepense> listeDepenses = budgetCourant.getListeDepenses();

			// Style sur les cellules
			String idProperty = (String)propertyId;
			if(idProperty != null){
				// valeurs : (rouge pour négatif)
				if(propertyId.equals(EntetesTableSuiviDepenseEnum.VALEUR.getId()) && listeDepenses != null){

					for (LigneDepense depense : budgetCourant.getListeDepenses()) {
						if(idDepense.equals(depense.getId()) && TypeDepenseEnum.DEPENSE.equals(depense.getTypeDepense())){
							return "valeur_rouge";
						}
					}
					return "valeur";
				}
				for (EntetesTableSuiviDepenseEnum cellId : EntetesTableSuiviDepenseEnum.values()){
					if(propertyId.equals(cellId.getId())){
						return null;
					}
				}
			}

			// Style de la ligne
			if(listeDepenses != null){
				/*
				 * 
				 *  Style pour les autres lignes
				 *  Sauf pour les dépenses réalisées, et celle réserve
				 */
				int rang = 0;
				for (LigneDepense depense : listeDepenses) {
					if(idDepense.equals(depense.getId()) 
							&& !BusinessDepensesService.ID_SS_CAT_RESERVE.equals(depense.getSsCategorie().getId())
							){
						style = new StringBuilder(depense.getEtat().getId());
						break;
					}
					rang ++;
				}

				// Gestion du style par préférence utilisateur
				boolean oddStyle = getUtilisateurCourant().getPreference(UtilisateurPrefsEnum.PREFS_ODD_STYLE, Boolean.class);
				if(oddStyle){
					style.append(rang%2 == 0 ? "" : "-odd");
				}


				// Style de la dernière opération
				for (LigneDepense depense : listeDepenses) {
					if(idDepense.equals(depense.getId()) && depense.isDerniereOperation()){
						style.append(" last-depense");
						break;
					}
				}
			}
		}
		return style.toString();
	}

}
