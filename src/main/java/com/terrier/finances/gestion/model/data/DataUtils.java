/**
 * 
 */
package com.terrier.finances.gestion.model.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * Utilitaire sur les données
 * @author vzwingma
 *
 */
public class DataUtils {

	
	
	/**
	 * @return la date actuelle en LocalDate
	 */
	public static final LocalDate localDateNow(){
		return Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	
	/**
	 * @return la date localisée au début du mois
	 */
	public static final LocalDate localDateFirstDayOfMonth(){
		LocalDate date = localDateNow();
		return date.with(ChronoField.DAY_OF_MONTH, 1);
	}
	
	/**
	 * @param month
	 * @return la date localisée en début du mois, au mois positionnée
	 */
	public static final LocalDate localDateFirstDayOfMonth(Month month){
		LocalDate date = localDateNow();
		return date
				.with(ChronoField.DAY_OF_MONTH, 1)
				.with(ChronoField.MONTH_OF_YEAR, month.getValue());
	}
}
