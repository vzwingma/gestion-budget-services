package com.terrier.finances.gestion.services.budgets.test.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.services.budgets.spi.ComptesAPIClient;
import com.terrier.finances.gestion.services.budgets.spi.ComptesServiceHTTPAdaptator;
import com.terrier.finances.gestion.services.communs.api.AbstractTestsClientAPI;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={ ComptesServiceHTTPAdaptator.class})
class TestComptesHTTPAdaptator extends AbstractTestsClientAPI {

	
	@Autowired
	private ComptesServiceHTTPAdaptator compteClient;
	
	
	@Test
	void testGetCompte() throws JsonProcessingException, InterruptedException {
		
		CompteBancaire compte = new CompteBancaire();
		compte.setId("c1");
		
		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
					.setBody(new ObjectMapper().writerFor(CompteBancaire.class).writeValueAsString(compte))
				);
		
		CompteBancaire compteResult = compteClient.getCompteById("c1");
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
