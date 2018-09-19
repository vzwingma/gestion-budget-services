package com.terrier.finances.gestion.services.budget.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;
import com.terrier.finances.gestion.test.config.TestMockAuthServicesConfig;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;

/**
 * Test opération Service
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestMockAuthServicesConfig.class})
public class TestOperationsService {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOperationsService.class);

	@Autowired
	private TestMockAuthServicesConfig mocksAuthConfig;

	@Autowired
	private BudgetDatabaseService mockDBBudget;

	@Autowired
	private OperationsService operationsService;

	@Autowired
	@Qualifier("mockAuthService")
	private UtilisateursService authenticationService;

	private BudgetMensuel budget;


	/**
	 * Surcharge de l'authservice
	 */
	@BeforeEach
	public void initBusinessSession(){
		UserBusinessSession mockUser = Mockito.mock(UserBusinessSession.class);
		when(mockUser.getEncryptor()).thenReturn(new BasicTextEncryptor());
		when(mocksAuthConfig.getMockAuthService().getBusinessSession(anyString())).thenReturn(mockUser);
		this.operationsService.setServiceUtilisateurs(mocksAuthConfig.getMockAuthService());


		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.setResultatMoisPrecedent(0D, 0D);
		this.budget.razCalculs();
		CategorieOperation dep = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
		dep.setCategorieParente(cat);
		this.budget.getListeOperations().add(new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, "123", EtatOperationEnum.PREVUE, false));

		LocalDate now = LocalDate.now();
		this.budget.setMois(now.getMonth());
		this.budget.setAnnee(now.getYear());
		CompteBancaire compte = new CompteBancaire();
		compte.setActif(true);
		compte.setId("C_ID");
		compte.setLibelle("TEST COMPTE");
		compte.setOrdre(0);
		this.budget.setCompteBancaire(compte);
	}


	/**
	 * Test #119
	 */
	@Test
	public void testSetBudgetInactif() throws UserNotAuthorizedException, BudgetNotFoundException{
		when(mockDBBudget.chargeBudgetMensuelById(anyString(), any())).thenReturn(this.budget);
		try {
			BudgetMensuel m = operationsService.setBudgetActif(this.budget.getId(), false, "TEST");
			assertEquals(EtatOperationEnum.ANNULEE, m.getListeOperations().get(0).getEtat());
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors du calcul", e);
			fail();
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
}
