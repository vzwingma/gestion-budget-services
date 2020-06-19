/**
 * 
 */
package com.terrier.finances.gestion.services.communs.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
class TestHTTPClient extends AbstractTestsClientAPI {

	@Test
	void testCallGet() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException, IOException {

		getMockWebServer().enqueue(
				new MockResponse()
					.setResponseCode(200)
				);
		
		getTestClient().callHTTPGetData("/get", null)
				.doOnError(e -> fail())
				.block();
		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/get", recordedRequest.getPath());
	}
	
	

	@Test
	void testCallGetError() throws InterruptedException {

		getMockWebServer()
				.enqueue(new MockResponse().setResponseCode(404));

		assertThrows(Exception.class, () 
				-> getTestClient().callHTTPGetData("/get", null)
					.doOnSuccess(s -> fail())
					.block());

		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/get", recordedRequest.getPath());
	}

	@Test
	void testCallGetData() throws InterruptedException, UserNotAuthorizedException, DataNotFoundException {
		
		getMockWebServer().enqueue(
				new MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setBody("{\"idUtilisateur\":\"Test\"}")
				);
		
		UtilisateurPrefsAPIObject response = getTestClient().callHTTPGetData("/getData", null).block();
		assertNotNull(response);
		assertEquals("Test", response.getIdUtilisateur());

		RecordedRequest recordedRequest = getMockWebServer().takeRequest();
		assertEquals("/getData", recordedRequest.getPath());
	}



	@Override
	public int getServerPort() {
		return 8092;
	}
}