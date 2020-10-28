package com.terrier.finances.gestion.services.comptes.test.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRequest;
import com.terrier.finances.gestion.services.comptes.test.data.TestDataComptes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.comptes.spi.ComptesDatabaseAdaptor;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;

/**
 * Test de l'API compte
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={MockServiceComptes.class})
class TestComptesAPI extends AbstractTestsAPI {


	@Autowired
	private IComptesRequest mockComptesService;


	@Test
	void testGetComptes() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.COMPTES_LIST_FULL))
		.andExpect(status().is4xxClientError());

		/** Authentification **/
		authenticateUser("123123");

		when(mockComptesService.getComptesUtilisateur(eq("345345"))).thenReturn(null);

		/** Authentification **/
		authenticateUser("345345");

		when(mockComptesService.getComptesUtilisateur(eq("345345"))).thenReturn(TestDataComptes.getListeComptes());

		// Comptes OK		
		getMockAPI().perform(
				get(BudgetApiUrlEnum.COMPTES_LIST_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("[{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true},{\"id\":\"C2\",\"libelle\":\"Libelle2\",\"itemIcon\":null,\"ordre\":2,\"actif\":true},{\"id\":\"A3\",\"libelle\":\"ALibelle3\",\"itemIcon\":null,\"ordre\":0,\"actif\":true}]"));
		
	}	
	

	@Test
	void testCompte() throws Exception {
		// Fail*
		String path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "AAA");
		getMockAPI().perform(
				post(path))
		.andExpect(status().is4xxClientError());

		/** Authentification **/
		authenticateUser("345345");

		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);
		when(mockComptesService.getCompteById(eq("111"), eq("345345"))).thenReturn(c1);
		when(mockComptesService.getCompteById(eq("111"), eq("123123"))).thenThrow(new DataNotFoundException("Mock : Compte 111 introuvable pour 123123"));
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111");
		
		// Compte OK

		
		path = BudgetApiUrlEnum.COMPTES_ID_FULL.replace("{idCompte}", "111");
		getMockAPI().perform(
				get(path)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true}"));;		
	}	
}
