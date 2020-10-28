package com.terrier.finances.gestion.test.config;

import static org.mockito.Mockito.mock;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient;
import com.terrier.finances.gestion.services.budgets.api.client.ParametragesAPIClient;
import com.terrier.finances.gestion.services.budgets.spi.OperationsDatabaseAdaptator;
import com.terrier.finances.gestion.services.communs.api.config.oauth2.SecurityOAuth2Config;
import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;

@Configuration
@ContextConfiguration(classes={TestMockBudgetServiceConfig.class, SecurityOAuth2Config.class, IncomingRequestInterceptor.class})
public class TestMockBudgetServiceConfig {

	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	

	@Bean
	public OperationsDatabaseAdaptator budgetDatabaseService() {
		return Mockito.mock(OperationsDatabaseAdaptator.class);
	}
	

	@Bean
	public ParametragesAPIClient parametragesAPIClient() {
		return mock(ParametragesAPIClient.class);
	}
	
	@Bean
	public ComptesAPIClient mockDataAPIComptes() {
		return mock(ComptesAPIClient.class);
	}
	
}
