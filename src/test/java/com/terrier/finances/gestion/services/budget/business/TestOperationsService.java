package com.terrier.finances.gestion.services.budget.business;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatLigneOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieDepense;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;

/**
 * Test opération Service
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMockDBServicesConfig.class})
public class TestOperationsService {

	
	@Autowired
	private OperationsService operationsService;
	
	private BudgetMensuel budget;
	
	
	@Before
	public void initBudget(){
		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.getListeOperations().add(new LigneOperation(new CategorieDepense(), "TEST1", TypeOperationEnum.CREDIT, "123", EtatLigneOperationEnum.PREVUE, false));
	}
	
	
	@Test
	public void testActif(){
		operationsService.setBudgetActif(this.budget, false, "TEST");
	}
}
