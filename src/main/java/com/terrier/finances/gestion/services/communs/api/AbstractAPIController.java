package com.terrier.finances.gestion.services.communs.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;

/**
 * Classe abstraite d'un API Controller
 * @author vzwingma
 *
 */
public abstract class AbstractAPIController {
	

	/**
	 * Logger
	 */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * @param restObjectModel 
	 * @return l'entity correspondante en JSON
	 */
	protected <T extends AbstractAPIObjectModel> ResponseEntity<T> getEntity(T restObjectModel){
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	    return new ResponseEntity<>(restObjectModel, httpHeaders, HttpStatus.OK);
	}
	
	
	/**
	 * @param restObjectModel 
	 * @return l'entity correspondante en JSON
	 */
	protected <M extends AbstractAPIObjectModel> ResponseEntity<List<M>> getEntities(List<M> restObjectModel){
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	    return new ResponseEntity<>(restObjectModel, httpHeaders, HttpStatus.OK);
	}
}
