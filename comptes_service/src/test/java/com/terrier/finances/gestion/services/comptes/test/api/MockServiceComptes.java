package com.terrier.finances.gestion.services.comptes.test.api;

import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRequest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.terrier.finances.gestion.services.comptes.spi.ComptesDatabaseAdaptor;

@Configuration
@ComponentScan(basePackages = {	
		"com.terrier.finances.gestion.services.comptes.api",
		"com.terrier.finances.gestion.services.communs.api.interceptors" })
public class MockServiceComptes {


	@Bean
	public IComptesRequest mockComptesService(){
		return Mockito.mock(IComptesRequest.class);
	}

}
