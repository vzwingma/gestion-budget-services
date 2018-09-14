package com.terrier.finances.gestion.services.comptes.api;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestComptesAPI extends AbstractTestsAPI {


	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;
	@Autowired
	private BudgetDatabaseService mockDataDBBudget;
	


	@Test
	public void testGetComptes() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.COMPTES_LIST_FULL))
		.andExpect(status().is4xxClientError());

		
		when(mockDataDBUsers.chargeComptes(eq("123123"))).thenReturn(null);

		List<CompteBancaire> comptes = new ArrayList<CompteBancaire>();
		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		comptes.add(c1);
		CompteBancaire c2 = new CompteBancaire();
		c2.setActif(true);
		c2.setId("C2");
		c2.setLibelle("Libelle2");
		c2.setOrdre(2);		
		comptes.add(c2);
		when(mockDataDBUsers.chargeComptes(eq("345345"))).thenReturn(comptes);

		
		// Comptes KO
		getMockAPI().perform(
				get(BudgetApiUrlEnum.COMPTES_LIST_FULL + "/123123")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		// Comptes OK		
		getMockAPI().perform(
				get(BudgetApiUrlEnum.COMPTES_LIST_FULL + "/345345")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("[{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true},{\"id\":\"C2\",\"libelle\":\"Libelle2\",\"itemIcon\":null,\"ordre\":2,\"actif\":true}]"));
		
	}	
	

	@Test
	public void testCompte() throws Exception {
		// Fail*
		String path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "AAA").replace("{idUtilisateur}", "111");
		getMockAPI().perform(
				post(path))
		.andExpect(status().is4xxClientError());

		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockDataDBUsers.chargeCompteParId(eq("111"), eq("345345"))).thenReturn(c1);
		
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111").replace("{idUtilisateur}", "123123");
		
		// Compte KO
		getMockAPI().perform(
				get(path)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111").replace("{idUtilisateur}", "345345");
		getMockAPI().perform(
				get(path)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true}"));;		
	}	
	
	
	@Test
	public void testIntervalles() throws Exception {
		String path = BudgetApiUrlEnum.COMPTES_INTERVALLES_FULL.replace("{idCompte}", "TEST");
		getMockAPI().perform(get(path))
		.andExpect(status().is4xxClientError());
		
		BudgetMensuelDTO debut = new BudgetMensuelDTO();
		debut.setAnnee(2018);
		debut.setMois(1);
		
		BudgetMensuelDTO fin = new BudgetMensuelDTO();
		fin.setAnnee(2018);
		fin.setMois(2);

		when(mockDataDBBudget.getPremierDernierBudgets(anyString())).thenReturn(new BudgetMensuelDTO[]{ debut, fin});
		getMockAPI().perform(
				get(path))
			.andExpect(status().isOk())
			.andExpect(content().string("{\"datePremierBudget\":17563,\"dateDernierBudget\":17622}"));
	}
}