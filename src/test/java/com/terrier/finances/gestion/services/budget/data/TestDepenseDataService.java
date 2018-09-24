/**
 * 
 */
package com.terrier.finances.gestion.services.budget.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author vzwingma
 *
 */
public class TestDepenseDataService {


	@Test
	public void testCollectionName(){
		BudgetDatabaseService service = new BudgetDatabaseService();
		assertEquals("budget_2015", service.getBudgetCollectionName(2015));
	}


	@Test
	public void testCollectionNameByIdBudget() throws Exception{

		BudgetDatabaseService service = new BudgetDatabaseService();
		String annee = service.getBudgetCollectionName("ingdirect_2016_2");
		assertNotNull(annee);
		assertEquals("budget_2016", annee);
	}

}
