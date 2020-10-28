/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.test.api;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest;
import com.terrier.finances.gestion.services.utilisateurs.test.data.TestDataUtilisateur;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;

/**
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={MockServiceUtilisateurs.class})
class TestUtilisateursAPI extends AbstractTestsAPI  {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtilisateursAPI.class);

	@Autowired
	private IUtilisateursRequest mockServiceUtilisateurs;


	@Test
	void testLastTime() throws Exception {

		// Fail
		LOGGER.info("LastTime KO");
		getMockAPI().perform(
				get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL))
		.andExpect(status().is4xxClientError());

		/** Authentification **/
		authenticateUser("345345");
		assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		assertEquals("345345", ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("login"));

		when(mockServiceUtilisateurs.getUtilisateur(eq("345345"))).thenReturn(TestDataUtilisateur.getTestUtilisateur());
		LOGGER.info("LastTime OK {}", BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL);
		getMockAPI().perform(
				get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("\"lastAccessTime\"")));

	}




	@Test
	void testPreferencesUtilisateur() throws Exception {

		/** Authentification **/
		authenticateUser("345345");
		assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		assertEquals("345345", ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("login"));

		when(mockServiceUtilisateurs.getUtilisateur(eq("345345"))).thenReturn(TestDataUtilisateur.getTestUtilisateur());
		getMockAPI().perform(
				get(BudgetApiUrlEnum.USERS_PREFS_FULL)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"PREFS_STATUT_NLLE_DEPENSE\"")));

	}
}
