package com.terrier.finances.gestion.services.budgets.test.api;

import static org.mockito.Mockito.mock;

import com.terrier.finances.gestion.services.budgets.business.ports.IComptesServiceProvider;
import com.terrier.finances.gestion.services.budgets.business.ports.IOperationsRequest;
import com.terrier.finances.gestion.services.budgets.business.ports.IParametragesServiceProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"com.terrier.finances.gestion.services.budgets.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors" })
public class MockServiceBudget {

	@Bean
	public IOperationsRequest mockComptesService(){
		return Mockito.mock(IOperationsRequest.class);
	}

	@Bean
	public IParametragesServiceProvider parametragesAPIClient() {
		return mock(IParametragesServiceProvider.class);
	}
	
	@Bean
	public IComptesServiceProvider mockDataAPIComptes() {
		return mock(IComptesServiceProvider.class);
	}
	
}
