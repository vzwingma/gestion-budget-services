/**
 * 
 */
package com.terrier.finances.gestion.model.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * Utilitaire sur les données
 * @author vzwingma
 *
 */
public class DataUtils {

	
	
	public static final LocalDate localDateNow(){
		return Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	
	public static final LocalDate localDateFirstDayOfMonth(){
		LocalDate date = localDateNow();
		return date.with(ChronoField.DAY_OF_MONTH, 1);
	}
	
}
