/**
 * 
 */
package com.terrier.finances.gestion.test.config;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.abstrait.AbstractRestObjectModel;
import com.terrier.finances.gestion.services.communs.rest.config.RessourcesConfig;

/**
 * Classe abstraite des tests d'API
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={RessourcesConfig.class})
public abstract class AbstractTestAPI {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	/**
	 * @return the mockMvc
	 */
	public MockMvc getMockAPI() {
		return mockMvc;
	}
	
	/**
	 * 
	 * @param restObject
	 * @return objet en JSON
	 */
	public static String json(AbstractRestObjectModel restObject){
		try {
			return new ObjectMapper().writeValueAsString(restObject);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
}
