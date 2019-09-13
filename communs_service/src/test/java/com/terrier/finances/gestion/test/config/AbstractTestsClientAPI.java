/**
 * 
 */
package com.terrier.finances.gestion.test.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import okhttp3.mockwebserver.MockWebServer;

/**
 * Classe abstraite des tests de client d'API
 * Création d'un mock webserver
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
public abstract class AbstractTestsClientAPI {

	/**
	 * Logger
	 */
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	/*
	 * Server API mock
	 */

	private final MockWebServer mockWebServer = new MockWebServer();
	
	@Autowired
	private MockApiClient testClient;

	/**
	 * Init du client suivant le serveur Mock Créé
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	@BeforeEach
	void initClient() throws KeyManagementException, NoSuchAlgorithmException {
		testClient.setBaseURL("http://"+mockWebServer.getHostName()+":"+ mockWebServer.getPort());
		testClient.createWebClient();
	}
	
	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}


	/**
	 * @return the mockWebServer
	 */
	public MockWebServer getMockWebServer() {
		LOGGER.info("Mock Web Server : {}:{}", mockWebServer.getHostName(), mockWebServer.getPort());
		return mockWebServer;
	}

	/**
	 * @return the mockServer
	 */
	public MockApiClient getTestClient() {
		return testClient;
	}
}
