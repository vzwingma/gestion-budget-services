package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import java.util.Collection;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.vaadin.v7.data.util.converter.Converter;

/**
 * Convertisseur Modèle <==> Présentation de la catégorie de dépense
 * @author vzwingma
 *
 */
public class CategorieConverter implements Converter<String, CategorieDepense> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9189652161676775908L;
	// Catégories
	private Collection<CategorieDepense> categories;
	
	
	/**
	 * Init de catégories
	 * @param categories liste des catégories
	 */
	public CategorieConverter(Collection<CategorieDepense> categories){
		this.categories = categories;
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public CategorieDepense convertToModel(String value,
			Class<? extends CategorieDepense> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		for (CategorieDepense categorie : this.categories) {
			if(categorie.getLibelle().equals(value)){
				return categorie;
			}
			for (CategorieDepense ssCategorie : categorie.getListeSSCategories()) {
				if(ssCategorie.getLibelle().equals(value)){
					return ssCategorie;
				}
			}
		}
		return null;
	}

	@Override
	public String convertToPresentation(CategorieDepense value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		return value != null ? value.getLibelle() : "INCONNU";
	}

	@Override
	public Class<CategorieDepense> getModelType() {
		return CategorieDepense.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
