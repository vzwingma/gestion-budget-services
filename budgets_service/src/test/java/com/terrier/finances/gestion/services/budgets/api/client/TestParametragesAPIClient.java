package com.terrier.finances.gestion.services.budgets.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.test.config.AbstractTestsClientAPI;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;


/**
 * @author vzwingma
 *
 */
//RunWith(SpringRunner.class)
//ExtendWith(SpringExtension.class)
//ContextConfiguration(classes={ParametragesAPIClient.class})
public class TestParametragesAPIClient extends AbstractTestsClientAPI {

	@Autowired
	private ParametragesAPIClient client;
	
	//Test
	void testCallGetCategories() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {
		CategorieOperation c = new CategorieOperation();
		c.setId("1");
		c.setActif(true);
		c.setLibelle("1");
		ObjectMapper mapper = new ObjectMapper();
		
		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
					.setBody("["+mapper.writerFor(CategorieOperation.class).writeValueAsString(c)+"]")
				);
		
		List<CategorieOperation> listeCategories = client.getCategories();
		assertNotNull(listeCategories);
		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/parametres/v1/categories", recordedRequest.getPath());
	}
	
	
	
	//Test
	void testCallGetCategorie() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {
		CategorieOperation c = new CategorieOperation();
		c.setId("1");
		c.setActif(true);
		c.setLibelle("1");
		ObjectMapper mapper = new ObjectMapper();
		String outS = mapper.writerFor(CategorieOperation.class).writeValueAsString(c);
		
		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
					.setBody("["+outS.toString()+"]")
				);
		
		CategorieOperation cResult = client.getCategorieParId("1");
		assertNotNull(cResult);
		assertEquals("1", cResult.getLibelle());
		
		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/parametres/v1/categories", recordedRequest.getPath());
	}



	@Override
	public int getServerPort() {
		return 8091;
	}
}
	