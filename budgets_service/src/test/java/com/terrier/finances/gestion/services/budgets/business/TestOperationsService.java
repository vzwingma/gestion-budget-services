package com.terrier.finances.gestion.services.budgets.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.api.filters.OutcomingRequestFilter;
import com.terrier.finances.gestion.communs.budget.model.BudgetMensuelUtils;
import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient;
import com.terrier.finances.gestion.services.budgets.data.BudgetDatabaseService;
import com.terrier.finances.gestion.test.config.TestMockBudgetServiceConfig;

/**
 * Test opération Service
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={TestMockBudgetServiceConfig.class,  OperationsService.class, OutcomingRequestFilter.class})
public class TestOperationsService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOperationsService.class);

	@Autowired
	private BudgetDatabaseService mockDBBudget;

	@Autowired
	private ComptesAPIClient mockCompteClientApi;
	
	@Autowired
	private OperationsService operationsService;

	private BudgetMensuel budget;

	private CompteBancaire compte = new CompteBancaire();
	
	/**
	 * Surcharge de l'authservice
	 * @throws DataNotFoundException 
	 */
	@BeforeEach
	public void initBusinessSession() throws DataNotFoundException{


		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.getSoldes().setFinMoisPrecedent(0D);
		this.budget.setListeOperations(new ArrayList<>());
		BudgetMensuelUtils.razCalculs(this.budget);
		CategorieOperation dep = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		dep.setCategorieParente(cat);
		
		LigneOperation test1 = new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, "123", EtatOperationEnum.PREVUE, false);
		test1.setId("TEST1");
		this.budget.getListeOperations().add(test1);

		this.budget.setMois(Month.JANUARY);
		this.budget.setAnnee(2018);
		compte = new CompteBancaire();
		compte.setActif(true);
		compte.setId("CID");
		compte.setLibelle("TEST COMPTE");
		compte.setOrdre(0);
	
		this.budget.setIdCompteBancaire(compte.getId());

		this.budget.setId(BudgetDataUtils.getBudgetId(compte.getId(), Month.JANUARY, 2018));
		
	
	}


	/**
	 * Test #119
	 */
	@Test
	public void testSetBudgetInactif() throws UserNotAuthorizedException, BudgetNotFoundException{
		when(mockDBBudget.chargeBudgetMensuel(anyString())).thenReturn(this.budget);
		try {
			BudgetMensuel m = operationsService.setBudgetActif(this.budget.getId(), false, "test");
			assertEquals(EtatOperationEnum.REPORTEE, m.getListeOperations().get(0).getEtat());
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors du calcul", e);
			fail("Erreur lors du calcul");
		}

	}	

	/**
	 * Test #121
	 */
	@Test
	public void testCalculBudget(){
		assertNotNull(this.operationsService);
		assertNotNull(this.budget);
		this.operationsService.calculBudget(this.budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldes().getMaintenant()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldes().getFinMoisCourant()).intValue());

		this.budget.getSoldes().setFinMoisPrecedent(0D);
		this.operationsService.calculBudget(budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldes().getMaintenant()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldes().getFinMoisCourant()).intValue());
	}

	@Test
	public void testSetLastOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException {

		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		when(mockCompteClientApi.getCompteById(anyString(), eq("userTest"))).thenReturn(new CompteBancaire());
		when(mockDBBudget.sauvegardeBudgetMensuel(any())).thenReturn("OK");
		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op1.setTagDerniereOperation(true);
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op2.setId("ID_op");
		op2.setTagDerniereOperation(false);
		budget.getListeOperations().add(op2);

		assertTrue(operationsService.setLigneAsDerniereOperation(this.budget.getId(), "ID_op", "userTest"));

		verify(mockDBBudget, atLeastOnce()).sauvegardeBudgetMensuel(any(BudgetMensuel.class));
	}




	/**
	 * Update opération
	 * @throws UserNotAuthorizedException
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 * @throws CompteClosedException
	 */
	@Test
	public void testDelOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException {
		LOGGER.info("testDelOperation");
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		when(mockCompteClientApi.getCompteById(anyString(), eq("userTest"))).thenReturn(new CompteBancaire());
		
		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);
		BudgetMensuel budgetDel = operationsService.deleteOperation(this.budget.getId(), "TEST1", "userTest");
		assertEquals(0, budgetDel.getListeOperations().size());
		
		LOGGER.info("/testDelOperation");

	}

	/**
	 * Update opération
	 * @throws UserNotAuthorizedException
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 * @throws CompteClosedException
	 */
	@Test
	public void testCRUDOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException {
		LOGGER.info("testCRUDOperation");
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);
		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op1.setTagDerniereOperation(true);
		op1.setId("OP1");
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		op2.setId("ID_op");
		op2.setTagDerniereOperation(false);
		op2.setId("OP2");
		budget.getListeOperations().add(op2);

		LOGGER.info("testCRUDOperation - Add new Ope");
		LigneOperation opNew = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		opNew.setId("OP3");

		BudgetMensuel budgetOP3 = operationsService.updateOperationInBudget(this.budget.getId(), opNew, "userTest");
		assertEquals(4, budgetOP3.getListeOperations().size());

		LOGGER.info("testCRUDOperation - Delete existing Ope");
		LigneOperation opDel = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", null, false);
		opDel.setId("OP1");

		BudgetMensuel budgetDel = operationsService.updateOperationInBudget(this.budget.getId(), opDel, "userTest");
		assertEquals(3, budgetDel.getListeOperations().size());
		
		
		assertEquals(213, budgetDel.getSoldes().getFinMoisCourant());
		assertEquals(0, budgetDel.getSoldes().getMaintenant());
		
		LOGGER.info("testCRUDOperation - Update Ope");
		LigneOperation opUpdate = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opUpdate.setId("OP3");
		BudgetMensuel budgetUpdate = operationsService.updateOperationInBudget(this.budget.getId(), opUpdate, "userTest");
		assertEquals(3, budgetUpdate.getListeOperations().size());
		assertEquals(426, budgetUpdate.getSoldes().getFinMoisCourant());
		assertEquals(213, budgetUpdate.getSoldes().getMaintenant());
		
		LOGGER.info("/testCRUDOperation");

	}
}
