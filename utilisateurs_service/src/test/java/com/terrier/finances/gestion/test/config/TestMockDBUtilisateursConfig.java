package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.services.utilisateurs.spi.UtilisateurDatabaseAdaptor;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.utilisateurs.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors",
		"com.terrier.finances.gestion.services.utilisateurs.business" })	
public class TestMockDBUtilisateursConfig {

	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	
	private UtilisateurDatabaseAdaptor mockDataDBUsers = Mockito.mock(UtilisateurDatabaseAdaptor.class);
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	
	@Bean
	public UtilisateurDatabaseAdaptor mockDataDBUsers() {
		return this.mockDataDBUsers;
	}
}
