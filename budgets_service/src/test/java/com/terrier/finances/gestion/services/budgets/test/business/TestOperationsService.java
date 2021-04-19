package com.terrier.finances.gestion.services.budgets.test.business;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.business.OperationsService;
import com.terrier.finances.gestion.services.budgets.business.ports.IComptesServiceProvider;
import com.terrier.finances.gestion.services.budgets.business.ports.IOperationsRepository;
import com.terrier.finances.gestion.services.budgets.business.ports.IOperationsRequest;
import com.terrier.finances.gestion.services.budgets.business.ports.IParametragesServiceProvider;
import com.terrier.finances.gestion.services.budgets.data.TestDataOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Month;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test opération Service
 * @author vzwingma
 *
 */
class TestOperationsService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOperationsService.class);
	// Préparation
	private IOperationsRepository mockDBBudget;
	private IComptesServiceProvider mockCompteClientApi;
	private IParametragesServiceProvider paramsServiceProvider;

	private IOperationsRequest operationsService;

	private BudgetMensuel budget;
	private CompteBancaire compte = new CompteBancaire();

	@BeforeEach
	public void initMocks(){
		// Préparation
		mockDBBudget = mock(IOperationsRepository.class);
		mockCompteClientApi = mock(IComptesServiceProvider.class);
		paramsServiceProvider = mock(IParametragesServiceProvider.class);
		operationsService = spy(new OperationsService(mockDBBudget, mockCompteClientApi, paramsServiceProvider));

		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		this.budget.setListeOperations(new ArrayList<>());
		BudgetDataUtils.razCalculs(this.budget);
		CategorieOperation dep = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		dep.setCategorieParente(cat);
		
		LigneOperation test1 = new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, 123D, EtatOperationEnum.PREVUE, false);
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
	void testSetBudgetInactif() throws UserNotAuthorizedException, BudgetNotFoundException{
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
	void testCalculBudget(){
		assertNotNull(this.operationsService);
		assertNotNull(this.budget);
		this.operationsService.calculBudget(this.budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldes().getSoldeAtMaintenant()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldes().getSoldeAtFinMoisCourant()).intValue());

		this.budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		this.operationsService.calculBudget(budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldes().getSoldeAtMaintenant()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldes().getSoldeAtFinMoisCourant()).intValue());
	}

	@Test
	void testSetLastOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException {

		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		when(mockCompteClientApi.getCompteById(anyString())).thenReturn(new CompteBancaire());
		when(mockDBBudget.sauvegardeBudgetMensuel(any())).thenReturn("OK");
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		op1.setTagDerniereOperation(true);
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
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
	void testDelOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException {
		LOGGER.info("testDelOperation");
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		when(mockCompteClientApi.getCompteById(anyString())).thenReturn(new CompteBancaire());
		
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);
		BudgetMensuel budgetDel = operationsService.deleteOperation(this.budget.getId(), "TEST1", "userTest");
		assertEquals(0, budgetDel.getListeOperations().size());
		
		LOGGER.info("/testDelOperation");

	}


	@Test
	public void testChargerLignesOperationsForAutocomplete() throws DataNotFoundException{
		// Préparation
		Set<String> resultatmock = TestDataOperations.getBudgetCompteC1().getListeOperations()
										.stream()
										.map(o -> o.getLibelle())
										.collect(Collectors.toSet());
		when(mockDBBudget.chargeLibellesOperations(eq("C1_2018_1"), anyInt())).thenReturn(resultatmock);

		// Lancement
		Set<String> autocompleteLibelles = operationsService.getLibellesOperations("C1_2018_1", 2018);

		// Vérification
		assertNotNull(autocompleteLibelles);
		assertEquals(3, autocompleteLibelles.size());
		autocompleteLibelles.stream().forEach(l -> assertTrue(!l.contains("[")));
	}

	/**
	 * Update opération
	 * @throws UserNotAuthorizedException
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 * @throws CompteClosedException
	 */
	@Test
	void testCRUDOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException, CompteClosedException {
		LOGGER.info("testCRUDOperation");
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));
		when(mockCompteClientApi.getCompteById(anyString())).thenReturn(new CompteBancaire());

		this.budget.getListeOperations().clear();
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);
		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		op1.setTagDerniereOperation(true);
		op1.setId("OP1");
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.PREVUE, false);
		op2.setId("ID_op");
		op2.setTagDerniereOperation(false);
		op2.setId("OP2");
		budget.getListeOperations().add(op2);

		LOGGER.info("testCRUDOperation - Add new Ope");
		LigneOperation opNew = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.PREVUE, false);
		opNew.setId("OP3");

		BudgetMensuel budgetOP3 = operationsService.updateOperationInBudget(this.budget.getId(), opNew, "userTest");
		assertEquals(4, budgetOP3.getListeOperations().size());

		LOGGER.info("testCRUDOperation - Delete existing Ope");
		LigneOperation opDel = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, null, false);
		opDel.setId("OP1");

		BudgetMensuel budgetDel = operationsService.updateOperationInBudget(this.budget.getId(), opDel, "userTest");
		assertEquals(3, budgetDel.getListeOperations().size());
		
		
		assertEquals(426, budgetDel.getSoldes().getSoldeAtFinMoisCourant());
		assertEquals(0, budgetDel.getSoldes().getSoldeAtMaintenant());
		
		LOGGER.info("testCRUDOperation - Update Ope");
		LigneOperation opUpdate = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		opUpdate.setId("OP3");
		BudgetMensuel budgetUpdate = operationsService.updateOperationInBudget(this.budget.getId(), opUpdate, "userTest");
		assertEquals(4, budgetUpdate.getListeOperations().size());
		assertEquals(426, budgetUpdate.getSoldes().getSoldeAtFinMoisCourant());
		assertEquals(213, budgetUpdate.getSoldes().getSoldeAtMaintenant());
		
		LOGGER.info("/testCRUDOperation");

	}
}
