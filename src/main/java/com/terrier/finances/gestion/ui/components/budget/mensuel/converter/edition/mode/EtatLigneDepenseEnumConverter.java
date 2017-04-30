package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import java.util.Locale;

import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.vaadin.v7.data.util.converter.Converter;

/**
 * Convertisseur Modèle <==> Présentation de l'énum etat dépense
 * @author vzwingma
 *
 */
public class EtatLigneDepenseEnumConverter implements Converter<String, EtatLigneDepenseEnum>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3943268078483103639L;

	@Override
	public EtatLigneDepenseEnum convertToModel(String value,
			Class<? extends EtatLigneDepenseEnum> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		return EtatLigneDepenseEnum.valueOf(value);
	}

	@Override
	public String convertToPresentation(EtatLigneDepenseEnum value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		return value.getLibelle();
	}

	@Override
	public Class<EtatLigneDepenseEnum> getModelType() {
		return EtatLigneDepenseEnum.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
