/**
 * 
 */
package com.terrier.finances.gestion.business;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.auth.PasswordEncoder;

/**
 * @author vzwingma
 *
 */
public class TestPasswordEncoder {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPasswordEncoder.class);


	@Test
	public void testPassWordEncoder() throws Exception{
		String originalPassword = "Secrete@343";

		String generatedSecuredPasswordHash = PasswordEncoder.generateStrongPasswordHash(originalPassword);
		LOGGER.debug("Mot de passe : {}", generatedSecuredPasswordHash);

		String generatedSecuredPasswordHash2 = PasswordEncoder.generateStrongPasswordHash(originalPassword);
		LOGGER.debug("Mot de passe 2 : {}", generatedSecuredPasswordHash2);
		assertNotEquals(generatedSecuredPasswordHash2, generatedSecuredPasswordHash);
		assertTrue(PasswordEncoder.validatePassword(originalPassword, generatedSecuredPasswordHash2));
	}
	

	@Test
	public void generate() throws Exception{
		String originalPassword = "test";

		String generatedSecuredPasswordHash = PasswordEncoder.generateStrongPasswordHash(originalPassword);
		LOGGER.debug("Mot de passe : {}", generatedSecuredPasswordHash);
	}
}
