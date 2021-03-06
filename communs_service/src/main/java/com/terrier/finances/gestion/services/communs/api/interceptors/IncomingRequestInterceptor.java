package com.terrier.finances.gestion.services.communs.api.interceptors;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.terrier.finances.gestion.communs.api.security.ApiHeaderIdEnum;
import com.terrier.finances.gestion.communs.utils.config.CorrelationsIdUtils;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;

/**
 * Request inteceptor pour charger le correlationID et le userSession
 * @author vzwingma
 *
 */
@Component
public class IncomingRequestInterceptor extends HandlerInterceptorAdapter {



	public static final Logger LOGGER = LoggerFactory.getLogger( IncomingRequestInterceptor.class );

	private static final String UNKNOWN_USER = "?";

	private static final String GUID_REGEX = "[0-9a-fA-F-]{36}";
	/**
	 * Traite les entêtes en entrée
	 * @param request requete 
	 * @param controller controleur IHM
	 */
	public void manageHeaders(HttpServletRequest request, AbstractAPIController controller) {

		// Logger des CorrId
		final String corrIdHeader = request.getHeader(ApiHeaderIdEnum.HEADER_CORRELATION_ID) != null ? request.getHeader(ApiHeaderIdEnum.HEADER_CORRELATION_ID) : UUID.randomUUID().toString();
		request.setAttribute(ApiHeaderIdEnum.HEADER_CORRELATION_ID, corrIdHeader);
		CorrelationsIdUtils.putCorrIdOnMDC(corrIdHeader);

		// Injection de la session User à partir du token OpenIdConnect
		String idUser = UNKNOWN_USER;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2Authentication) {
		    idUser = (String)((OAuth2Authentication)authentication).getPrincipal();
		}

		// Log API CorrId
		final String corrIdAPIHeader = request.getHeader(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID) != null ? request.getHeader(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID) : UUID.randomUUID().toString();
		request.setAttribute(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID, corrIdAPIHeader);
		CorrelationsIdUtils.putApiIdOnMDC(corrIdAPIHeader);
		LOGGER.info("[{} :: {}] [user={}]", request.getMethod(), request.getRequestURI(), idUser);

		// Injection des Token et CorrId dans la chaine
		if(controller != null 
				&& controller.getHTTPClients() != null 
				&& !controller.getHTTPClients().isEmpty()) {
			
			controller.getHTTPClients()
				.parallelStream()
				.forEach(c -> c.setCorrelationId(corrIdHeader));
		}
		CorrelationsIdUtils.clearApiIdOnMDC();
	}


	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Ajout des headers
		if(handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			if(handlerMethod.getBean() instanceof AbstractAPIController) {
				manageHeaders(request, ((AbstractAPIController)handlerMethod.getBean()));
			}
		}
		return true;
	}


	/**
	 * Retour de l'appel
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		String corrId =  request.getHeader(ApiHeaderIdEnum.HEADER_CORRELATION_ID) != null ? request.getHeader(ApiHeaderIdEnum.HEADER_CORRELATION_ID) :  (String)request.getAttribute(ApiHeaderIdEnum.HEADER_CORRELATION_ID);
		// Allow only GUID
		if (corrId != null && corrId.matches(GUID_REGEX)) {
			response.setHeader(ApiHeaderIdEnum.HEADER_CORRELATION_ID, corrId);
		}
		CorrelationsIdUtils.putCorrIdOnMDC(corrId);
		
		String corrIdApi =  request.getHeader(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID) != null ?  request.getHeader(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID) : (String)request.getAttribute(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID);
		// Allow only GUID
		if (corrIdApi != null && corrIdApi.matches(GUID_REGEX)) {
			response.setHeader(ApiHeaderIdEnum.HEADER_API_CORRELATION_ID, corrIdApi);
		}
		CorrelationsIdUtils.putApiIdOnMDC(corrIdApi);
		super.postHandle(request, response, handler, modelAndView);

		if(HttpStatus.Series.resolve(response.getStatus()) == Series.SUCCESSFUL) {
			LOGGER.info("Statut HTTP : [{}]", response.getStatus());
		}
		else {
			LOGGER.warn("Statut HTTP : [{}]", response.getStatus());
		}
		CorrelationsIdUtils.clearApiIdOnMDC();
	}
}
