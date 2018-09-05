/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.utilisateur.model.Authentification;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

/**
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestAuthentificationAPI  {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestAuthentificationAPI.class);

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testAuthenticate() throws Exception {
		// Fail
		mockMvc.perform(
				post("/rest/authentification/v1/authenticate")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());

		// AuthOK
		Authentification auth = new Authentification("Test", "mdpTest");
		String jsonAuth = new ObjectMapper().writeValueAsString(auth);
		LOGGER.info("Authentification de {}", jsonAuth);
		
		mockMvc.perform(
				post("/rest/authentification/v1/authenticate")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonAuth ))
		.andExpect(status().isNotFound());
		//        mockMvc.perform(
		//                post("/project")
		//                        .accept(MediaType.APPLICATION_JSON)
		//                        .contentType(MediaType.APPLICATION_JSON))
		//                .andExpect(status().isCreated());
	}	
}
