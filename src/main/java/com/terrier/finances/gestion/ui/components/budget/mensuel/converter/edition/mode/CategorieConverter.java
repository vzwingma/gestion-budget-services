package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import java.util.Collection;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

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


	@Override
	public Result<CategorieDepense> convertToModel(String value, ValueContext context) {
		
//
//		
//		for (CategorieDepense categorie : this.categories) {
//			if(categorie.getLibelle().equals(value)){
//				return categorie;
//			}
//			for (CategorieDepense ssCategorie : categorie.getListeSSCategories()) {
//				if(ssCategorie.getLibelle().equals(value)){
//					return ssCategorie;
//				}
//			}
//		}
		return null;
	}

	@Override
	public String convertToPresentation(CategorieDepense value, ValueContext context) {
		return value != null ? value.getLibelle() : "INCONNU";
	}

}
