package com.terrier.finances.gestion.services.communs.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.security.ApiConfigEnum;
import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Classe abstraite d'un API Controller
 * @author vzwingma
 *
 */
public abstract class AbstractAPIController {
	

	/**
	 * Logger
	 */
	public final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	/**
	 * Constructeur des API Controleur
	 */
	public AbstractAPIController() {
		org.slf4j.MDC.put(ApiConfigEnum.HEADER_CORRELATION_ID, "");
	}
	
	@Autowired
	private UtilisateursService serviceUtilisateurs;
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
	

	/**
	 * Chargement de l'utilisateur
	 * @param token token JWT
	 * @return utilisateur si authentifié
	 * @throws UserNotAuthorizedException erreur d'auter
	 */
	public UserBusinessSession getUtilisateur(String token) throws UserNotAuthorizedException{
		UserBusinessSession userSession = null;
		try{
			String idUser = (String)JwtConfigEnum.getJWTClaims(token).get(JwtConfigEnum.JWT_CLAIM_HEADER_USERID);
			userSession = serviceUtilisateurs.getBusinessSession(idUser);
		}
		catch (Exception e) {
			logger.warn("Erreur lors du décodage du token [{}]", token, e);
		}
		if(userSession != null){
			return userSession;
		}
		throw new UserNotAuthorizedException("L'utilisateur n'est pas authentifié");
	}
}
