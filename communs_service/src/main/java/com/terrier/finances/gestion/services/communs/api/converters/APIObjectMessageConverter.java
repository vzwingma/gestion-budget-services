package com.terrier.finances.gestion.services.communs.api.converters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;

/**
 * Message Converter pour les échanges entre l'IHM et les services via API
 * @author vzwingma
 *
 * @param <T> modèle d'objet métier
 */
public class APIObjectMessageConverter<T extends AbstractAPIObjectModel> implements HttpMessageConverter<T> {

	private ObjectMapper mapper;

	public APIObjectMessageConverter() {
		JsonFactory factory = new JsonFactory();
		factory.setCharacterEscapes(new JsonpCharacterEscapes());
		mapper = new ObjectMapper(factory).deactivateDefaultTyping();
	}


	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canRead(java.lang.Class, org.springframework.http.MediaType)
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return isAbstractAPIObjectModel(clazz, mediaType);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canWrite(java.lang.Class, org.springframework.http.MediaType)
	 */
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return isAbstractAPIObjectModel(clazz, mediaType);

	}

	private boolean isAbstractAPIObjectModel(Class<?> clazz, MediaType mediaType){
		return MediaType.APPLICATION_JSON.equals(mediaType) && AbstractAPIObjectModel.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#getSupportedMediaTypes()
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_JSON);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#read(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	public T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException {
		return mapper.readValue(inputMessage.getBody(), clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#write(java.lang.Object, org.springframework.http.MediaType, org.springframework.http.HttpOutputMessage)
	 */
	@Override
	public void write(T t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException {
		mapper.writeValue(outputMessage.getBody(), t);
	}
}
