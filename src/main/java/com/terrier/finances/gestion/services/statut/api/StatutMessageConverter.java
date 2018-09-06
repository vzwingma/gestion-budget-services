package com.terrier.finances.gestion.services.statut.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.services.statut.model.StatutDependencyObject;

public class StatutMessageConverter implements HttpMessageConverter<StatutDependencyObject> {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return MediaType.APPLICATION_JSON.equals(mediaType);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return MediaType.APPLICATION_JSON.equals(mediaType);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_JSON);
	}

	@Override
	public StatutDependencyObject read(Class<? extends StatutDependencyObject> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return mapper.readValue(inputMessage.getBody(), StatutDependencyObject.class);
	}

	@Override
	public void write(StatutDependencyObject t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		mapper.writeValue(outputMessage.getBody(), t);
	}

}
