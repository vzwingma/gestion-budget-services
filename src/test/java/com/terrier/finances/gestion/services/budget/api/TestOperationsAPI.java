package com.terrier.finances.gestion.services.budget.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.parametrages.data.ParametragesDatabaseService;
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
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestOperationsAPI extends AbstractTestsAPI {

	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;
	@Autowired
	private BudgetDatabaseService mockDataDBBudget;
	@Autowired
	private ParametragesDatabaseService mockDataDBParams;
	@Autowired
	private UtilisateursService serviceUser;

	private CompteBancaire c1;
	private BudgetMensuel bo;

	@BeforeEach
	public void initBudget () throws DataNotFoundException, BudgetNotFoundException {
		// Budget
		c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockDataDBUsers.chargeCompteParId(eq("C1"), anyString())).thenReturn(c1);

		bo = new BudgetMensuel();
		bo.setCompteBancaire(c1);
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(true);
		bo.setId("C1_2018_1");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		bo.setDateMiseAJour(Calendar.getInstance());
		bo.setResultatMoisPrecedent(0D, 100D);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(eq(bo), any())).thenReturn(bo.getId());
		when(mockDataDBBudget.chargeBudgetMensuel(eq(c1), eq(Month.JANUARY), eq(2018), any())).thenReturn(bo);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017), any())).thenThrow(new BudgetNotFoundException("Mock"));

		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		user.setLibelle("userTest");
		user.setLogin("userTest");
		serviceUser.registerUserBusinessSession(user, "clear");

	}

	@Test
	public void testGetBudgetQuery() throws Exception {
		// Fail
		String urlWrongCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018";
		getMockAPI().perform(
				get(urlWrongCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("unknown")))
		.andExpect(status().is4xxClientError());

	}

	@Test
	public void testGetBudgetWrongCompte() throws Exception {

		when(mockDataDBUsers.chargeCompteParId(eq("unknown"), eq("test"))).thenThrow(new DataNotFoundException("Compte introuvable"));

		String urlWrongCompte =  BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=unknown&mois=1&annee=2018";
		LOGGER.info("Wrong Compte : {}", urlWrongCompte);
		// Wrong compte
		getMockAPI().perform(
				get(urlWrongCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("test")))
		.andExpect(status().is4xxClientError());
	}



	@Test
	public void testGetBudgetOK() throws Exception {

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_QUERY_FULL + "?idCompte=C1&mois=1&annee=2018";
		LOGGER.info("Good Compte : {}", urlGoodCompte);

		getMockAPI().perform(
				get(urlGoodCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("{\"id\":\""+bo.getId()+"\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":true")));
	}





	@Test
	public void testReinitbudget() throws Exception {

		BudgetMensuelDTO budget = new BudgetMensuelDTO();
		budget.setCompteBancaire(c1);
		budget.setMois(Month.JANUARY.getValue());
		budget.setAnnee(2018);
		budget.setActif(false);
		budget.setId();
		when(mockDataDBBudget.chargeBudgetMensuelDTO(any())).thenReturn(budget);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.JANUARY), eq(2018), any())).thenReturn(bo);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(any(), any())).thenReturn(bo.getId());

		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		user.setLibelle("userTest");
		user.setLogin("userTest");
		serviceUser.registerUserBusinessSession(user, "clear");

		// OK

		String url = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", "C1_2018_1");
		LOGGER.info("Reinit budget: {}", url);

		getMockAPI().perform(
				delete(url).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
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
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().is4xxClientError());
		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		serviceUser.registerUserBusinessSession(user, "ms");

		when(mockDataDBBudget.isBudgetActif(eq("TESTKO"))).thenReturn(Boolean.FALSE);
		when(mockDataDBBudget.isBudgetActif(eq("TESTOK"))).thenReturn(Boolean.TRUE);

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isNoContent());

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isOk());
	}


	/**
	 * Test budget
	 * @throws Exception
	 */
	@Test
	public void testIsBudgetUptodate() throws Exception{
		String urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TEST");
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().is4xxClientError());

		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		serviceUser.registerUserBusinessSession(user, "ms");

		Calendar futur = Calendar.getInstance();
		futur.add(Calendar.HOUR_OF_DAY, 1);

		Calendar passe = Calendar.getInstance();
		passe.add(Calendar.HOUR_OF_DAY, -1);

		when(mockDataDBBudget.getDateMiseAJourBudget(eq("TESTKO"), any())).thenReturn(futur.getTime());
		when(mockDataDBBudget.getDateMiseAJourBudget(eq("TESTOK"), any())).thenReturn(passe.getTime());

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isNoContent());

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK") + "?uptodateto=" + Calendar.getInstance().getTimeInMillis();
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(get(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
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

		Utilisateur user = new Utilisateur();
		user.setId("userTest");
		serviceUser.registerUserBusinessSession(user, "ms");

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
		bo.setResultatMoisPrecedent(0D, 100D);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTKO"), any())).thenReturn(bo);
		bo.setActif(true);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("TESTOK"), any())).thenReturn(bo);

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTKO") + "?actif=true";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(post(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("\"actif\":true")));

		urlActif = BudgetApiUrlEnum.BUDGET_ETAT_FULL.replace("{idBudget}", "TESTOK") + "?actif=false";
		LOGGER.info("is Actif : {}", urlActif);
		getMockAPI().perform(post(urlActif).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest")))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("\"actif\":false")));
	}



	@Test
	public void testGetBudget() throws Exception {

		String urlBadBudget = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", bo.getId()+"XXX");
		LOGGER.info("Bad Budget : {}", urlBadBudget);

		getMockAPI().perform(
				get(urlBadBudget).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_ID_FULL.replace("{idBudget}", bo.getId());
		LOGGER.info("Good Budget : {}", urlGoodCompte);

		getMockAPI().perform(
				get(urlGoodCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON))
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

		String urlBadBudget = BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE_FULL.replace("{idBudget}", bo.getId()+"XXX").replace("{idOperation}", "ID_op");
		LOGGER.info("Bad SetOperation : {}", urlBadBudget);

		getMockAPI().perform(
				post(urlBadBudget).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(bo)))
		.andExpect(status().isNoContent());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_DERNIERE_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "ID_op");
		LOGGER.info("Good SetOperation : {}", urlGoodCompte);

		getMockAPI().perform(
				post(urlGoodCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
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

		String urlBadBudget = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "ID_op");
		LOGGER.info("Bad del : {}", urlBadBudget);

		getMockAPI().perform(
				delete(urlBadBudget).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		// OK

		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", "OP1");
		LOGGER.info("Good Del : {}", urlGoodCompte);

		getMockAPI().perform(
				delete(urlGoodCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON))
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

		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opupdate.setId("OP1");
		String urlGoodCompte = BudgetApiUrlEnum.BUDGET_OPERATION_FULL.replace("{idBudget}", bo.getId()).replace("{idOperation}", opupdate.getId());
		String jsonopupdate = json(opupdate);
		
		LOGGER.info("Good Update {} : {}", urlGoodCompte, jsonopupdate);

		getMockAPI().perform(
				post(urlGoodCompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonopupdate))
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
		when(mockDataDBUsers.chargeCompteParId(eq("C2"), anyString())).thenReturn(c2);


		BudgetMensuel bo2 = new BudgetMensuel();
		bo2.setCompteBancaire(c2);
		bo2.setMois(Month.JANUARY);
		bo2.setAnnee(2018);
		bo2.setActif(true);
		bo2.setId("C2_2018_1");
		bo2.setSoldeFin(0D);
		bo2.setSoldeNow(1000D);
		bo2.setDateMiseAJour(Calendar.getInstance());
		bo2.setResultatMoisPrecedent(0D, 100D);
		when(mockDataDBBudget.sauvegardeBudgetMensuel(eq(bo2), any())).thenReturn(bo2.getId());
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("C2_2018_1"), any())).thenReturn(bo2);
		when(mockDataDBBudget.chargeBudgetMensuelById(eq("C1_2018_1"), any())).thenReturn(bo);
		when(mockDataDBBudget.chargeBudgetMensuel(eq(c2), eq(Month.JANUARY), eq(2018), any())).thenReturn(bo2);
		when(mockDataDBBudget.chargeBudgetMensuel(any(), eq(Month.DECEMBER), eq(2017), any())).thenThrow(new BudgetNotFoundException("Mock"));

		CategorieOperation cat = new CategorieOperation(IdsCategoriesEnum.REMBOURSEMENT);
		CategorieOperation sscat = new CategorieOperation(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE);
		sscat.setCategorieParente(cat);

		when(mockDataDBParams.chargeCategories()).thenReturn(Arrays.asList(cat, sscat));
		when(mockDataDBParams.getCategorieParId(eq(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE.name()))).thenReturn(sscat);
		
		LigneOperation opIntercompte = new LigneOperation(sscat, "OPInter", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.PREVUE, false);
		opIntercompte.setId("OPInter");
		bo.getListeOperations().add(opIntercompte);


		String urlIntercompte = BudgetApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE_FULL
									.replace("{idBudget}", bo.getId())
									.replace("{idOperation}", opIntercompte.getId())
									.replace("{idCompte}", c2.getId());
		c2.setActif(false);
		LOGGER.info("Bad Intercompte : {}", urlIntercompte);
		getMockAPI().perform(
				post(urlIntercompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(opIntercompte)))
			.andExpect(status().is4xxClientError());


		// OK
		LigneOperation opupdate = new LigneOperation(sscat, "OP1", TypeOperationEnum.CREDIT, "213", EtatOperationEnum.REALISEE, false);
		opupdate.setId("OP1");

		LOGGER.info("Good Intercompte : {}", urlIntercompte);
		c2.setActif(true);
		getMockAPI().perform(
				post(urlIntercompte).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(opupdate)))
		.andExpect(status().isOk());
	}
}
