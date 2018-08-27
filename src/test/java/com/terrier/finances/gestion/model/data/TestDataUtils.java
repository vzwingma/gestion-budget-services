package com.terrier.finances.gestion.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;

/**
 * @author vzwingma
 *
 */
public class TestDataUtils {

	
	@Test
	public void testDates(){
		
		LocalDate now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dataUtilsNow = DataUtils.localDateNow();
		
		assertEquals(now, dataUtilsNow);
		
		assertEquals(1, DataUtils.localDateFirstDayOfMonth().getDayOfMonth());
		assertEquals(now.getMonth(), DataUtils.localDateFirstDayOfMonth().getMonth());
	}
	
	
	@Test
	public void testMaxDateOperations(){
		
		Calendar c = Calendar.getInstance();
		LigneDepense depense1 = new LigneDepense(true);
		depense1.setDateOperation(c.getTime());
		LigneDepense depense2 = new LigneDepense(true);
		c.set(Calendar.DAY_OF_MONTH, 31);
		depense2.setDateOperation(c.getTime());
		
		LigneDepense depense3 = new LigneDepense(true);
		Calendar c3 = Calendar.getInstance();
		c3.set(Calendar.DAY_OF_MONTH, 12);
		c3.set(Calendar.MONTH, Calendar.OCTOBER);
		c3.set(Calendar.YEAR, 2050);
		depense3.setDateOperation(c3.getTime());
		
		List<LigneDepense> depenses = new ArrayList<>();
		depenses.addAll(Arrays.asList(depense1, depense2, depense3));
		LocalDate cd = DataUtils.getMaxDateListeOperations(depenses);
		
		assertEquals(Month.OCTOBER.getValue(), cd.get(ChronoField.MONTH_OF_YEAR));
	}
	
	
	@Test
	public void testDoubleFromString(){
		assertNull(DataUtils.getValueFromString(null));
		assertNull(DataUtils.getValueFromString("123/0"));
		assertEquals("123.3", DataUtils.getValueFromString("123.3"));
		assertEquals("123.3", DataUtils.getValueFromString("123,3"));
		assertEquals("-123.3", DataUtils.getValueFromString("-123,3"));
	}
}
