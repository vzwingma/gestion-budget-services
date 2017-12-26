/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;

import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.CellStyleGenerator;


/**
 * Style des cellules du tableau des catégories
 * @author vzwingma
 *
 */
public class TreeResumeCellStyle implements CellStyleGenerator<String> {

	/**
	 * Logger
	 */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TreeResumeCellStyle.class);


	/* (non-Javadoc)
	 * @see com.vaadin.client.widget.grid.CellStyleGenerator#getStyle(com.vaadin.client.widget.grid.CellReference)
	 */
	@Override
	public String getStyle(CellReference<String> cellReference) {
		String styles = "";
		if(cellReference.getElement().getPropertyInt("Code") < 100){
			styles = "tree-ss-categorie";
		}
		
		// Coloration suivant la valeur du total de la ss catégorie
		if(cellReference.getColumn().getHeaderCaption().equals(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle())
			||
			cellReference.getColumn().getHeaderCaption().equals(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle())){
			
			if((Double)cellReference.getValue() >= 0){
				styles += " v-table-cell-content-valeur";
			}
			else{
				styles += " v-table-cell-content-valeur_rouge";
			}
		}
		return styles;
	}
}
