package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.services.comptes.data.ComptesDatabaseService;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.comptes.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors",
		"com.terrier.finances.gestion.services.comptes.business" })	
public class TestMockDBComptesConfig {

	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	
	private ComptesDatabaseService mockComptesDBService = Mockito.mock(ComptesDatabaseService.class);
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	
	@Bean
	public ComptesDatabaseService mockDataDBParams() {
		return this.mockComptesDBService;
	}
}
