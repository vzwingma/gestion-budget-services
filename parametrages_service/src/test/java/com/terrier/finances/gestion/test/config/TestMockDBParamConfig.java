package com.terrier.finances.gestion.test.config;

import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.services.parametrages.spi.ParametragesDatabaseService;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.parametrages.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors",
		"com.terrier.finances.gestion.services.parametrages.business" })
public class TestMockDBParamConfig {

	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	
	private IParametrageRepository mockParamsDBService = Mockito.mock(ParametragesDatabaseService.class);
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	
	@Bean
	public IParametrageRepository mockDataDBParams() {
		return this.mockParamsDBService;
	}
}
