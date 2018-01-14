package com.terrier.finances.gestion.model.data;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Test;

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
}
