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

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.data.BudgetDatabaseService;
import com.terrier.finances.gestion.test.config.TestMockAuthServicesConfig;
import com.terrier.finances.gestion.test.config.TestMockBudgetServiceConfig;

/**
 * Test opération Service
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={TestMockBudgetServiceConfig.class, TestMockAuthServicesConfig.class})
public class TestOperationsService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOperationsService.class);

	@Autowired
	private BudgetDatabaseService mockDBBudget;

	@Autowired
	private OperationsService operationsService;

//	@Autowired
//	private UtilisateurDatabaseService mockDataDBUsers;

//	@Autowired
//	@Qualifier("mockAuthService")
//	private UtilisateursService authenticationService;

	private BudgetMensuel budget;

	private CompteBancaire compte = new CompteBancaire();
	
	private Utilisateur user;
	
	/**
	 * Surcharge de l'authservice
	 * @throws DataNotFoundException 
	 */
	@BeforeEach
	public void initBusinessSession() throws DataNotFoundException{
//		UserBusinessSession mockUser = Mockito.mock(UserBusinessSession.class);
//		when(mocksAuthConfig.getMockAuthService().getBusinessSession(anyString())).thenReturn(mockUser);
//		this.operationsService.setServiceUtilisateurs(mocksAuthConfig.getMockAuthService());
		user = new Utilisateur();
		user.setId("userTest");

		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.setResultatMoisPrecedent(0D, 0D);
		this.budget.razCalculs();
		CategorieOperation dep = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		dep.setCategorieParente(cat);
		
		LigneOperation test1 = new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, "123", EtatOperationEnum.PREVUE, false);
		test1.setId("TEST1");
		this.budget.getListeOperations().add(test1);

		LocalDate now = LocalDate.now();
		this.budget.setMois(now.getMonth());
		this.budget.setAnnee(now.getYear());
		compte = new CompteBancaire();
		compte.setActif(true);
		compte.setId("CID");
		compte.setLibelle("TEST COMPTE");
		compte.setOrdre(0);

//		when(mockDataDBUsers.chargeCompteParId(anyString(), anyString())).thenReturn(compte);
//		when(operationsService.getServiceComptes().getCompteById(anyString(), anyString())).thenReturn(compte);
		
		this.budget.setCompteBancaire(compte);

		this.budget.setId(BudgetDataUtils.getBudgetId(compte, Month.JANUARY, 2018));
	}


	/**
	 * Test #119
	 */
	@Test
	public void testSetBudgetInactif() throws UserNotAuthorizedException, BudgetNotFoundException{
		when(mockDBBudget.chargeBudgetMensuelById(anyString())).thenReturn(this.budget);
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
		assertEquals(0, Double.valueOf(this.budget.getSoldeNow()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldeFin()).intValue());

		assertEquals(0, Double.valueOf(this.budget.getSoldeReelNow()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldeReelFin()).intValue());


		this.budget.setResultatMoisPrecedent(0D, 100D);
		this.operationsService.calculBudget(budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldeNow()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldeFin()).intValue());

		assertEquals(100, Double.valueOf(this.budget.getSoldeReelNow()).intValue());
		assertEquals(223, Double.valueOf(this.budget.getSoldeReelFin()).intValue());


		CategorieOperation reserveSSCat = new CategorieOperation(IdsCategoriesEnum.RESERVE);
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.SALAIRE);
		reserveSSCat.setCategorieParente(cat);

		LigneOperation reserve = new LigneOperation(reserveSSCat, "TESTRESERVE", TypeOperationEnum.CREDIT, "100", EtatOperationEnum.REALISEE, false);
		this.budget.getListeOperations().add(reserve);
		this.operationsService.calculBudget(budget);
		assertEquals(0, Double.valueOf(this.budget.getSoldeNow()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldeFin()).intValue());

		assertEquals(200, Double.valueOf(this.budget.getSoldeReelNow()).intValue());
		assertEquals(323, Double.valueOf(this.budget.getSoldeReelFin()).intValue());

		// Pour éviter le doublon du recalcul ci dessous
		this.budget.setResultatMoisPrecedent(0D, 0D);

		LigneOperation piocheReserve = new LigneOperation(reserveSSCat, "PIOCHERESERVE", TypeOperationEnum.DEPENSE, "50", EtatOperationEnum.REALISEE, false);
		this.budget.getListeOperations().add(piocheReserve);
		this.operationsService.calculBudget(budget);

		assertEquals(0, Double.valueOf(this.budget.getSoldeNow()).intValue());
		assertEquals(123, Double.valueOf(this.budget.getSoldeFin()).intValue());

		assertEquals(150, Double.valueOf(this.budget.getSoldeReelNow()).intValue());
		assertEquals(273, Double.valueOf(this.budget.getSoldeReelFin()).intValue());		

	}

	@Test
	public void testSetLastOperation() throws UserNotAuthorizedException, DataNotFoundException, BudgetNotFoundException {

		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(this.budget);
		when(mockDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("MOCK"));	
		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op1.setDerniereOperation(true);
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op2.setId("ID_op");
		op2.setDerniereOperation(false);
		budget.getListeOperations().add(op2);

		assertTrue(operationsService.setLigneAsDerniereOperation(this.budget.getId(), "ID_op", user.getId()));

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

		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);
		BudgetMensuel budgetDel = operationsService.deleteOperation(this.budget.getId(), "TEST1", user.getId());
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
		op1.setDerniereOperation(true);
		op1.setId("OP1");
		budget.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		op2.setId("ID_op");
		op2.setDerniereOperation(false);
		op2.setId("OP2");
		budget.getListeOperations().add(op2);

		LOGGER.info("testCRUDOperation - Add new Ope");
		LigneOperation opNew = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		opNew.setId("OP3");

		BudgetMensuel budgetOP3 = operationsService.createOrUpdateOperation(this.budget.getId(), opNew, user.getId());
		assertEquals(4, budgetOP3.getListeOperations().size());

		LOGGER.info("testCRUDOperation - Delete existing Ope");
		LigneOperation opDel = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", null, false);
		opDel.setId("OP1");

		BudgetMensuel budgetDel = operationsService.createOrUpdateOperation(this.budget.getId(), opDel, user.getId());
		assertEquals(3, budgetDel.getListeOperations().size());
		
		
		assertEquals(549, budgetDel.getSoldeFin());
		assertEquals(0, budgetDel.getSoldeNow());
		
		LOGGER.info("testCRUDOperation - Update Ope");
		LigneOperation opUpdate = new LigneOperation(sscat, "OP3", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opUpdate.setId("OP3");
		BudgetMensuel budgetUpdate = operationsService.createOrUpdateOperation(this.budget.getId(), opUpdate, user.getId());
		assertEquals(3, budgetUpdate.getListeOperations().size());
		assertEquals(549, budgetUpdate.getSoldeFin());
		assertEquals(213, budgetUpdate.getSoldeNow());
		
		LOGGER.info("/testCRUDOperation");

	}
}
