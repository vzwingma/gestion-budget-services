package com.terrier.finances.gestion.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Mock de l'authentification
 * @author vzwingma
 *
 */
@Deprecated
public class TestMockAuthServicesConfig {


  
    

	@Bean BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}