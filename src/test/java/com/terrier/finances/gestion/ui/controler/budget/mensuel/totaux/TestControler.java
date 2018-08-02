package com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux;

import java.time.LocalDate;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.data.DataUtils;

/**
 * Tests des controleurs
 * @author vzwingma
 *
 */
public class TestControler {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestControler.class);
	@Test
	public void testAffichageDate(){
		LocalDate now = DataUtils.localDateNow();
		LOGGER.info("du {} à fin {}", 
				now.format(GridResumeTotauxController.auDateFormat), now.format(GridResumeTotauxController.finDateFormat));
	}
}
