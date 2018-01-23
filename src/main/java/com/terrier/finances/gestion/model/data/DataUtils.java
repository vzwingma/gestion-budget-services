/**
 * 
 */
package com.terrier.finances.gestion.model.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;

/**
 * Utilitaire sur les données
 * @author vzwingma
 *
 */
public class DataUtils {

	private DataUtils(){
		// Constructeur privé pour classe utilitaire
	}

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

	/**
	 * @param month
	 * @return la date localisée en début du mois, au mois positionnée
	 */
	public static final LocalDate localDateFirstDayOfMonth(Month month, int year){
		LocalDate date = localDateNow();
		return date
				.with(ChronoField.DAY_OF_MONTH, 1)
				.with(ChronoField.MONTH_OF_YEAR, month.getValue())
				.with(ChronoField.YEAR, year);
	}


	/**
	 * @param date
	 * @return localdate
	 */
	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * @param listeOperations
	 * @return date max d'une liste de dépenses
	 */
	public static LocalDate getMaxDateListeOperations(List<LigneDepense> listeOperations){

		LocalDate localDateDerniereOperation = localDateNow();

		if(listeOperations != null && !listeOperations.isEmpty()){
			// Comparaison de date
			Comparator <LigneDepense> comparator = Comparator.comparing(LigneDepense::getDateOperation, (date1, date2) -> {
				if(date1 == null){
					return 1;
				}
				else if(date2 == null){
					return -1;
				}
				else{
					return date1.before(date2) ? -1 : 1;
				}
	        });
			Optional<LigneDepense> maxDate = listeOperations.stream().max(comparator);
			if(maxDate.isPresent() && maxDate.get().getDateOperation() != null){
				localDateDerniereOperation = asLocalDate(maxDate.get().getDateOperation());
			}
		}
		return localDateDerniereOperation;
	}
}
