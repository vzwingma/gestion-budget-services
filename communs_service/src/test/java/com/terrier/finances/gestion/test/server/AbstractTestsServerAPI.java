/**
 * 
 */
package com.terrier.finances.gestion.test.server;

import com.terrier.finances.gestion.communs.api.filters.OutcomingRequestFilter;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

/**
 * Classe abstraite des tests de client d'API
 * Création d'un mock webserver
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={ OutcomingRequestFilter.class })
public abstract class AbstractTestsServerAPI {

	/**
	 * Logger
	 */
	protected final Logger LOGGER = LoggerFactory.getLogger(AbstractTestsServerAPI.class);
	/*
	 * Server API mock
	 */
	private MockWebServer mockWebServer;

	/**
	 * Init du serveur Mock Créé
	 * @throws IOException erreur création serveur 
	 */
	@BeforeEach
	public void initServer() throws IOException {
		if(this.mockWebServer == null) {
			MockWebServer mockWebServer = new MockWebServer();
			mockWebServer.start(getServerPort());
			this.mockWebServer = mockWebServer;
		}
	}

	public abstract int getServerPort(); 

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}


	/**
	 * @return the mockWebServer
	 */
	public MockWebServer getMockWebServer() {
		return mockWebServer;
	}

}
