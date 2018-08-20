/**
 * 
 */
package com.terrier.finances.gestion.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author vzwingma
 *
 */
public class TestDepenseDataService {


	@Test
	public void testCollectionName(){
		DepensesDatabaseService service = new DepensesDatabaseService();
		assertEquals("budget_2015", service.getBudgetCollectionName(2015));
	}


	@Test
	public void testCollectionNameByIdBudget() throws Exception{

		DepensesDatabaseService service = new DepensesDatabaseService();
		String annee = service.getBudgetCollectionName("ingdirect_2016_2");
		assertNotNull(annee);
		assertEquals("budget_2016", annee);
	}

}
