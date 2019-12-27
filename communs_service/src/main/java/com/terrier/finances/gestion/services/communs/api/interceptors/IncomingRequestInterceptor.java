package com.terrier.finances.gestion.services.communs.api.interceptors;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.terrier.finances.gestion.communs.api.security.ApiConfigEnum;
import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
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

	/**
	 * Traite les entêtes en entrée
	 * @param request requete
	 * @throws UserNotAuthorizedException utilisateur inconnu
	 */
	public void manageHeaders(HttpServletRequest request, AbstractAPIController controller) {

		// Logger des CorrId
		final String corrIdHeader = request.getHeader(ApiConfigEnum.HEADER_CORRELATION_ID) != null ? request.getHeader(ApiConfigEnum.HEADER_CORRELATION_ID) : UUID.randomUUID().toString();
		request.setAttribute(ApiConfigEnum.HEADER_CORRELATION_ID, corrIdHeader);
		org.slf4j.MDC.put(ApiConfigEnum.HEADER_CORRELATION_ID, new StringBuilder("[").append(ApiConfigEnum.LOG_CORRELATION_ID).append("=").append(corrIdHeader).append("]").toString());


		// Injection de la session User à partir du JWT
		String idUser = UNKNOWN_USER;
		final String jwtToken =  request.getHeader(JwtConfigEnum.JWT_HEADER_AUTH);
		if(jwtToken != null && JwtConfigEnum.getJWTClaims(jwtToken) != null) {
			idUser = (String)JwtConfigEnum.getJWTClaims(jwtToken).get(JwtConfigEnum.JWT_CLAIM_HEADER_USERID);
			request.setAttribute(AbstractAPIController.ID_USER, JwtConfigEnum.getJWTClaims(jwtToken).get(JwtConfigEnum.JWT_CLAIM_HEADER_USERID));
		}
		else {
			LOGGER.warn("[idUser={}] JwTToken introuvable ou incorrect. Impossible d'injecter la userSession. Utilisateur non authentifié", idUser);
		}

		// Log API CorrId
		final String corrIdAPIHeader = request.getHeader(ApiConfigEnum.HEADER_API_CORRELATION_ID) != null ? request.getHeader(ApiConfigEnum.HEADER_API_CORRELATION_ID) : UUID.randomUUID().toString();
		request.setAttribute(ApiConfigEnum.HEADER_API_CORRELATION_ID, corrIdAPIHeader);

		if(UNKNOWN_USER.equals(idUser)) {
			LOGGER.info("[API={}][{} :: {}] anonyme", corrIdAPIHeader, request.getMethod(), request.getRequestURI());
		}
		else {
			LOGGER.info("[API={}][{} :: {}] authentifiée [idUser={}]", corrIdAPIHeader, request.getMethod(), request.getRequestURI(), idUser);
		}

		// Injection des Token et CorrId dans la chaine
		if(controller != null 
				&& controller.getHTTPClients() != null 
				&& !controller.getHTTPClients().isEmpty()) {
			controller.getHTTPClients()
			.parallelStream()
			.forEach(c -> {
				c.setJwtToken(jwtToken);
				c.setCorrelationId(corrIdHeader);
			});
		}
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

		String corrIdHeader =  request.getHeader(ApiConfigEnum.HEADER_CORRELATION_ID) != null ? request.getHeader(ApiConfigEnum.HEADER_CORRELATION_ID) :  (String)request.getAttribute(ApiConfigEnum.HEADER_CORRELATION_ID);
		response.setHeader(ApiConfigEnum.HEADER_CORRELATION_ID, corrIdHeader);

		String corrIdApiHeader =  request.getHeader(ApiConfigEnum.HEADER_API_CORRELATION_ID) != null ?  request.getHeader(ApiConfigEnum.HEADER_API_CORRELATION_ID) : (String)request.getAttribute(ApiConfigEnum.HEADER_API_CORRELATION_ID);
		response.setHeader(ApiConfigEnum.HEADER_API_CORRELATION_ID, corrIdApiHeader);
		super.postHandle(request, response, handler, modelAndView);

		if(HttpStatus.Series.resolve(response.getStatus()) == Series.SUCCESSFUL) {
			LOGGER.info("[API={}] Statut HTTP : [{}]", corrIdApiHeader, response.getStatus());
		}
		else {
			LOGGER.warn("[API={}] Statut HTTP : [{}]", corrIdApiHeader, response.getStatus());
		}
	}
}
