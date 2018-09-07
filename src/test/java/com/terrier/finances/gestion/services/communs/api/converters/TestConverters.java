package com.terrier.finances.gestion.services.communs.api.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;

/**
 * Test de converters
 * @author vzwingma
 *
 */
public class TestConverters {

	@Test
	public void testConverterAPIObject() throws HttpMessageNotWritableException, IOException{
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		
		assertFalse(AuthLoginAPIObject.class.isAssignableFrom(AbstractAPIObjectModel.class));
		assertTrue(AbstractAPIObjectModel.class.isAssignableFrom(AuthLoginAPIObject.class));
		
		assertTrue(converter.canRead(AuthLoginAPIObject.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(AuthLoginAPIObject.class, MediaType.APPLICATION_JSON));

		assertFalse(converter.canRead(AuthLoginAPIObject.class, MediaType.APPLICATION_XML));
		assertFalse(converter.canWrite(AuthLoginAPIObject.class, MediaType.APPLICATION_XML));
		
		assertFalse(converter.canRead(List.class, MediaType.APPLICATION_JSON));
		assertFalse(converter.canWrite(List.class, MediaType.APPLICATION_JSON));

		AuthLoginAPIObject auth = new AuthLoginAPIObject("Test", "Test");
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(auth, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"login\":\"Test\",\"motDePasse\":\"Test\"}", out.getBody().toString());
		
		assertTrue(converter.canRead(CompteBancaire.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(CompteBancaire.class, MediaType.APPLICATION_JSON));
	}
}
