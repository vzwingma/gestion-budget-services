package com.terrier.finances.gestion.services.budget.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

/**
 * Test des opérations
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestOperationsAPI extends AbstractTestsAPI {

	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;
	@Autowired
	private BudgetDatabaseService mockDataDBBudget;

	@Autowired
	private UtilisateursService serviceUser;

	@Test
	public void testGetBudget() throws Exception {
		// Fail
		String urlWrongCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018&idUtilisateur=unknown";
		getMockAPI().perform(
				get(urlWrongCompte))
		.andExpect(status().is4xxClientError());

	}

	@Test
	public void testGetBudgetWrongCompte() throws Exception {

		when(mockDataDBUsers.chargeCompteParId(eq("unknown"), eq("test"))).thenThrow(new DataNotFoundException("Compte introuvable"));

		String urlWrongCompte =  BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018&idUtilisateur=test";
		LOGGER.info("Wrong Compte : {}", urlWrongCompte);
		// Wrong compte
		getMockAPI().perform(
				get(urlWrongCompte))
		.andExpect(status().is4xxClientError());
	}



	@Test
	public void testGetBudgetOK() throws Exception {

		// Budget
		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockDataDBUsers.chargeCompteParId(eq("compteTest"), eq("userTest"))).thenReturn(c1);

		BudgetMensuelDTO budget = new BudgetMensuelDTO();
		budget.setCompteBancaire(c1);
		budget.setMois(Month.JANUARY.getValue());
		budget.setAnnee(2018);
		budget.setActif(false);
		budget.setId();
		when(mockDataDBBudget.chargeBudgetMensuelDTO(any())).thenReturn(budget);
		BudgetMensuel bo = new BudgetMensuel();
		bo.setCompteBancaire(c1);
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(false);
		bo.setId("BUDGETTEST");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		bo.setDateMiseAJour(Calendar.getInstance());
		bo.setResultatMoisPrecedent(0D, 100D);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018), any())).thenReturn(bo);


		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		user.setLibelle("userTest");
		user.setLogin("userTest");
		serviceUser.registerUserBusinessSession(user, "clear");

		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=compteTest&mois=1&annee=2018&idUtilisateur=userTest";
		LOGGER.info("Good Compte : {}", urlGoodCompte);

		getMockAPI().perform(
				get(urlGoodCompte))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("{\"id\":\"BUDGETTEST\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":false")));
	}


	/**
	 * Test buget
	 * @throws Exception
	 */
	@Test
	public void testBudgetActif() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST").replace("{idUtilisateur}", "userTest");
		getMockAPI().perform(get(urlActif))
			.andExpect(status().is4xxClientError());
		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		serviceUser.registerUserBusinessSession(user, "ms");
		
		when(mockDataDBBudget.isBudgetActif(eq("TESTKO"))).thenReturn(Boolean.FALSE);
		when(mockDataDBBudget.isBudgetActif(eq("TESTOK"))).thenReturn(Boolean.TRUE);
		
		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO").replace("{idUtilisateur}", "userTest") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
			.andExpect(status().isNoContent());
		
		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK").replace("{idUtilisateur}", "userTest") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
			.andExpect(status().isOk());
	}
	

	/**
	 * Test buget
	 * @throws Exception
	 */
	@Test
	public void testIsBudgetUptodate() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST").replace("{idUtilisateur}", "userTest");
		getMockAPI().perform(get(urlActif))
			.andExpect(status().is4xxClientError());

		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		serviceUser.registerUserBusinessSession(user, "ms");
		when(mockDataDBBudget.getDateMiseAJourBudget(eq("TESTKO"), any())).thenReturn(new Date());
		when(mockDataDBBudget.getDateMiseAJourBudget(eq("TESTOK"), any())).thenReturn(new Date(0));
		
		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO").replace("{idUtilisateur}", "userTest") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
			.andExpect(status().isNoContent());
		
		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK").replace("{idUtilisateur}", "userTest") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
			.andExpect(status().isOk());
	}
}
