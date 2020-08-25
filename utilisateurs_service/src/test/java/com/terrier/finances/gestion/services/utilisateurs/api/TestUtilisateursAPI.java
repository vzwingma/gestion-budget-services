/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;
import com.terrier.finances.gestion.services.utilisateurs.model.v12.Utilisateur;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBUtilisateursConfig;

/**
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBUtilisateursConfig.class})
class TestUtilisateursAPI extends AbstractTestsAPI  {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtilisateursAPI.class);

	@Autowired
	private UtilisateurDatabaseService mockDataDBUsers;

	@BeforeEach
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void mockApplicationUser() {
		OAuth2User applicationUser = Mockito.mock(OAuth2User.class);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
		when(applicationUser.getAttribute(eq("name"))).thenReturn("User");

		assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		assertEquals("User", ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("name"));
	}

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


		Utilisateur userOK = new Utilisateur();
		userOK.setId("345345");
		userOK.setLogin("Test");
		userOK.setDernierAcces(LocalDateTime.now());
		when(mockDataDBUsers.chargeUtilisateur(eq("345345"))).thenReturn(userOK);
		LOGGER.info("LastTime OK {}", BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL);
		getMockAPI().perform(
				get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("\"lastAccessTime\"")));

	}	
}
