/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

/**
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestUtilisateursAPI extends AbstractTestsAPI  {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtilisateursAPI.class);

	@Autowired
	private UtilisateursService service;
	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;

	@Test
	public void testAuthenticate() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());

		Utilisateur userOK = new Utilisateur();
		userOK.setId("test");
		userOK.setLogin("Test");
		userOK.setPassword(new BCryptPasswordEncoder().encode("Test"));
		userOK.setMasterCleChiffrementDonnees("Sf35rwnRDc7v4SDXsnGHUg==");
		when(mockDataDBUsers.chargeUtilisateur(eq("Test"))).thenReturn(null, userOK);
		
		
		// AuthFailed
		AuthLoginAPIObject auth = new AuthLoginAPIObject("Test", "mdpTest");
		
		LOGGER.info("Authentification Failed de {}", json(auth));
		
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(auth) ))
		.andExpect(status().isForbidden());
	}	
	


	@Test
	public void testDisconnect() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_DISCONNECT_FULL).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest22")))
		.andExpect(status().is4xxClientError());

		Utilisateur userOK = new Utilisateur();
		userOK.setId("345345");
		userOK.setLogin("345345");
		service.registerUserBusinessSession(userOK, "345345");
		
		LOGGER.info("Disconnect Failed");
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_DISCONNECT_FULL).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("userTest22"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnauthorized());
		
		LOGGER.info("Disconnect OK");
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_DISCONNECT_FULL).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("345345"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
		
	}	
	
	


	@Test
	public void testLastTime() throws Exception {
		
		// Fail
		LOGGER.info("LastTime KO");
		getMockAPI().perform(
				post(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("345345")))
		.andExpect(status().is4xxClientError());

		Utilisateur userOK = new Utilisateur();
		userOK.setId("345345");
		userOK.setLogin("Test");
		userOK.setDernierAcces(LocalDateTime.now());
		service.registerUserBusinessSession(userOK, "test");
		LOGGER.info("LastTime OK {}", BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL);
		getMockAPI().perform(
				get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL).header(JwtConfig.JWT_AUTH_HEADER, getTestToken("345345", "Test"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("\"lastAccessTime\"")));
		
	}	
}
