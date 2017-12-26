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
public class TableTotalCellStyle implements CellStyleGenerator<String> {


	/**
	 * Logger
	 */
	//private static final Logger LOGGER = LoggerFactory.getLogger(TreeResumeCellStyle.class);



	@Override
	public String getStyle(CellReference<String> cellReference) {
		String idProperty = (String)cellReference.getRow();
		if(idProperty == null){
			return null;
		}
		if(idProperty != null && 
				(cellReference.getColumn().getHeaderCaption().equals(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId())
						||
						cellReference.getColumn().getHeaderCaption().equals(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()))){


			if(cellReference.getValue() == null){
				return "";
			}
			Double valeur = (Double)cellReference.getValue();
			if(valeur >= 0){
				return "valeur v-table-cell-content-totaux";
			}
			else{
				return "valeur_rouge v-table-cell-content-totaux";
			}

		}
		return null;
	}

}
