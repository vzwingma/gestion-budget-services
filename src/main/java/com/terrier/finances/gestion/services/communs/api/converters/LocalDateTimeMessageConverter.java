package com.terrier.finances.gestion.services.communs.api.converters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Message Converter pour les échanges entre l'IHM et les services via API
 * @author vzwingma
 */
public class LocalDateTimeMessageConverter implements HttpMessageConverter<LocalDateTime> {
	
	
	private ObjectMapper mapper = new ObjectMapper();

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canRead(java.lang.Class, org.springframework.http.MediaType)
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(LocalDateTime.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canWrite(java.lang.Class, org.springframework.http.MediaType)
	 */
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(LocalDateTime.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#getSupportedMediaTypes()
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#read(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	public LocalDateTime read(Class<? extends LocalDateTime> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return mapper.readValue(inputMessage.getBody(), clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#write(java.lang.Object, org.springframework.http.MediaType, org.springframework.http.HttpOutputMessage)
	 */
	@Override
	public void write(LocalDateTime t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		mapper.writeValue(outputMessage.getBody(), t);
	}
	
}
