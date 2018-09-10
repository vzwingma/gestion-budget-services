package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;

/**
 * Mock de l'authentification
 * @author vzwingma
 *
 */
@Configuration
public class TestMockAuthServicesConfig {


    private UtilisateursService mockAuthService = Mockito.mock(UtilisateursService.class);


	@Bean UtilisateursService mockAuthService(){
		return this.mockAuthService;
	}

	
    /**
     * @return the mockAuthService
     */
    public UtilisateursService getMockAuthService() {
        return mockAuthService;
    }
    

}
