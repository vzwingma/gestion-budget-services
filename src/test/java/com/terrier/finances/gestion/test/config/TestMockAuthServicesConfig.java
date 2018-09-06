package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;

/**
 * Mock de l'authentification
 * @author vzwingma
 *
 */
@Configuration
public class TestMockAuthServicesConfig {


    private AuthenticationService mockAuthService = Mockito.mock(AuthenticationService.class);


	@Bean AuthenticationService mockAuthService(){
		return this.mockAuthService;
	}

	
    /**
     * @return the mockAuthService
     */
    public AuthenticationService getMockAuthService() {
        return mockAuthService;
    }
    

}
