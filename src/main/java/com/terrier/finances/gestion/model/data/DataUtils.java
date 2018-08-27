/**
 * 
 */
package com.terrier.finances.gestion.model.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;

/**
 * Utilitaire sur les données
 * @author vzwingma
 *
 */
public class DataUtils {

	public static final String DATE_DAY_PATTERN = "dd/MM/yyyy";
	public static final String DATE_DAY_HOUR_PATTERN = DATE_DAY_PATTERN + " HH:mm";
	public static final String DATE_DAY_HOUR_S_PATTERN = DATE_DAY_HOUR_PATTERN + ":ss";

	public static final String DATE_FULL_TEXT_PATTERN = "dd MMMM yyyy HH:mm";


	private DataUtils(){
		// Constructeur privé pour classe utilitaire
	}

	public static final TimeZone getTzParis(){
		return TimeZone.getTimeZone("Europe/Paris");
	}

	/**
	 * @param utcTime
	 * @return date transformée en local
	 * @throws ParseException
	 */
	public static final String getUtcToLocalTime(String utcTime) throws ParseException{
		SimpleDateFormat sdfutc = new SimpleDateFormat(DATE_DAY_HOUR_PATTERN, Locale.FRENCH);
		sdfutc.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date dateBuild = sdfutc.parse(utcTime);
		SimpleDateFormat sdflocale = new SimpleDateFormat(DATE_DAY_HOUR_PATTERN, Locale.FRENCH);
		sdflocale.setTimeZone(DataUtils.getTzParis());
		return sdflocale.format(dateBuild);
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


	/**
	 * @param valeurS
	 * @return la valeur d'un String en double
	 */
	public static String getValueFromString(String valeurS){

		if(valeurS != null){
			valeurS = valeurS.replaceAll(",", ".");
			try{
				valeurS = Double.toString(Double.valueOf(valeurS));
			}
			catch(Exception e){
				valeurS = null;
			}
		}
		return valeurS;
	}
}
