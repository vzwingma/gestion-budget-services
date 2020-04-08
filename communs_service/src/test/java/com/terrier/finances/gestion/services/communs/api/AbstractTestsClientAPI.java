/**
 * 
 */
package com.terrier.finances.gestion.services.communs.api;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.api.filters.OutcomingRequestFilter;
import com.terrier.finances.gestion.test.config.MockApiClient;

import okhttp3.mockwebserver.MockWebServer;

/**
 * Classe abstraite des tests de client d'API
 * Création d'un mock webserver
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={MockApiClient.class, OutcomingRequestFilter.class})
public abstract class AbstractTestsClientAPI {

	/**
	 * Logger
	 */
	protected final Logger LOGGER = LoggerFactory.getLogger(AbstractTestsClientAPI.class);
	/*
	 * Server API mock
	 */
	private MockWebServer mockWebServer;

	@Autowired
	private MockApiClient testClient;

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

	/**
	 * @return the mockServer
	 */
	public MockApiClient getTestClient() {
		return testClient;
	}
}
