package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Convertisseur Modèle <==> Présentation de l'énum etat dépense
 * @author vzwingma
 * @deprecated
 */
@Deprecated
public class EtatLigneDepenseEnumConverter implements Converter<String, EtatLigneDepenseEnum>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3943268078483103639L;

	
	
	/* (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<EtatLigneDepenseEnum> convertToModel(String value, ValueContext context) {
		// return EtatLigneDepenseEnum.valueOf(value);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public String convertToPresentation(EtatLigneDepenseEnum value, ValueContext context) {
		return value.getLibelle();
	}
}
