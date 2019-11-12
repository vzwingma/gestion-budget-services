package com.terrier.finances.gestion.services.communs.api.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import com.terrier.finances.gestion.communs.api.security.ApiConfigEnum;

import reactor.core.publisher.Mono;

/**
 * Logger des API
 * @author vzwingma
 *
 */
@Component
public class CallAPIInterceptor implements ExchangeFilterFunction {

	

	public static final Logger LOGGER = LoggerFactory.getLogger( CallAPIInterceptor.class );
	


	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		

		String apiCorrID = request.headers().getValuesAsList(ApiConfigEnum.HEADER_API_CORRELATION_ID).stream().findAny().orElse("?");
		LOGGER.info("[API={}] {} :: {}", apiCorrID, request.method(), request.url());
		if (HttpMethod.POST.equals(request.method()) || HttpMethod.PUT.equals(request.method())) {
			LOGGER.debug("Contenu envoyé \n [{}]", request.body());
		}
		Mono<ClientResponse> response = next.exchange(request);
		response.doAfterSuccessOrError((r, e) -> {
			LOGGER.info("Statut HTTP : [{}]", r.statusCode());
		});
		
		return response;
	}

}