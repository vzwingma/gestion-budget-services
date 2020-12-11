package com.terrier.finances.gestion.services.communs.api.config;

import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
        LOGGER.error("Erreur : Budget non trouvé");
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(DataNotFoundException.class)
    public void handleDataNotFoundException() {
    	 LOGGER.error("Erreur : Données introuvables");
    }
    
    @ResponseStatus(HttpStatus.LOCKED)  // 423
    @ExceptionHandler(CompteClosedException.class)
    public void handleClosedException() {
    	 LOGGER.error("Erreur : Compte clos");
    }
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(UserNotAuthorizedException.class)
    public void handleUnauthorizedException() {
    	 LOGGER.error("Erreur : Accès non authentifié");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(HttpClientErrorException.class)
    public void handleOAuth2UnauthorizedException() {
        LOGGER.error("Erreur : Accès invalide");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)  // 500
    @ExceptionHandler(HttpServerErrorException.class)
    public void handleOAuth2ServerException() {
        LOGGER.error("Erreur : Accès impossible");
    }

}
