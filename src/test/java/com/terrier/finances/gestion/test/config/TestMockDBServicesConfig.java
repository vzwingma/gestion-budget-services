package com.terrier.finances.gestion.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.parametrages.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.services.statut.business.StatusApplicationService;
import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;

@Configuration
@ComponentScan(basePackages = {"com.terrier.finances.gestion.services.budget.business", 
								"com.terrier.finances.gestion.services.parametrages.business",
								"com.terrier.finances.gestion.services.budget.model.transformer"})	
public class TestMockDBServicesConfig {

	
	
	private StatusApplicationService mockStatutService = Mockito.mock(StatusApplicationService.class);
	private AuthenticationService mockAuthService = Mockito.mock(AuthenticationService.class);
	
	private MongoTemplate mockDBTemplate = Mockito.mock(MongoTemplate.class);
	private BudgetDatabaseService mockBudgetDBService = Mockito.mock(BudgetDatabaseService.class);
	private UtilisateurDatabaseService mockUserDBService = Mockito.mock(UtilisateurDatabaseService.class);
	private ParametragesDatabaseService mockParamsDBService = Mockito.mock(ParametragesDatabaseService.class);
	
	@Bean BudgetDatabaseService dataService(){
		return this.mockBudgetDBService;
	}
	
	@Bean UtilisateurDatabaseService utilisateurDatabaseService(){
		return this.mockUserDBService;
	}
	
	@Bean AuthenticationService mockAuthService(){
		return this.mockAuthService;
	}
	
	
	@Bean
	public MongoTemplate mockMongoTemplate(){
		return this.mockDBTemplate;
	}
	
	@Bean StatusApplicationService mockStatutService(){
		return this.mockStatutService;
	}
	
	@Bean ParametragesDatabaseService mockParamsDBService(){
		return this.mockParamsDBService;
	}

	/**
	 * @return the mockBudgetDBService
	 */
	public BudgetDatabaseService getMockBudgetDBService() {
		return mockBudgetDBService;
	}

	/**
	 * @return the mockUserDBService
	 */
	public UtilisateurDatabaseService getMockUserDBService() {
		return mockUserDBService;
	}

	/**
	 * @return the mockParamsDBService
	 */
	public ParametragesDatabaseService getMockParamsDBService() {
		return mockParamsDBService;
	}

	/**
	 * @return the mockAuthService
	 */
	public AuthenticationService getMockAuthService() {
		return mockAuthService;
	}
	
	
}
