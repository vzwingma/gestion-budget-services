package com.terrier.finances.gestion.communs.api;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.api.security.ApiHeaderIdEnum;
import com.terrier.finances.gestion.communs.utils.config.CorrelationsIdUtils;
import com.terrier.finances.gestion.communs.utils.config.EnvVarConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Classe d'un client HTTP
 * @author vzwingma
 *
 */
public abstract class AbstractHTTPReactiveClient{


	protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractHTTPReactiveClient.class );


	protected final String serviceURI;

	@Autowired
	private ExchangeFilterFunction outcomingRequestFilter;

	/*
	 *  CorrelationId
	 */
	private String correlationId;

	public AbstractHTTPReactiveClient() {
		serviceURI = EnvVarConfigUtils.getStringEnvVar(getConfigServiceURI());
	}

	public abstract ApiUrlConfigEnum getConfigServiceURI();


	/**
	 * Créé un client HTTP 
	 * (dans une méthode séparée pour pouvoir être mocké facilement)
	 * @return client HTTP
	 */
	protected WebClient getClient() {

		try {
			// Install the all-trusting trust manager	
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null,  null, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());

			/**
			SslContext sslContext = SslContextBuilder
					.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
				HttpClient httpClient = HttpClient.create()
					.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
				ClientHttpConnector sslConnector = new ReactorClientHttpConnector(httpClient);
			 **/			
			return WebClient.builder()
					//					.clientConnector(sslConnector)
					.filter(outcomingRequestFilter)
					.baseUrl(serviceURI)
					.build();
		}
		catch(Exception e){
			LOGGER.error("Erreur envoi : Erreur lors de la création du Client HTTP {}", e.getMessage());
			return WebClient.builder().baseUrl(serviceURI).build();
		}
	}

	/**
	 * 
	 * @return CorrelationId
	 */
	public abstract String getCorrId();
	/**
	 * Spec du client
	 * @param method méthodes HTTP
	 * @param path chemin à appeler
	 * @param pathParams paramètres du path (à remplacer dans l'URL)
	 * @param queryParams paramètres de requêtes (si existants)
	 * @return Config
	 */
	private RequestBodySpec getInvocation(HttpMethod method, String path, 
			Map<String, String> pathParams,
			Map<String, String> queryParams){

		// Correlation ID
		String corrID = getCorrId() != null ? getCorrId() : this.correlationId != null ? this.correlationId : UUID.randomUUID().toString();

		final Map<String, String> paramsPath = pathParams != null ? pathParams : new HashMap<>();

		final MultiValueMap<String, String> queryMVParams = new LinkedMultiValueMap<>();
		if(queryParams != null) {
			queryParams.forEach(queryMVParams::add);
		}

		LOGGER.trace("[URL={}][pathParams={}][queryParams={}]", path, pathParams, queryParams);

		RequestBodySpec spec = getClient()
				.method(method)
				// URI & Params
				.uri(uriBuilder -> uriBuilder
						.path(path)
						.queryParams(queryMVParams)
						.build(paramsPath))
				// Tout est en JSON
				.accept(MediaType.APPLICATION_JSON)	   
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(ApiHeaderIdEnum.HEADER_CORRELATION_ID, corrID)
				.acceptCharset(StandardCharsets.UTF_8);

		CorrelationsIdUtils.putCorrIdOnMDC(corrID);

		if(getAccessToken() != null){
			spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());
			LOGGER.trace("[Token={}]", getAccessToken());
		}
		// API Correlation ID
		String apiCorrID = UUID.randomUUID().toString();
		spec = spec.header(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID, apiCorrID);		
		return spec;
	}

	public abstract String getAccessToken();


	/**
	 * @param <R> Type de la réponse
	 * @param method méthode HTTP
	 * @param path chemin
	 * @param pathParams paramètres de l'URL
	 * @param queryParams paramètres de requête
	 * @param apiBodyObject contenu de la requête
	 * @return données en réponse
	 */
	protected <Q extends AbstractAPIObjectModel, R extends AbstractAPIObjectModel>
	Mono<R> callAPIandReturnResponse(HttpMethod method, String path, Map<String, String> pathParams, Map<String, String> queryParams, Q apiBodyObject, Class<R> apiBodyResponse) {
		Mono<R> bodyResponse = Mono.empty();
		if(path == null) {
			catchWebApplicationException(method, new Exception("Le path ne peut pas être null"));
		}

		try {
			RequestBodySpec spec = getInvocation(method, path, pathParams, queryParams);
			if(apiBodyObject != null) {
				LOGGER.info("BodyObject : {}", apiBodyObject);
				bodyResponse = spec.bodyValue(apiBodyObject)
						.exchangeToMono(callResponse -> getResponseBody(callResponse, apiBodyResponse))
						.doOnError(e -> catchWebApplicationException(method, e ))
						.onErrorStop();
			}
			else {
				bodyResponse = spec
						.exchangeToMono(callResponse -> getResponseBody(callResponse, apiBodyResponse))
						.doOnError(e -> catchWebApplicationException(method, e ))
						.onErrorStop();
			}
		}
		catch (Exception e) {
			catchWebApplicationException(method, e);
		}
		CorrelationsIdUtils.clearApiIdOnMDC();
		return bodyResponse;
	}


	/**
	 * @param <R> Type de la réponse
	 * @param method méthode HTTP
	 * @param path chemin
	 * @return données en réponse
	 */
	protected <Q extends AbstractAPIObjectModel, R extends AbstractAPIObjectModel>
	Flux<R> callAPIandReturnResponses(HttpMethod method, String path, Class<R> apiBodyResponse) {
		Flux<R> bodyResponse = Flux.empty();
		if(path == null) {
			catchWebApplicationException(method, new Exception("Le path ne peut pas être null"));
		}

		try {
			RequestBodySpec spec = getInvocation(method, path, null, null);
			bodyResponse = spec
					.exchangeToFlux(callResponse -> getResponsesBody(callResponse, apiBodyResponse))
					.doOnError(e -> catchWebApplicationException(method, e ));
		}
		catch (Exception e) {
			catchWebApplicationException(method, e);
		}
		CorrelationsIdUtils.clearApiIdOnMDC();
		return bodyResponse;
	}


	/**
	 * @param <Q> Type de la réponse
	 * @param method méthode HTTP
	 * @param path chemin
	 * @param pathParams paramètres de l'URL
	 * @param queryParams paramètres de requête
	 * @param apiBodyObject contenu de la requête
	 * @return données en réponse
	 */
	protected <Q extends AbstractAPIObjectModel>
	HttpStatus callAPIandReturnStatus(HttpMethod method, String path, Map<String, String> pathParams, Map<String, String> queryParams, Q apiBodyObject) {
		if(path == null) {
			catchWebApplicationException(method, new Exception("Le path ne peut pas être null"));
			return HttpStatus.BAD_REQUEST;
		}
		HttpStatus statutResponse = HttpStatus.NOT_FOUND;
		try {
			RequestBodySpec spec = getInvocation(method, path, pathParams, queryParams);
			if(apiBodyObject != null) {
				LOGGER.info("BodyObject : {}", apiBodyObject);
				statutResponse = spec.bodyValue(apiBodyObject)
						.exchangeToMono(callResponse -> Mono.just(callResponse.statusCode()))
						.doOnError(e -> catchWebApplicationException(method, e ))
						.block();
			}
			else {
				statutResponse = spec
						.exchangeToMono(callResponse -> Mono.just(callResponse.statusCode()))
						.doOnError(e -> catchWebApplicationException(method, e ))
						.block();
			}
			if(statutResponse.is2xxSuccessful()){
				LOGGER.info("Statut HTTP : [{}]", statutResponse.value());
			}
			else{
				LOGGER.error("Statut HTTP : [{}]", statutResponse.value());
			}
		}
		catch (Exception e) {
			catchWebApplicationException(method, e);
		}
		CorrelationsIdUtils.clearApiIdOnMDC();
		return statutResponse;
	}


	/**
	 * Response body
	 * @param callResponse client response
	 * @param apiBodyResponse classe de la réponse
	 * @param <R> Classe de la réponse
	 * @return body response
	 */
	private <R extends AbstractAPIObjectModel> Mono<R> getResponseBody(ClientResponse callResponse, Class<R> apiBodyResponse){
		if(callResponse.statusCode().is2xxSuccessful()){
			LOGGER.info("Statut HTTP : [{}]", callResponse.rawStatusCode());
			return callResponse.bodyToMono(apiBodyResponse);
		}
		else{
			LOGGER.error("Statut HTTP : [{}]", callResponse.rawStatusCode());
			return Mono.empty();
		}
	}


	/**
	 * Response body
	 * @param callResponse client response
	 * @param apiBodyResponse classe de la réponse
	 * @param <R> Classe de la réponse
	 * @return body response
	 */
	private <R extends AbstractAPIObjectModel> Flux<R> getResponsesBody(ClientResponse callResponse, Class<R> apiBodyResponse){
		if(callResponse.statusCode().is2xxSuccessful()){
			LOGGER.info("Statut HTTP : [{}]", callResponse.rawStatusCode());
			return callResponse.bodyToFlux(apiBodyResponse);
		}
		else{
			LOGGER.error("Statut HTTP : [{}]", callResponse.rawStatusCode());
			return Flux.empty();
		}
	}
	/**
	 * Catch HTTP error
	 * @param verbe verbe HTTP
	 * @param ex Exception
	 */
	private void catchWebApplicationException(HttpMethod verbe, Throwable ex) {
		LOGGER.error("[{}] Erreur lors de l'appel : {}", verbe, ex.getMessage());
		LOGGER.debug("[{}] Détails :", verbe, ex);
	}

	/**
	 * Surcharge du CorrId
	 * @param correlationId id de corrélation
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
}
