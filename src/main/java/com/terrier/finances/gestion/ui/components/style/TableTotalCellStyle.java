/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;


import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CellStyleGenerator;

/**
 * Style des cellules du tableau des catégories
 * @author vzwingma
 *
 */
public class TableTotalCellStyle implements CellStyleGenerator {



	/**
	 * 
	 */
	private static final long serialVersionUID = -5413761571256496486L;
	/**
	 * Logger
	 */
	//private static final Logger LOGGER = LoggerFactory.getLogger(TreeResumeCellStyle.class);

	
	

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getStyle(Table source, Object ligneId, Object propertyId) {

		String idProperty = (String)propertyId;
		if(propertyId == null){
			return null;
		}
		if(idProperty != null && 
				(propertyId.equals(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId())
						||
						propertyId.equals(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()))){
		
			
			if(ligneId instanceof String){
				String idLigne = (String)ligneId;
				if(source.getItem(idLigne) == null || source.getItem(idLigne).getItemProperty(propertyId) == null){
					return "";
				}
				Double valeur = (Double)source.getItem(idLigne).getItemProperty(propertyId).getValue();
				if(valeur >= 0){
					return "valeur v-table-cell-content-totaux";
				}
				else{
					return "valeur_rouge v-table-cell-content-totaux";
				}

			}
		}
		return null;
	}

}
