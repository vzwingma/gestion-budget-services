package com.terrier.finances.gestion.services.budgets.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.interceptors.LogApiFilter;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


/**
 * @author vzwingma
 *
 */
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestParametragesAPIClient.class, ParametragesAPIClient.class, LogApiFilter.class})
public class TestParametragesAPIClient {

	
	@Autowired
	private MockWebServer mockWebServer;
	
	@Bean
	public MockWebServer createServer() throws IOException {
		MockWebServer web = new MockWebServer();
		web.start(8091);
		return web;
	}
	
	@Autowired
	private ParametragesAPIClient client;
	
	@Test
	void testCallGetCategories() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {
		CategorieOperation c = new CategorieOperation();
		c.setId("1");
		c.setActif(true);
		c.setLibelle("1");
		ObjectMapper mapper = new ObjectMapper();
		String outS = mapper.writerFor(CategorieOperation.class).writeValueAsString(c);
		
		mockWebServer.enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
					.setBody("["+outS.toString()+"]")
				);
		
		List<CategorieOperation> listeCategories = client.getCategories();
		assertNotNull(listeCategories);
		RecordedRequest recordedRequest = mockWebServer.takeRequest();
		assertEquals("/parametres/v1/categories", recordedRequest.getPath());
	}
	
	
	
	@Test
	void testCallGetCategorie() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {
		CategorieOperation c = new CategorieOperation();
		c.setId("1");
		c.setActif(true);
		c.setLibelle("1");
		ObjectMapper mapper = new ObjectMapper();
		String outS = mapper.writerFor(CategorieOperation.class).writeValueAsString(c);
		
		mockWebServer.enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
					.setBody("["+outS.toString()+"]")
				);
		
		CategorieOperation cResult = client.getCategorieParId("1");
		assertNotNull(cResult);
		assertEquals("1", cResult.getLibelle());
		
		RecordedRequest recordedRequest = mockWebServer.takeRequest();
		assertEquals("/parametres/v1/categories", recordedRequest.getPath());
	}
	
	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}
}
	