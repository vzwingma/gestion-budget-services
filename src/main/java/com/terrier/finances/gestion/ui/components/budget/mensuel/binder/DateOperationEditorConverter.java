package com.terrier.finances.gestion.ui.components.budget.mensuel.binder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.terrier.finances.gestion.model.data.DataUtils;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Converteur Modèle / Présentation pour les dates (en mode édition)
 * 
 * @author vzwingma
 *
 */
public class DateOperationEditorConverter implements Converter<String, Date> {

	//
	private static final long serialVersionUID = -3920598435421890807L;

	// Format des dates
	private SimpleDateFormat sfd = new SimpleDateFormat(DataUtils.DATE_DAY_HOUR_PATTERN);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object,
	 * com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<Date> convertToModel(String value, ValueContext context) {
		try {
			return Result.ok(sfd.parse(value));
		} catch (ParseException e) {
			return Result.error("Erreur : La date " + value + " n'est pas au format " + DataUtils.DATE_DAY_HOUR_PATTERN);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object,
	 * com.vaadin.data.ValueContext)
	 */
	@Override
	public String convertToPresentation(Date value, ValueContext context) {
		return sfd.format(value);
	}

}
