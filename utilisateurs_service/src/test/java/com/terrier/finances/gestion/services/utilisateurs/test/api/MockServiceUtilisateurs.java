package com.terrier.finances.gestion.services.utilisateurs.test.api;

import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.utilisateurs.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors"})
public class MockServiceUtilisateurs {

	@Autowired
	private IUtilisateursRequest mockServiceUtilisateurs;

	@Bean
	public IUtilisateursRequest createMockServiceUtilisateurs(){
		return mock(IUtilisateursRequest.class);
	}
}
