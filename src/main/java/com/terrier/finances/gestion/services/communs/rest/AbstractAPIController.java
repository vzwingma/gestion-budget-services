package com.terrier.finances.gestion.services.communs.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.terrier.finances.gestion.communs.abstrait.AbstractRestObjectModel;

/**
 * Classe abstraite d'un API Controller
 * @author vzwingma
 *
 */
public abstract class AbstractAPIController {
	
	
	
	protected <T extends AbstractRestObjectModel> ResponseEntity<T> getEntity(T restObjectModel){
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	    return new ResponseEntity<T>(restObjectModel, httpHeaders, HttpStatus.OK);
	}
}
