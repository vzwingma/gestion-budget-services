package com.terrier.finances.gestion.communs.api.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import com.terrier.finances.gestion.communs.api.security.ApiHeaderIdEnum;
import com.terrier.finances.gestion.communs.utils.config.CorrelationsIdUtils;

import reactor.core.publisher.Mono;

/**
 * Logger des appels d'API
 * @author vzwingma
 *
 */
@Service
public class OutcomingRequestFilter implements ExchangeFilterFunction {



	public static final Logger LOGGER = LoggerFactory.getLogger( OutcomingRequestFilter.class );

	/**
	 * Log de la requête
	 */
	@Override
	public Mono<ClientResponse> filter(ClientRequest requestContext, ExchangeFunction next) {

		String corrID = requestContext.headers().getValuesAsList(ApiHeaderIdEnum.HEADER_CORRELATION_ID).stream().findAny().orElse("?");
		String apiCorrID = requestContext.headers().getValuesAsList(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID).stream().findAny().orElse("?");
		CorrelationsIdUtils.putCorrIdsOnMDC(corrID, apiCorrID);
		LOGGER.info("{} :: {}", requestContext.method(), requestContext.url());
		return next.exchange(requestContext);
	}
}
