/**
 * 
 */
package com.terrier.finances.gestion.ui.components.style;

import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CellStyleGenerator;

/**
 * Style des cellules du tableau des catégories
 * @author vzwingma
 *
 */
public class TreeResumeCellStyle implements CellStyleGenerator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5413761571256496486L;
	/**
	 * Logger
	 */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TreeResumeCellStyle.class);


	@SuppressWarnings("unchecked")
	/* (non-Javadoc)
	 * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getStyle(Table source, Object niveauCategorie, Object propertyId) {

		String idProperty = (String)propertyId;
		if(propertyId == null){
			return null;
		}
		String styles = "";
		if((Integer)niveauCategorie < 100){
			styles = "tree-ss-categorie";
		}
		
		// Coloration suivant la valeur du total de la ss catégorie
		if(idProperty.equals(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId())
			||
			idProperty.equals(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId())){
			
			Item i = source.getItem(niveauCategorie);
			Property<Double> p = i.getItemProperty(idProperty);
			if(p.getValue() >= 0){
				styles += " v-table-cell-content-valeur";
			}
			else{
				styles += " v-table-cell-content-valeur_rouge";
			}
		}
		return styles;
	}
}
