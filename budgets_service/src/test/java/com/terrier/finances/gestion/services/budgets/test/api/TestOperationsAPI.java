package com.terrier.finances.gestion.services.budgets.test.api;

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

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.services.budgets.business.ports.IComptesServiceProvider;
import com.terrier.finances.gestion.services.budgets.business.ports.IOperationsRequest;
import com.terrier.finances.gestion.services.budgets.business.ports.IParametragesServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test des opérations
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={MockServiceBudget.class})
class TestOperationsAPI extends AbstractTestsAPI {

	@Autowired
	private IOperationsRequest mockDataDBBudget;
	
	@Autowired
	private IParametragesServiceProvider mockDataAPIParams;

	@Autowired
	private IComptesServiceProvider mockDataAPIComptes;

	
	
	private CompteBancaire c1;
	private BudgetMensuel bo;

	@BeforeEach
	public void initBudget () throws DataNotFoundException, BudgetNotFoundException, CompteClosedException {
		
		MockitoAnnotations.initMocks(this);
		
		// Budget
		c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockDataAPIComptes.getCompteById(eq("C1"))).thenReturn(c1);

		bo = new BudgetMensuel();
		bo.setIdCompteBancaire(c1.getId());
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(true);
		bo.setId("C1_2018_1");
		bo.getSoldes().setSoldeAtFinMoisCourant(0D);
		bo.getSoldes().setSoldeAtMaintenant(1000D);
		bo.setDateMiseAJour(LocalDateTime.now());
		bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		when(mockDataDBBudget.updateOperationInBudget(anyString(), any(), anyString())).thenReturn(bo);

		when(mockDataDBBudget.getBudgetMensuel(eq(c1.getId()), eq(Month.JANUARY), eq(2018), anyString())).thenReturn(bo);
		when(mockDataDBBudget.getBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017), anyString())).thenThrow(new BudgetNotFoundException("Mock"));
	}

	@Test
	void testGetBudgetQuery() throws Exception {
		// Fail
		String urlWrongCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018";
		getMockAPI().perform(get(urlWrongCompte))
		.andExpect(status().is4xxClientError());

	}

	@Test
	void testGetBudgetWrongCompte() throws Exception {

		when(mockDataAPIComptes.getCompteById(eq("unknown"))).thenReturn(null);
		
		String urlWrongCompte =  BudgetApiUrlEnum.BUDGET_QUERY_FULL;
		LOGGER.info("Wrong Compte : {}", urlWrongCompte);
		/** Authentification **/
		authenticateUser("test");
		// Wrong compte
		getMockAPI().perform(get(urlWrongCompte, "unkown", "1", "2018"))
		.andExpect(status().is4xxClientError());
	}



	@Test
	void testGetBudgetOK() throws Exception {

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL+"?idCompte=C1&mois=1&annee=2018";
		
		
		LOGGER.info("Good Compte : {}", urlGoodCompte);
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(get(urlGoodCompte, "C1", 1, 2018))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("{\"id\":\""+bo.getId()+"\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":true")));
	}



	@Test
	void testIntervalles() throws Exception {
		String path = BudgetApiUrlEnum.BUDGET_COMPTE_INTERVALLES_FULL.replace("{idCompte}", "TEST");
	
		BudgetMensuel debut = new BudgetMensuel();
		debut.setAnnee(2018);
		debut.setMois(Month.JANUARY);
		
		BudgetMensuel fin = new BudgetMensuel();
		fin.setAnnee(2018);
		fin.setMois(Month.FEBRUARY);
		/** Authentification **/
		authenticateUser("123123");

	//	when(mockDataDBBudget.getIntervallesBudgets(anyString())).thenReturn(new BudgetMensuel[]{ debut, fin});
		getMockAPI().perform(get(path))
			.andExpect(status().isOk())
			.andExpect(content().string("{\"datePremierBudget\":17532,\"dateDernierBudget\":17563}"));
	}
	
	


	@Test
	void testReinitbudget() throws Exception {

		BudgetMensuel budget = new BudgetMensuel();
		budget.setIdCompteBancaire(c1.getId());
		budget.setMois(Month.JANUARY);
		budget.setAnnee(2018);
		budget.setActif(false);
		budget.setId();
		when(mockDataDBBudget.reinitialiserBudgetMensuel(anyString(),anyString())).thenReturn(budget);

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
	void testBudgetActif() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		/** Authentification **/
		authenticateUser("userTest");

		getMockAPI().perform(get(urlActif))
					.andExpect(status().is4xxClientError());

		when(mockDataDBBudget.isBudgetMensuelActif(eq("TESTKO"))).thenReturn(Boolean.FALSE);
		when(mockDataDBBudget.isBudgetMensuelActif(eq("TESTOK"))).thenReturn(Boolean.TRUE);

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
	void testIsBudgetUptodate() throws Exception{
		/** Authentification **/
		authenticateUser("userTest");
		
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		getMockAPI().perform(get(urlActif))
					.andExpect(status().is4xxClientError());

		LocalDateTime futur = LocalDateTime.now().plus(5, ChronoUnit.HOURS);
		BudgetMensuel ko = new BudgetMensuel();
		ko.setDateMiseAJour(futur);
		when(mockDataDBBudget.getBudgetMensuel(eq("TESTKO"), anyString())).thenReturn(ko);

		urlActif = BudgetApiUrlEnum.BUDGET_UP_TO_DATE_FULL.replace("{idBudget}", "TESTKO") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		/** Authentification **/
		authenticateUser("userTest");

		LocalDateTime passe = LocalDateTime.now().plus(-5, ChronoUnit.HOURS);
		BudgetMensuel ok = new BudgetMensuel();
		ok.setDateMiseAJour(passe);
		when(mockDataDBBudget.getBudgetMensuel(eq("TESTOK"), anyString())).thenReturn(ok);
		
		
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
	void testLockBudget() throws Exception{
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
		bo.setIdCompteBancaire(c1.getId());
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(false);
		bo.setId("BUDGETTEST");
		bo.getSoldes().setSoldeAtFinMoisCourant(0D);
		bo.getSoldes().setSoldeAtMaintenant(1000D);
		bo.setDateMiseAJour(LocalDateTime.now());
		bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		when(mockDataDBBudget.getBudgetMensuel(eq("TESTKO"), anyString())).thenReturn(bo);
		bo.setActif(true);
		when(mockDataDBBudget.getBudgetMensuel(eq("TESTOK"), anyString())).thenReturn(bo);

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
	void testGetBudget() throws Exception {

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
	void testSetAsDerniereOperation() throws Exception {
		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		op1.setTagDerniereOperation(true);
		bo.getListeOperations().add(op1);
		LigneOperation op2 = new LigneOperation(sscat, "OP2", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		op2.setId("ID_op");
		op2.setTagDerniereOperation(false);
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
	void testDelOperation() throws Exception {

		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		op1.setId("OP1");
		op1.setTagDerniereOperation(true);
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
	void testUpdateOperation() throws Exception {


		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.FRAIS_REMBOURSABLES);
		sscat.setCategorieParente(cat);

		LigneOperation op1 = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.PREVUE, false);
		op1.setTagDerniereOperation(true);
		op1.setId("OP1");
		bo.getListeOperations().add(op1);
		bo.setActif(true);

		/** Authentification **/
		authenticateUser("userTest");

		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
		opupdate.setId("OP1");
		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", opupdate.getId());
		String jsonopupdate = json(opupdate);
		
		LOGGER.info("Good Update {} : {}", urlGoodCompte, jsonopupdate);

		getMockAPI().perform(post(urlGoodCompte).contentType(MediaType.APPLICATION_JSON).content(jsonopupdate))
					.andExpect(status().isOk());
	}
	
	


	@Test
	void testCreateOperationInterCompte() throws Exception {

		// Budget
		CompteBancaire c2 = new CompteBancaire();
		c2.setActif(true);
		c2.setId("C2");
		c2.setLibelle("C2");
		c2.setOrdre(1);
		when(mockDataAPIComptes.getCompteById(eq("C2"))).thenReturn(c2);


		BudgetMensuel bo2 = new BudgetMensuel();
		bo2.setIdCompteBancaire(c2.getId());
		bo2.setMois(Month.JANUARY);
		bo2.setAnnee(2018);
		bo2.setActif(true);
		bo2.setId("C2_2018_1");
		bo2.getSoldes().setSoldeAtFinMoisCourant(0D);
		bo2.getSoldes().setSoldeAtMaintenant(1000D);
		bo2.setDateMiseAJour(LocalDateTime.now());
		bo2.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		when(mockDataDBBudget.createOperationIntercompte(anyString(), any(), anyString(), anyString())).thenReturn(bo2);

		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.REMBOURSEMENT);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE);
		sscat.setCategorieParente(cat);

		when(mockDataAPIParams.getCategories()).thenReturn(Arrays.asList(cat, sscat));
		when(mockDataAPIParams.getCategorieParId(eq(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE.name()))).thenReturn(sscat);
		
		LigneOperation opIntercompte = new LigneOperation(sscat, "OPInter", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.PREVUE, false);
		opIntercompte.setId("OPInter");
		bo.getListeOperations().add(opIntercompte);

		/** Authentification **/
		authenticateUser("userTest");

		
		String urlIntercompte = BudgetApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE_FULL
									.replace("{idBudget}", bo.getId())
									.replace("{idOperation}", opIntercompte.getId())
									.replace("{idCompte}", c2.getId());

		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, 213D, EtatOperationEnum.REALISEE, false);
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
	void testLibelles() throws Exception {
		
		/** Authentification **/
		authenticateUser("123123");

		String path = BudgetApiUrlEnum.BUDGET_COMPTE_OPERATIONS_LIBELLES_FULL.replace("{idCompte}", "TEST") + "?annee=2019";
		getMockAPI().perform(get(path))
					.andExpect(status().isNoContent());
				
		Set<String> libelles = new HashSet<>();
		libelles.add("OPE1");
		libelles.add("OPE2");
		when(mockDataDBBudget.getLibellesOperations(eq("TEST"), eq(2019))).thenReturn(libelles);
		
		getMockAPI().perform(get(path))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"idCompte\":\"TEST\",\"libellesOperations\":[\"OPE1\",\"OPE2\"]}"));
	}
}
