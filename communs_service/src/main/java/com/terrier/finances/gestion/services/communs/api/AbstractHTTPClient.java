/**
 * Client HTTP
 */
package com.terrier.finances.gestion.services.communs.api;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.security.ApiConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.interceptors.LogApiFilter;

import reactor.core.publisher.Mono;

/**
 * Classe d'un client HTTP
 * @author vzwingma
 *
 */
public abstract class AbstractHTTPClient {


	protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractHTTPClient.class );

	public static final String ACCEPT_CHARSET_HEADER_NAME = "accept-charset";
	public static final String ACCEPT_HEADER_NAME = "Accept";
	public static final String CONTENT_TYPE_HEADER_NAME = "Content-type";

	// Charset utilisé dans les fichiers XML
	private static final String ACCEPT_CHARSET = "UTF-8";

	// Nombre d'essais lors d'un premier échec lors de l'appel à un service externe
	protected final int nbEssais = 2;


	@Autowired
	private LogApiFilter logFilter;

	private WebClient client;

	@PostConstruct
	public void createWebClient() throws NoSuchAlgorithmException, KeyManagementException {

		// Register des converters
		//		clientConfig.register(new ListAPIObjectModelReader<AbstractAPIObjectModel>());
		//		clientConfig.register(new APIObjectModelReader<AbstractAPIObjectModel>());

		// TODO : Ajouter ClientHttpRequestFactory
		// Install the all-trusting trust manager
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null,  null, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());

		LOGGER.info("Création du Client : {}", getBaseURL());
		this.client = WebClient.builder()
				.baseUrl(getBaseURL())
				// Headers
				.defaultHeaders(createHeaders())
				// Log Filter
				.filter(logFilter)

				.build();
	}

	/**
	 * @return l'URI du µService
	 */
	public abstract String getBaseURL();

	/**
	 * Crée les headers HTTP
	 *
	 * @return {@link org.apache.http.HttpHeaders} headers
	 */
	protected Consumer<HttpHeaders> createHeaders() {
		return (headers) -> {
			headers.add(ACCEPT_CHARSET_HEADER_NAME, ACCEPT_CHARSET);
			headers.add(ACCEPT_HEADER_NAME, MediaType.APPLICATION_JSON_VALUE);
			headers.add(CONTENT_TYPE_HEADER_NAME, MediaType.APPLICATION_JSON_UTF8_VALUE);

			// Correlation ID
			String corrID = UUID.randomUUID().toString();
			org.slf4j.MDC.put(ApiConfigEnum.HEADER_CORRELATION_ID, "["+ApiConfigEnum.LOG_CORRELATION_ID+"="+corrID+"]");
			headers.add(ApiConfigEnum.HEADER_CORRELATION_ID, corrID);

			String apiCorrID = UUID.randomUUID().toString();
			org.slf4j.MDC.put(ApiConfigEnum.HEADER_API_CORRELATION_ID, "[API="+apiCorrID+"]");
			headers.add(ApiConfigEnum.HEADER_API_CORRELATION_ID, apiCorrID);

			//		if(getJwtToken() != null){
			//			headers.add(JwtConfigEnum.JWT_HEADER_AUTH, getJwtToken());
			//			LOGGER.debug("[JWT Token={}]", getJwtToken());
			//		}

		};
	}


	/**
	 * @param <T>            Classe de l'objet attendu
	 * @param ressource      ressource
	 * @param methode        méthode HTTP utilisée
	 * @param classeAttendue classe attendue
	 * @param entity         entité à transmettre
	 * @return réponse
	 * @throws URISyntaxException  erreur de syntaxe
	 * @throws ServiceExterneException erreur de service
	 */
	protected <T> ResponseEntity<T> callAndRetry(final String ressource, final HttpMethod methode,
			final Class<T> classeAttendue, final HttpEntity<String> entity) throws URISyntaxException {
		int nbAppels = 0;
		Exception e = null;

		while (nbAppels < this.nbEssais) {
			//            PoolStats poolStats = this.connectionManager.getTotalStats();

			//            LOG.debug("[Interface] [{}] Connexions : utilisées={}, louées={}, en attente={}, maximum={}", codeAppel,
			//                    poolStats.getAvailable(), poolStats.getLeased(), poolStats.getPending(), poolStats.getMax());
			LOGGER.info("Appel n°{}", nbAppels + 1);

			try {
				Mono<ClientResponse> exchangeResponse = client.method(methode).uri(ressource, new HashMap<>()).exchange();
				ClientResponse response = exchangeResponse.block();

				LOGGER.info("Réponse [{}] : [{}] ms", response.statusCode(), "TODO");
				return response.toEntity(classeAttendue).block();
			} catch (ResourceAccessException | HttpStatusCodeException | UnknownHttpStatusCodeException ex) {
				if (HttpMethod.POST.equals(methode) || HttpMethod.PUT.equals(methode)) {
					//                    msg.append(CONTENU_MSG).append(entity != null ? entity.getBody() : "null").append("]");
				}
				LOGGER.error("Réponse [{}] : [{}] ms", 	ex.getStackTrace(), "TODO");
				e = ex;
				nbAppels++;
			}
			try {
				int wait = 1;
				LOGGER.debug(" Attente de {}s avant l'appel suivant", wait);
				Thread.sleep((long) wait * 1000);
			} catch (InterruptedException ie) {
				LOGGER.error("Erreur lors de l'attente avant rejeu");
				Thread.currentThread().interrupt();
			}
		}
		return null;
	}


	//
	//	/**
	//	 * Appel POST vers les API Services
	//	 * @param path chemin
	//	 * @param params paramètres
	//	 * @param dataToSend body à envoyer
	//	 * @param responseClassType réponse type
	//	 * @return réponse
	//	 * @throws UserNotAuthorizedException  erreur d'authentification
	//	 * @throws DataNotFoundException  erreur lors de l'appel
	//	 */
	//	protected <Q extends AbstractAPIObjectModel> Response callHTTPPost(String path, Q dataToSend) throws UserNotAuthorizedException, DataNotFoundException {
	//		if(path != null){
	//			try{
	//				Response response = getInvocation(path).post(getEntity(dataToSend));
	//				LOGGER.debug("Réponse : {}", response);
	//				return response;
	//			}
	//			catch(Exception e){
	//				catchWebApplicationException(HttpMethod.POST, e);
	//			}
	//		}
	//		return null;
	//	}
	//
	//	/**
	//	 * Appel POST vers les API Services
	//	 * @param path chemin
	//	 * @param dataToSend body à envoyer
	//	 * @param responseClassType réponse type
	//	 * @return réponse
	//	 * @throws UserNotAuthorizedException  erreur d'authentification
	//	 * @throws DataNotFoundException  erreur lors de l'appel
	//	 */
	//	protected  <Q extends AbstractAPIObjectModel, R extends AbstractAPIObjectModel>
	//	R callHTTPPost(String path, Q dataToSend, Class<R> responseClassType) throws UserNotAuthorizedException, DataNotFoundException{
	//		return callHTTPPost(path, null, dataToSend, responseClassType);
	//	}
	//
	//
	//	/**
	//	 * Appel POST vers les API Services
	//	 * @param path chemin
	//	 * @param params paramètres
	//	 * @param dataToSend body à envoyer
	//	 * @param responseClassType réponse type
	//	 * @return réponse
	//	 * @throws UserNotAuthorizedException 
	//	 * @throws DataNotFoundException 
	//	 */
	//	protected <Q extends AbstractAPIObjectModel, R extends AbstractAPIObjectModel> 
	//	R callHTTPPost(String path, Map<String, String> params, Q dataToSend, Class<R> responseClassType) throws UserNotAuthorizedException, DataNotFoundException{
	//		if(path != null){
	//			try{
	//				R response = getInvocation(path, params).post(getEntity(dataToSend), responseClassType);
	//				LOGGER.debug("Réponse : {}", response);
	//				return response;
	//			}
	//			catch(Exception e){
	//				catchWebApplicationException(HttpMethod.POST, e);
	//			}
	//		}
	//		return null;
	//	}
	/**
	 * Appel HTTP GET
	 * @param path paramètres de l'URL
	 * @return résultat de l'appel
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected boolean callHTTPGet(String path) throws UserNotAuthorizedException, DataNotFoundException{
		return callHTTPGet(path, null);
	}


	/**
	 * Appel HTTP GET
	 * @param params params
	 * @param path paramètres de l'URL
	 * @return résultat de l'appel
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected boolean callHTTPGet(String ressource, Map<String, String> params) throws UserNotAuthorizedException, DataNotFoundException{
		try{
			ResponseEntity<Boolean> response = callAndRetry(ressource, HttpMethod.GET, Boolean.class, null);
			return response.getStatusCode().is2xxSuccessful();
		}
		catch(Exception e){
			catchWebApplicationException(HttpMethod.GET, e);
		}
		return false;
	}

	/**
	 * Appel HTTP GET
	 * @param path chemin
	 * @param responseClassType type de la réponse
	 * @return résultat
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected <R extends AbstractAPIObjectModel> R callHTTPGetData(String path, Class<R> responseClassType) throws UserNotAuthorizedException, DataNotFoundException{
		return callHTTPGetData(path, null, responseClassType);
	}
	/**
	 * Appel HTTP GET
	 * @param path Ressource 
	 * @param params paramètres de l'URL (à part pour ne pas les tracer)
	 * @return résultat de l'appel
	 * @throws UserNotAuthorizedException  erreur d'authentification
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	protected <R extends AbstractAPIObjectModel> R callHTTPGetData(final String ressource, final Map<String, String> params, final Class<R> responseClassType) throws UserNotAuthorizedException, DataNotFoundException{
		try{
			ResponseEntity<R> response = callAndRetry(ressource, HttpMethod.GET, responseClassType, null);
			if (response != null) {
				LOGGER.debug("Réponse : [{}]", response.getStatusCode());
				if (response.getStatusCode().equals(HttpStatus.OK)) {
					return response.getBody();
				}
				// Si code 201 HttpStatus.NO_CONTENT - Pas de body dans la réponse
			}
		}
		catch(Exception e){
			catchWebApplicationException(HttpMethod.GET, e);
		}
		return null;
	}

	//
	//	/**
	//	 * Appel DELETE
	//	 * @param path racine de l'URL
	//	 * @param responseClassType reponse
	//	 * @return résultat de l'appel
	//	 */
	//	protected <R extends AbstractAPIObjectModel> R callHTTPDeleteData(String path, Class<R> responseClassType) throws UserNotAuthorizedException, DataNotFoundException{
	//		if(path != null){
	//			try{
	//				R response = getInvocation(path).delete(responseClassType);
	//				LOGGER.debug("Réponse : [{}]", response);
	//				return response;
	//			}
	//			catch(Exception e){
	//				catchWebApplicationException(HttpMethod.DELETE, e);
	//			}
	//		}
	//		return null;
	//	}
	//
	//
	//	/**
	//	 * Appel HTTP GET List
	//	 * @param path racine de l'URL
	//	 * @return résultat de l'appel
	//	 * @throws UserNotAuthorizedException  erreur d'authentification
	//	 * @throws DataNotFoundException  erreur lors de l'appel
	//	 */
	//	protected <R extends AbstractAPIObjectModel> List<R> callHTTPGetListData(String path) throws UserNotAuthorizedException, DataNotFoundException{
	//		if(path != null){
	//			try{
	//				@SuppressWarnings("unchecked")
	//				List<R> response = getInvocation(path).get(List.class);
	//				LOGGER.debug("Réponse : [{}]", response);
	//				return response;
	//			}
	//			catch(Exception e){
	//				catchWebApplicationException(HttpMethod.GET, e);
	//			}
	//		}
	//		return new ArrayList<>();
	//	}



	/**
	 * Catch 401 error
	 * @param c code API
	 * @param e Exception
	 * @throws UserNotAuthorizedException utilisateur non authentifié
	 * @throws DataNotFoundException  erreur lors de l'appel
	 */
	private void catchWebApplicationException(HttpMethod verbe,  Exception ex) throws UserNotAuthorizedException, DataNotFoundException {
		//		if(ex instanceof WebApplicationException) {
		//			WebApplicationException e = (WebApplicationException)ex;
		//			LOGGER.error("[{}] Erreur [{}] lors de l'appel ", verbe, e.getResponse().getStatus());
		//			if(e.getResponse().getStatusInfo().equals(Status.UNAUTHORIZED)) {
		//				throw new UserNotAuthorizedException("Utilisateur non authentifié");
		//			}
		//			else if(Status.INTERNAL_SERVER_ERROR.equals(e.getResponse().getStatusInfo()) || Status.BAD_REQUEST.equals(e.getResponse().getStatusInfo())) {
		//				throw new DataNotFoundException("Erreur lors de l'appel au service");
		//			}
		//		}
		//		else {
		LOGGER.error("[{}] Erreur lors de l'appel", verbe, ex);
		//		}
	}
}
