package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.statut.business.StatusApplicationService;
import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;

/**
 * Mock de l'authentification
 * @author vzwingma
 *
 */
@Configuration
public class TestMockAuthServicesConfig {

    private StatusApplicationService mockStatutService = Mockito.mock(StatusApplicationService.class);
    private AuthenticationService mockAuthService = Mockito.mock(AuthenticationService.class);


	@Bean AuthenticationService mockAuthService(){
		return this.mockAuthService;
	}

	
	@Bean StatusApplicationService mockStatutService(){
		return this.mockStatutService;
	}
	
	/**
	 * @return the mockAuthService
	 */
	public StatusApplicationService getMockStatutService() {
		return mockStatutService;
	}
	
    /**
     * @return the mockAuthService
     */
    public AuthenticationService getMockAuthService() {
        return mockAuthService;
    }
    

}
