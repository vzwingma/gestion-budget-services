package com.terrier.finances.gestion.services.parametrages.test.api;

import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.parametrages.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors"})
public class MockServiceParametrage {

	@Bean
	public IParametrageRequest createMockServiceParametrage(){
		return mock(IParametrageRequest.class);
	}
}
