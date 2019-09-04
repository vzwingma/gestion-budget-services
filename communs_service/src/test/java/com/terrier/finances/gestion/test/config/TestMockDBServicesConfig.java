package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

//Configuration
/*ComponentScan(basePackages = {
		"com.terrier.finances.gestion.services.statut.business",
		"com.terrier.finances.gestion.services.budget.business", 
		"com.terrier.finances.gestion.services.comptes.business",		
		"com.terrier.finances.gestion.services.parametrages.business",
		"com.terrier.finances.gestion.services.budget.model.transformer",
		"com.terrier.finances.gestion.services.communs.api.interceptors"})	
		*/
@Deprecated
public class TestMockDBServicesConfig {

	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	
}
