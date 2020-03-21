/**
 * Client HTTP
 */
package com.terrier.finances.gestion.services.communs.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.AbstractHTTPReactiveClient;
import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;

import reactor.core.publisher.Mono;

/**
 * Classe d'un client HTTP
 * @author vzwingma
 *
 */
public abstract class AbstractHTTPClient<R extends AbstractAPIObjectModel> extends AbstractHTTPReactiveClient {


	protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractHTTPClient.class );

	// Token JWT
	private String jwtToken;

	/**
	 * Constructeur
	 * @param responseClassType
	 */
	public AbstractHTTPClient(Class<R> responseClassType) {
		this.responseClassType = responseClassType;
	}
	
	protected Class<R> responseClassType;
	
	/**
	 * @return l'URI du µService
	 */
	public abstract ApiUrlConfigEnum getConfigServiceURI();
	
	
	/**
	 * @param <R> classe de la réponse
	 * @param path chemin
	 * @param params paramètres
	 * @param responseClassType classe <R>
	 * @return données en réponse
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected Mono<R> callHTTPGetData(String path, Map<String, String> params) throws UserNotAuthorizedException, DataNotFoundException{
		return callAPIandReturnMono(HttpMethod.GET, path, params, null, responseClassType);
	}
	/**
	 * @param <R> classe de la réponse
	 * @param path chemin
	 * @param responseClassType classe <R>
	 * @return données en réponse
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected Mono<List<R>> callHTTPGetListData(String path) throws UserNotAuthorizedException, DataNotFoundException{
		return callAPIandReturnFlux(HttpMethod.GET, path, null, null, responseClassType).collectList();
	}
	/**
	 * Injecte le token JWT
	 * @param jwtToken
	 * @return le client avec le jwt token
	 */
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
	

	@Override
	public String getJwtToken() {
		return jwtToken;
	}
}
