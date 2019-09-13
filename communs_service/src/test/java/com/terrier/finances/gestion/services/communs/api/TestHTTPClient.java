/**
 * 
 */
package com.terrier.finances.gestion.services.communs.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.interceptors.LogApiFilter;
import com.terrier.finances.gestion.test.config.AbstractTestsClientAPI;
import com.terrier.finances.gestion.test.config.MockApiClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author vzwingma
 *
 */
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={MockApiClient.class, LogApiFilter.class})
public class TestHTTPClient extends AbstractTestsClientAPI {




	@Test
	void testCallGet() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {

		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
				);
		
		boolean response = getTestClient().callHTTPGet("/get");
		assertTrue(response);

		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/get", recordedRequest.getPath());
	}

	@Test
	void testCallGetData() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException {
		
		getMockWebServer().enqueue(
				new MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setBody("{\"login\":\"Test\",\"motDePasse\":\"Test\"}")
				);
		
		AuthLoginAPIObject response = getTestClient().callHTTPGetData("/getData", AuthLoginAPIObject.class);
		assertNotNull(response);
		assertEquals("Test", response.getLogin());
		assertEquals("Test", response.getMotDePasse());		

		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/getData", recordedRequest.getPath());
	}
}