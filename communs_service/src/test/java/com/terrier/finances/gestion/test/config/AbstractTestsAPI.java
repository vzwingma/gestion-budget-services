/**
 * 
 */
package com.terrier.finances.gestion.test.config;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

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
	
	public static final String ID_USER = "idProprietaire";
	/*
	 * Client API mock
	 */
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
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
	public static String json(AbstractAPIObjectModel restObject){
		try {
			return new ObjectMapper().writeValueAsString(restObject);
		} catch (JsonProcessingException e) {
			LoggerFactory.getLogger(AbstractTestsAPI.class).error("Erreur lors du JSON {}", restObject, e);
			return null;
		}
	}


	public static String getTestToken(String id){
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
				.setSubject(id)
				.setId(id)
				.claim(JwtConfigEnum.JWT_CLAIM_HEADER_USERID, id)
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + 10000))  // in milliseconds
				.signWith(Keys.hmacShaKeyFor(JwtConfigEnum.getJwtSecretKey().getBytes()))
				.compact();
		// Add token to header
		return JwtConfigEnum.JWT_HEADER_AUTH_PREFIX + token;
	}

}
