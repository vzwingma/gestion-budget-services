/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style.operations;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.vaadin.ui.StyleGenerator;

/**
 * Style des lignes du tableau de dépense
 * @author vzwingma
 *
 */
public class GridOperationStyle implements StyleGenerator<LigneDepense> {


	private static final long serialVersionUID = -6709397765771547573L;
	/**
	 * Logger
	 */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TableDepensesCellStyle.class);


	private Boolean oddStyleLignes;

	public GridOperationStyle(Boolean oddStyleLignes){
		this.oddStyleLignes = oddStyleLignes;
	}


	@Override
	public String apply(LigneDepense depense) {
		StringBuilder style = new StringBuilder();

//		// Style sur les cellules
//		String idProperty = (String)cellReference.getColumn().getHeaderCaption();
//		if(idProperty != null){
//			// valeurs : (rouge pour négatif)
//			if(idProperty.equals(EntetesTableSuiviDepenseEnum.VALEUR.getLibelle())){
//
//				if(TypeDepenseEnum.DEPENSE.equals(depense.getTypeDepense())){
//					return "valeur_rouge";
//				}
//				else{
//					return "valeur";
//				}
//			}
//			for (EntetesTableSuiviDepenseEnum cellId : EntetesTableSuiviDepenseEnum.values()){
//				if(idProperty.equals(cellId.name())){
//					return null;
//				}
//			}
//		}

		// Style de la ligne
		/*
		 * 
		 *  Style pour les autres lignes
		 *  Sauf pour les dépenses réalisées, et celle réserve
		 */
		int rang = 0;
		if(!BusinessDepensesService.ID_SS_CAT_RESERVE.equals(depense.getSsCategorie().getId())){
			style = new StringBuilder(depense.getEtat().getId());
			return style.toString();
		}

		// Gestion du style par préférence utilisateur
		if(oddStyleLignes){
			style.append(rang%2 == 0 ? "" : "-odd");
		}

		// Style de la dernière opération
		if(depense.isDerniereOperation()){
			style.append(" last-depense");
			return style.toString();
		}
		return style.toString();
	}
}
