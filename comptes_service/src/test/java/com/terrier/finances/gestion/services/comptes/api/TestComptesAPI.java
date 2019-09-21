package com.terrier.finances.gestion.services.comptes.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.comptes.data.ComptesDatabaseService;
import com.terrier.finances.gestion.services.comptes.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBComptesConfig;

/**
 * Test de l'API compte
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBComptesConfig.class})
public class TestComptesAPI extends AbstractTestsAPI {


	@Autowired
	private ComptesDatabaseService mockComptesDBService;

//	
//	@BeforeEach
//	public void init() {
//		Utilisateur user = new Utilisateur();
//		user.setId("345345");
//		user.setLogin("345345");
//		user.setLibelle("345345");
//		serviceUser.registerUserBusinessSession(user);
//		Utilisateur user2 = new Utilisateur();
//		user2.setId("123123");
//		user2.setLogin("123123");
//		user2.setLibelle("123123");
//		serviceUser.registerUserBusinessSession(user2);
//	}

	@Test
	public void testGetComptes() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.COMPTES_LIST_FULL))
		.andExpect(status().is4xxClientError());

		
		when(mockComptesDBService.chargeComptes(eq("123123"))).thenReturn(null);

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
		when(mockComptesDBService.chargeComptes(eq("345345"))).thenReturn(comptes);

		// Comptes KO
		getMockAPI().perform(
				get(BudgetApiUrlEnum.COMPTES_LIST_FULL).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("123123"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		// Comptes OK		
		getMockAPI().perform(
				get(BudgetApiUrlEnum.COMPTES_LIST_FULL).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("345345"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("[{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true},{\"id\":\"C2\",\"libelle\":\"Libelle2\",\"itemIcon\":null,\"ordre\":2,\"actif\":true}]"));
		
	}	
	

	@Test
	public void testCompte() throws Exception {
		// Fail*
		String path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "AAA");
		getMockAPI().perform(
				post(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("111")))
		.andExpect(status().is4xxClientError());

		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockComptesDBService.chargeCompteParId(eq("111"), eq("345345"))).thenReturn(c1);
		when(mockComptesDBService.chargeCompteParId(eq("111"), eq("123123"))).thenThrow(new DataNotFoundException("Mock : Compte 111 introuvable pour 123123"));
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111");
		
		// Compte KO
		LOGGER.info("testCompte : {}", path);
		getMockAPI().perform(
				get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("123123"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111");
		getMockAPI().perform(
				get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("345345"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true}"));;		
	}	
	
	
	@Test
	public void testIntervalles() throws Exception {
		String path = BudgetApiUrlEnum.COMPTES_INTERVALLES_FULL.replace("{idCompte}", "TEST");
		getMockAPI().perform(get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("TEST")))
		.andExpect(status().is4xxClientError());
		
		BudgetMensuelDTO debut = new BudgetMensuelDTO();
		debut.setAnnee(2018);
		debut.setMois(1);
		
		BudgetMensuelDTO fin = new BudgetMensuelDTO();
		fin.setAnnee(2018);
		fin.setMois(2);

		when(mockComptesDBService.getPremierDernierBudgets(anyString())).thenReturn(new BudgetMensuelDTO[]{ debut, fin});
		getMockAPI().perform(
				get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("123123")))
			.andExpect(status().isOk())
			.andExpect(content().string("{\"datePremierBudget\":17563,\"dateDernierBudget\":17622}"));
	}
	
	
	
	/**
	 * Tests libellés
	 * @throws Exception
	 */
	@Test
	public void testLibelles() throws Exception {
		String path = BudgetApiUrlEnum.COMPTES_OPERATIONS_LIBELLES_FULL.replace("{idCompte}", "TEST") + "?annee=2019";
		getMockAPI().perform(get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("123123")))
		.andExpect(status().isNoContent());
		
//		Utilisateur user = new Utilisateur();
//		user.setId("TEEST");
//		serviceUser.registerUserBusinessSession(user);
		
		Set<String> libelles = new HashSet<>();
		libelles.add("OPE1");
		libelles.add("OPE2");
		when(mockComptesDBService.chargeLibellesOperations(eq("TEST"), eq(2019))).thenReturn(libelles);
		
		getMockAPI().perform(get(path).header(JwtConfigEnum.JWT_HEADER_AUTH, getTestToken("123123")))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"idCompte\":\"TEST\",\"libellesOperations\":[\"OPE1\",\"OPE2\"]}"));
	}
}
