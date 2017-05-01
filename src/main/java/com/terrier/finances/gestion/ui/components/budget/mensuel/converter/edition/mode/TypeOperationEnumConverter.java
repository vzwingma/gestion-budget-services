package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import java.util.Locale;

import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.v7.data.util.converter.Converter;

/**
 * Convertisseur Modèle <==> Présentation de l'énum type TypeDepenseEnum
 * @author vzwingma
 *
 */
public class TypeOperationEnumConverter implements Converter<Object, TypeDepenseEnum>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3943268078483103639L;

	@Override
	public TypeDepenseEnum convertToModel(Object value,
			Class<? extends TypeDepenseEnum> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		for (TypeDepenseEnum type : TypeDepenseEnum.values()) {
			if(type.getLibelle().equals((String)value)){
				return type;
			}
		}
		return null;
	}

	@Override
	public Object convertToPresentation(TypeDepenseEnum value,
			Class<? extends Object> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		return value != null ? value.getLibelle() : null;
	}

	@Override
	public Class<TypeDepenseEnum> getModelType() {
		return TypeDepenseEnum.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}
	
	

}
