/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.business;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author vzwingma
 *
 */
public class TestPasswordEncoder {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPasswordEncoder.class);

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Test
	public void testPassWordEncoder() throws Exception{
		String originalPassword = "Secrete@343";

		String generatedSecuredPasswordHash = passwordEncoder().encode(originalPassword);
		LOGGER.debug("Mot de passe : {}", generatedSecuredPasswordHash);

		String generatedSecuredPasswordHash2 = passwordEncoder().encode(originalPassword);
		LOGGER.debug("Mot de passe 2 : {}", generatedSecuredPasswordHash2);
		assertNotEquals(generatedSecuredPasswordHash2, generatedSecuredPasswordHash);
		assertTrue(passwordEncoder().matches(originalPassword, generatedSecuredPasswordHash2));
	}
	

	@Test
	public void generate() throws Exception{
		String originalPassword = "test";

		String generatedSecuredPasswordHash = passwordEncoder().encode(originalPassword);
		LOGGER.debug("Mot de passe : {}", generatedSecuredPasswordHash);
		String generatedSecuredPasswordHash2 = passwordEncoder().encode(originalPassword);
		LOGGER.debug("Mot de passe 2 : {}", generatedSecuredPasswordHash2);

		assertNotEquals(generatedSecuredPasswordHash, generatedSecuredPasswordHash2);
	}
}
