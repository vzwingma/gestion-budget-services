package com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode;

import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Convertisseur Modèle <==> Présentation de l'énum type TypeDepenseEnum
 * @author vzwingma
 * @deprecated
 */
@Deprecated
public class TypeOperationEnumConverter implements Converter<String, TypeDepenseEnum>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3943268078483103639L;


	@Override
	public Result<TypeDepenseEnum> convertToModel(final String value, ValueContext context) {
		

		for (TypeDepenseEnum type : TypeDepenseEnum.values()) {
			if(type.getLibelle().equals((String)value)){
				//return new SimpleResultTreeImpl(dtmManager, documentID);
				//return new Simpleres.ok(type);
			}
		}
		return null;
	}

	@Override
	public String convertToPresentation(TypeDepenseEnum value, ValueContext context) {
		return value != null ? value.getLibelle() : null;
	}
}
