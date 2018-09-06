/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthentificationRestObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

/**
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestAuthentificationAPI extends AbstractTestsAPI  {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestAuthentificationAPI.class);

	
	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;

	@Test
	public void testAuthenticate() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.AUTH_AUTHENTICATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());

		Utilisateur userOK = new Utilisateur();
		userOK.setId("test");
		userOK.setLogin("Test");
		userOK.setHashMotDePasse("1000:2f71112c1693c5378e1cd3a0d4884b20:9a00559a79652c140e6539bec1b9ed78fd2a2f42bd0b6f409c399564f8031766c53927b1594579598794cf372af872632450b35196959ac334f7bc97a5a5ddc0");
		userOK.setMasterCleChiffrementDonnees("Sf35rwnRDc7v4SDXsnGHUg==");
		when(mockDataDBUsers.chargeUtilisateur(eq("Test"))).thenReturn(null, userOK);
		
		
		// AuthFailed
		AuthentificationRestObject auth = new AuthentificationRestObject("Test", "mdpTest");
		
		LOGGER.info("Authentification Failed de {}", json(auth));
		
		getMockAPI().perform(
				post(BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.AUTH_AUTHENTICATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(auth) ))
		.andExpect(status().isForbidden());
		
		// AuthOK
		AuthentificationRestObject auth2 = new AuthentificationRestObject("Test", "test");
		LOGGER.info("Authentification OK de {}", json(auth2));
		getMockAPI().perform(
				post(BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.AUTH_AUTHENTICATE_FULL)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(json(auth2) ))
				.andExpect(status().isOk())
				.andExpect(header().exists("Content-Type"))
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()))
				.andExpect(content().string("{\"login\":\"Test\",\"motDePasse\":\"test\",\"idUtilisateur\":\"test\"}"));
	}	
}
