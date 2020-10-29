package com.terrier.finances.gestion.services.communs.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.AbstractHTTPReactiveClient;
import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;

import reactor.core.publisher.Mono;

/**
 * Classe d'un client HTTP
 * @author vzwingma
 *
 */
public abstract class AbstractHTTPClient<R extends AbstractAPIObjectModel> extends AbstractHTTPReactiveClient {


	protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractHTTPClient.class );


	/**
	 * Constructeur
	 * @param responseClassType
	 */
	protected AbstractHTTPClient(Class<R> responseClassType) {
		this.responseClassType = responseClassType;
	}
	
	protected Class<R> responseClassType;
	
	/**
	 * @return l'URI du µService
	 */
	public abstract ApiUrlConfigEnum getConfigServiceURI();
	
	
	/**
	 * @param path chemin
	 * @param pathParams paramètres
	 * @return données en réponse
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected Mono<R> callHTTPGetData(String path, Map<String, String> pathParams) throws DataNotFoundException{
		return callAPIandReturnMono(HttpMethod.GET, path, pathParams, null, null, responseClassType);
	}
	/**
	 * @param path chemin
	 * @return données en réponse
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected Mono<List<R>> callHTTPGetListData(String path) throws DataNotFoundException{
		return callAPIandReturnFlux(HttpMethod.GET, path, null, null, null, responseClassType).collectList();
	}
	

	@Override
	public String getCorrId() {
		// Pas de surcharge du CorrId. Transmis par HTTP Header
		return null;
	}
	
	
	/**
	 * L'appel initial a été authentifié. Réutilisation du token pour les appels sous jacent
	 */
	@Override
	public String getAccessToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2Authentication && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
		    return ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
		}
		return null;
	}
}
