/**
 * 
 */
package com.terrier.finances.gestion.test.config;

import java.util.Date;
import java.util.UUID;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.services.communs.api.config.RessourcesConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Classe abstraite des tests d'API
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={RessourcesConfig.class})
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
			return null;
		}
	}
	
	
	public static String getTestToken(String id){
		return getTestToken(id, id);
	}
	
	public static String getTestToken(String id, String login){
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
				.setSubject(login)
				.setId(UUID.randomUUID().toString())
				.claim(JwtConfig.JWT_CLAIM_USERID_HEADER, id)
				// Convert to list of strings. 
//				.claim("authorities", auth.getAuthorities().stream()
//						.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + JwtConfig.JWT_EXPIRATION_S * 1000))  // in milliseconds
				.signWith(SignatureAlgorithm.HS512, JwtConfig.JWT_SECRET_KEY.getBytes())
				.compact();

		// Add token to header
		return JwtConfig.JWT_AUTH_PREFIX + token;
	}
	
}
