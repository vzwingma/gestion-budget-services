package com.terrier.finances.gestion.business.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.terrier.finances.gestion.communs.utils.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exception.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exception.NotModifiedException;
import com.terrier.finances.gestion.communs.utils.exception.UserNotAuthorizedException;

/**
 * Gestion des exceptions dans le service REST
 * @author vzwingma
 *
 */
@ControllerAdvice
class GlobalControllerExceptionHandler {
	

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	
	
	
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(BudgetNotFoundException.class)
    public void handleBudgetNotFoundException() {
        LOGGER.error("Erreur Interne : Budget non trouvé");
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    @ExceptionHandler(DataNotFoundException.class)
    public void handleDataNotFoundException() {
    	 LOGGER.error("Erreur Interne : Données introuvables");
    }
    
    
    @ResponseStatus(HttpStatus.NOT_MODIFIED)  // 204
    @ExceptionHandler(NotModifiedException.class)
    public void handlNotModifiedException() {
    	 LOGGER.info("Non modifiée");
    }
    
    @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(UserNotAuthorizedException.class)
    public void handlException() {
    	 LOGGER.error("Erreur Interne : Accès non autorisé");
    }
    
}
