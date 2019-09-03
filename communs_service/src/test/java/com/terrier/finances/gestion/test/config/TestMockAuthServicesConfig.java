package com.terrier.finances.gestion.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Mock de l'authentification
 * @author vzwingma
 *
 */
@Configuration
public class TestMockAuthServicesConfig {


  
    

	@Bean BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
