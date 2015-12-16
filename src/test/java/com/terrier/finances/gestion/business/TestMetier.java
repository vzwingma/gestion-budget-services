package com.terrier.finances.gestion.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

public class TestMetier {

	@Test
	public void testDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm:ss", Locale.FRENCH);
		LOGGER.info(sdf.format(Calendar.getInstance().getTime()));
	}
}
