package com.terrier.finances.gestion.services.budget.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatLigneOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieDepense;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;
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
	private TestMockDBServicesConfig mocksConfig;
	
	
	@Autowired
	private OperationsService operationsService;
	


	private BudgetMensuel budget;
	
	
	/**
	 * Surcharge de l'authservice
	 */
	@Before
	public void initBusinessSession(){
		UserBusinessSession mockUser = Mockito.mock(UserBusinessSession.class);
		when(mockUser.getEncryptor()).thenReturn(new BasicTextEncryptor());
		when(mocksConfig.getMockAuthService().getBusinessSession(anyString())).thenReturn(mockUser);
		this.operationsService.setServiceUtilisateurs(mocksConfig.getMockAuthService());
	}
	
	@Before
	public void initBudget(){
		this.budget = new BudgetMensuel();
		this.budget.setActif(true);
		this.budget.getListeOperations().add(new LigneOperation(new CategorieDepense(), "TEST1", TypeOperationEnum.CREDIT, "123", EtatLigneOperationEnum.PREVUE, false));
		this.budget.setResultatMoisPrecedent(1234D);
		this.budget.setMargeSecurite(0D);
		
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
	public void testSetBudgetInactif(){
		BudgetMensuel m = operationsService.setBudgetActif(this.budget, false, "TEST");
		assertEquals(EtatLigneOperationEnum.ANNULEE, m.getListeOperations().get(0).getEtat());
	}	
}
