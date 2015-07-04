package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import java.util.Collection;
import java.util.Date;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.data.Container;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

/**
 * Field Factory pour le mode edition
 * @author vzwingma
 *
 */
public class TableSuiviDepenseEditedFieldFactory extends DefaultFieldFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8561840459023324017L;

	private Collection<CategorieDepense> categories;

	/**
	 * Constructeur
	 * @param categories catégories de dépenses
	 */
	public TableSuiviDepenseEditedFieldFactory(Collection<CategorieDepense> categories){
		this.categories = categories;
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Field createField(Container container, Object itemId,
			Object propertyId, Component uiContext) {
		// Create fields by their class
		Class<?> cls = container.getType(propertyId);
		Field editorField;
		// Create a DateField with year resolution for dates
		if(propertyId.equals(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId())){
			editorField = new TextField();
			editorField.setEnabled(false);
		}		
		else if (cls.equals(Date.class)) {
			editorField = new DateField();
		}
		else if(propertyId.equals(EntetesTableSuiviDepenseEnum.AUTEUR.getId())){
			editorField = new TextField();
			editorField.setEnabled(false);
		}		
		// Create a CheckBox for Boolean fields
		else if (cls.equals(Boolean.class)){
			editorField = new CheckBox();
		}
		// Converter spécial pour l'édition d'un enum
		else if (cls.equals(EtatLigneDepenseEnum.class)){
			TextField tf = new TextField();
			tf.setConverter(new EtatLigneDepenseEnumConverter());
			editorField = tf;
		}	 
		else if(propertyId.equals(EntetesTableSuiviDepenseEnum.CATEGORIE.getId())){
			TextField tf = new TextField();
			tf.setConverter(new CategorieConverter(categories));
			tf.setEnabled(false);
			editorField = tf;
		}

		else if(propertyId.equals(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId())){
			ListSelect  comboField = new ListSelect();
			comboField.setNullSelectionAllowed(false);
			comboField.setRows(1);
			CategorieDepense categorieLigneLibelle = (CategorieDepense)container.getContainerProperty(itemId, propertyId).getValue();
			for (CategorieDepense categorie : categories) {
				for (CategorieDepense ssCategorie : categorie.getListeSSCategories()) {
					comboField.addItem(ssCategorie);
					comboField.setItemCaption(ssCategorie, ssCategorie.getLibelle());
					// Sélection par défaut
					if(ssCategorie.equals(categorieLigneLibelle)){
						comboField.select(ssCategorie);
					}				
				}
			}
			editorField = comboField;
		}		
		else if(propertyId.equals(EntetesTableSuiviDepenseEnum.TYPE.getId())){
			ListSelect comboField = new ListSelect();
			comboField.setNullSelectionAllowed(false);
			comboField.setMultiSelect(false);
			comboField.setRows(1);
			TypeDepenseEnum typeDepense = (TypeDepenseEnum)container.getContainerProperty(itemId, propertyId).getValue();
			for (TypeDepenseEnum type : TypeDepenseEnum.values()) {
				comboField.addItem(type);
				comboField.setItemCaption(type, type.getLibelle());

				// Sélection par défaut
				if(type.equals(typeDepense)){
					comboField.select(type);
				}				
			}
			
			editorField = comboField;
		}				
		else{
			// Otherwise use the default field factory 
			editorField =  super.createField(container, itemId, propertyId, uiContext);
		}


		editorField.setWidth("100%");
		return editorField;
	}
}
