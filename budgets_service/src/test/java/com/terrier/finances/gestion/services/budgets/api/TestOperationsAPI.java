package com.terrier.finances.gestion.services.budgets.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient;
import com.terrier.finances.gestion.services.budgets.api.client.ParametragesAPIClient;
import com.terrier.finances.gestion.services.budgets.business.OperationsService;
import com.terrier.finances.gestion.services.budgets.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budgets.model.v12.BudgetMensuelDTO;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockBudgetServiceConfig;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;

/**
 * Test des opérations
 * @author vzwingma
 *
 */
@ExtendWith({SpringExtension.class })
@ContextConfiguration(classes={TestMockBudgetServiceConfig.class, TestMockDBServicesConfig.class, TestOperationsAPI.class, OperationsAPIController.class, OperationsService.class})
public class TestOperationsAPI extends AbstractTestsAPI {

	@Autowired
	private BudgetDatabaseService mockDataDBBudget;
	
	@Autowired
	private ParametragesAPIClient mockDataAPIParams;

	@Autowired
	private ComptesAPIClient mockDataAPIComptes;

	
	
	private CompteBancaire c1;
	private BudgetMensuel bo;

	@BeforeEach
	public void initBudget () throws DataNotFoundException, BudgetNotFoundException {
		
		MockitoAnnotations.initMocks(this);
		
		// Budget
		c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockDataAPIComptes.getCompteById(eq("C1"), anyString())).thenReturn(c1);

		bo = new BudgetMensuel();
		bo.setCompteBancaire(c1);
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(true);
		bo.setId("C1_2018_1");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		bo.setDateMiseAJour(Calendar.getInstance());
		bo.setResultatMoisPrecedent(0D);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(eq(bo))).thenReturn(bo.getId());

		when(mockDataDBBudget.chargeBudgetMensuel(eq(c1), eq(Month.JANUARY), eq(2018))).thenReturn(bo);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("Mock"));
	}

	@Test
	public void testGetBudgetQuery() throws Exception {
		// Fail
		String urlWrongCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018";
		getMockAPI().perform(get(urlWrongCompte))
		.andExpect(status().is4xxClientError());

	}

	@Test
	public void testGetBudgetWrongCompte() throws Exception {

		when(mockDataAPIComptes.getCompteById(eq("unknown"), eq("test"))).thenReturn(null);
		
		String urlWrongCompte =  BudgetApiUrlEnum.BUDGET_QUERY_FULL;
		LOGGER.info("Wrong Compte : {}", urlWrongCompte);
		/** Authentification **/
		authenticateUser("test");
		// Wrong compte
		getMockAPI().perform(get(urlWrongCompte, "unkown", "1", "2018"))
		.andExpect(status().is4xxClientError());
	}



	@Test
	public void testGetBudgetOK() throws Exception {

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL+"?idCompte=C1&mois=1&annee=2018";
		
		
		LOGGER.info("Good Compte : {}", urlGoodCompte);
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(get(urlGoodCompte, "C1", 1, 2018))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("{\"id\":\""+bo.getId()+"\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":true")));
	}



	@Test
	public void testIntervalles() throws Exception {
		String path = BudgetApiUrlEnum.BUDGET_COMPTE_INTERVALLES_FULL.replace("{idCompte}", "TEST");
	
		BudgetMensuelDTO debut = new BudgetMensuelDTO();
		debut.setAnnee(2018);
		debut.setMois(Month.JANUARY);
		
		BudgetMensuelDTO fin = new BudgetMensuelDTO();
		fin.setAnnee(2018);
		fin.setMois(Month.FEBRUARY);
		/** Authentification **/
		authenticateUser("123123");

		when(mockDataDBBudget.getPremierDernierBudgets(anyString())).thenReturn(new BudgetMensuelDTO[]{ debut, fin});
		getMockAPI().perform(get(path))
			.andExpect(status().isOk())
			.andExpect(content().string("{\"datePremierBudget\":17563,\"dateDernierBudget\":17622}"));
	}
	
	


	@Test
	public void testReinitbudget() throws Exception {

		BudgetMensuelDTO budget = new BudgetMensuelDTO();
		budget.setIdCompteBancaire(c1.getId());
		budget.setMois(Month.JANUARY);
		budget.setAnnee(2018);
		budget.setActif(false);
		budget.setId();
		when(mockDataDBBudget.chargeBudgetMensuelDTO(any())).thenReturn(budget);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018))).thenReturn(bo);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(any())).thenReturn(bo.getId());

		// OK

		String url = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", "C1_2018_1");
		LOGGER.info("Reinit budget: {}", url);
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(delete(url))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("\"newBudget\":true")));
	}



	/**
	 * Test buget
	 * @throws Exception
	 */
	@Test
	public void testBudgetActif() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(get(urlActif))
					.andExpect(status().is4xxClientError());

		when(mockDataDBBudget.isBudgetActif(eq("TESTKO"))).thenReturn(Boolean.FALSE);
		when(mockDataDBBudget.isBudgetActif(eq("TESTOK"))).thenReturn(Boolean.TRUE);

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
					.andExpect(status().is(423));

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif))
					.andExpect(status().isOk());
	}


	/**
	 * Test budget
	 * @throws Exception
	 */
	@Test
	public void testIsBudgetUptodate() throws Exception{
		/** Authentification **/
		authenticateUser("userTest");
		
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		getMockAPI().perform(get(urlActif))
					.andExpect(status().is4xxClientError());

		Calendar futur = Calendar.getInstance();
		futur.add(Calendar.HOUR_OF_DAY, 1);

		Calendar passe = Calendar.getInstance();
		passe.add(Calendar.HOUR_OF_DAY, -1);

		BudgetMensuel ko = new BudgetMensuel();
		ko.setDateMiseAJour(futur);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTKO"))).thenReturn(ko);
		BudgetMensuel ok = new BudgetMensuel();
		ok.setDateMiseAJour(passe);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTOK"))).thenReturn(ok);

		urlActif = BudgetApiUrlEnum.BUDGET_UP_TO_DATE_FULL.replace("{idBudget}", "TESTKO") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		/** Authentification **/
		authenticateUser("userTest");

		LOGGER.info("is UptoDate : {}", urlActif);
		getMockAPI().perform(get(urlActif))
					.andExpect(status().is4xxClientError());

		urlActif = BudgetApiUrlEnum.BUDGET_UP_TO_DATE_FULL.replace("{idBudget}", "TESTOK") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		LOGGER.info("is UptoDate : {}", urlActif);
		getMockAPI().perform(get(urlActif))
					.andExpect(status().isOk());
	}





	/**
	 * Test buget
	 * @throws Exception
	 */
	@Test
	public void testLockBudget() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		getMockAPI().perform(post(urlActif))
		.andExpect(status().is4xxClientError());

		/** Authentification **/
		authenticateUser("userTest");


		BudgetMensuel bo = new BudgetMensuel();
		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);		
		bo.setCompteBancaire(c1);
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(false);
		bo.setId("BUDGETTEST");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		bo.setDateMiseAJour(Calendar.getInstance());
		bo.setResultatMoisPrecedent(0D);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTKO"))).thenReturn(bo);
		bo.setActif(true);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTOK"))).thenReturn(bo);

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(post(urlActif))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("\"actif\":true")));

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK") + "?actif=false";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(post(urlActif))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("\"actif\":false")));
	}



	@Test
	public void testGetBudget() throws Exception {

		String urlBadBudget = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", "C3_2018_1");
		LOGGER.info("Bad Budget : {}", urlBadBudget);
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(get(urlBadBudget).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().is4xxClientError());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", bo.getId());
		LOGGER.info("Good Budget : {}", urlGoodCompte);

		getMockAPI().perform(get(urlGoodCompte).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("{\"id\":\""+bo.getId()+"\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":true")));
	}




	@Test
	public void testSetAsDerniereOperation() throws Exception {
		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op1.setDerniereOperation(true);
		bo.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op2.setId("ID_op");
		op2.setDerniereOperation(false);
		bo.getListeOperations().add(op2);

		/** Authentification **/
		authenticateUser("userTest");
		
		String urlBadBudget = BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE_FULL.replace("{idBudget}", bo.getId()+"XXX").replace("{idOperation}", "ID_op");
		LOGGER.info("Bad SetOperation : {}", urlBadBudget);

		getMockAPI().perform(
				post(urlBadBudget)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(bo)))
		.andExpect(status().isNoContent());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "ID_op");
		LOGGER.info("Good SetOperation : {}", urlGoodCompte);

		getMockAPI().perform(
				post(urlGoodCompte)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(bo)))
		.andExpect(status().isOk());
	}




	@Test
	public void testDelOperation() throws Exception {

		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		op1.setId("OP1");
		op1.setDerniereOperation(true);
		bo.getListeOperations().add(op1);

		/** Authentification **/
		authenticateUser("userTest");

		
		String urlBadBudget = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "ID_op");
		LOGGER.info("Bad del : {}", urlBadBudget);

		getMockAPI().perform(delete(urlBadBudget).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "OP1");
		LOGGER.info("Good Del : {}", urlGoodCompte);

		getMockAPI().perform(delete(urlGoodCompte).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
	}



	@Test
	public void testUpdateOperation() throws Exception {


		CategorieOperation cat = new CategorieOperation("SCAT_ID");
		CategorieOperation sscat = new CategorieOperation("CAT_ID");
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		op1.setDerniereOperation(true);
		op1.setId("OP1");
		bo.getListeOperations().add(op1);
		bo.setActif(true);

		/** Authentification **/
		authenticateUser("userTest");

		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opupdate.setId("OP1");
		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", opupdate.getId());
		String jsonopupdate = json(opupdate);
		
		LOGGER.info("Good Update {} : {}", urlGoodCompte, jsonopupdate);

		getMockAPI().perform(post(urlGoodCompte).contentType(MediaType.APPLICATION_JSON).content(jsonopupdate))
					.andExpect(status().isOk());
	}
	
	


	@Test
	public void testCreateOperationInterCompte() throws Exception {

		// Budget
		CompteBancaire c2 = new CompteBancaire();
		c2.setActif(true);
		c2.setId("C2");
		c2.setLibelle("C2");
		c2.setOrdre(1);
		when(mockDataAPIComptes.getCompteById(eq("C2"), anyString())).thenReturn(c2);


		BudgetMensuel bo2 = new BudgetMensuel();
		bo2.setCompteBancaire(c2);
		bo2.setMois(Month.JANUARY);
		bo2.setAnnee(2018);
		bo2.setActif(true);
		bo2.setId("C2_2018_1");
		bo2.setSoldeFin(0D);
		bo2.setSoldeNow(1000D);
		bo2.setDateMiseAJour(Calendar.getInstance());
		bo2.setResultatMoisPrecedent(0D);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(eq(bo2))).thenReturn(bo2.getId());
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("C2_2018_1"))).thenReturn(bo2);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("C1_2018_1"))).thenReturn(bo);
		when(mockDataDBBudget.chargeBudgetMensuel(eq(c2), eq(Month.JANUARY), eq(2018))).thenReturn(bo2);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017))).thenThrow(new BudgetNotFoundException("Mock"));

		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.REMBOURSEMENT);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE);
		sscat.setCategorieParente(cat);

		when(mockDataAPIParams.getCategories()).thenReturn(Arrays.asList(cat, sscat));
		when(mockDataAPIParams.getCategorieParId(eq(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE.name()))).thenReturn(sscat);
		
		LigneOperation opIntercompte = new LigneOperation(sscat, "OPInter", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		opIntercompte.setId("OPInter");
		bo.getListeOperations().add(opIntercompte);

		/** Authentification **/
		authenticateUser("userTest");

		
		String urlIntercompte = BudgetApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE_FULL
									.replace("{idBudget}", bo.getId())
									.replace("{idOperation}", opIntercompte.getId())
									.replace("{idCompte}", c2.getId());
		c2.setActif(false);
		LOGGER.info("Bad Intercompte : {}", urlIntercompte);
		getMockAPI().perform(
				post(urlIntercompte)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(opIntercompte)))
			.andExpect(status().is4xxClientError());


		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opupdate.setId("OP1");

		LOGGER.info("Good Intercompte : {}", urlIntercompte);
		c2.setActif(true);
		getMockAPI().perform(
				post(urlIntercompte)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(opupdate)))
		.andExpect(status().isOk());
	}
	
	
	/**
	 * Tests libellés
	 * @throws Exception
	 */
	@Test
	public void testLibelles() throws Exception {
		
		/** Authentification **/
		authenticateUser("123123");

		String path = BudgetApiUrlEnum.BUDGET_COMPTE_OPERATIONS_LIBELLES_FULL.replace("{idCompte}", "TEST") + "?annee=2019";
		getMockAPI().perform(get(path))
					.andExpect(status().isNoContent());
				
		Set<String> libelles = new HashSet<>();
		libelles.add("OPE1");
		libelles.add("OPE2");
		when(mockDataDBBudget.chargeLibellesOperations(eq("TEST"), eq(2019))).thenReturn(libelles);
		
		getMockAPI().perform(get(path))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"idCompte\":\"TEST\",\"libellesOperations\":[\"OPE1\",\"OPE2\"]}"));
	}
}
