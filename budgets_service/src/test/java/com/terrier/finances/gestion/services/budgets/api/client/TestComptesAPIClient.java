package com.terrier.finances.gestion.services.budgets.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.test.config.AbstractTestsClientAPI;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author vzwingma
 *
 */
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={ ComptesAPIClient.class})
public class TestComptesAPIClient extends AbstractTestsClientAPI {

	
	@Autowired
	private ComptesAPIClient compteClient;
	
	
	@Test
	public void testGetCompte() throws JsonProcessingException, InterruptedException {
		
		CompteBancaire compte = new CompteBancaire();
		compte.setId("c1");
		
		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
					.setBody(new ObjectMapper().writerFor(CompteBancaire.class).writeValueAsString(compte))
				);
		
		CompteBancaire compteResult = compteClient.getCompteById("c1", "null");
		assertNotNull(compteResult);
		assertEquals("c1", compteResult.getId());
		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/comptes/v1/c1", recordedRequest.getPath());
	}


	@Override
	public int getServerPort() {
		return 8092;
	}
}