/**
 * 
 */
package com.terrier.finances.gestion.test.config;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.filters.OutcomingRequestFilter;

/**
 * Classe abstraite des tests d'API
 * @author vzwingma
 *
 */
@EnableWebMvc
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={OutcomingRequestFilter.class })
@WebAppConfiguration
public abstract class AbstractTestsAPI {

	/**
	 * Logger
	 */
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * Client API mock
	 */
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private OAuth2User applicationUser;
	
	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
		this.applicationUser = Mockito.mock(OAuth2User.class);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
	}

	public void authenticateUser(String loginUser) {
        when(applicationUser.getAttribute(eq("name"))).thenReturn(loginUser);
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
	public static String json(AbstractAPIObjectModel restObject){
		try {
			return new ObjectMapper().writeValueAsString(restObject);
		} catch (JsonProcessingException e) {
			LoggerFactory.getLogger(AbstractTestsAPI.class).error("Erreur lors du JSON {}", restObject, e);
			return null;
		}
	}
}
