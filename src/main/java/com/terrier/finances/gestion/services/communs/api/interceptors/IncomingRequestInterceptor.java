package com.terrier.finances.gestion.services.communs.api.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.terrier.finances.gestion.communs.api.security.ApiConfigEnum;
import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;

/**
 * Request inteceptor pour charger le correlationID et le idUser
 * @author vzwingma
 *
 */
@Component
public class IncomingRequestInterceptor extends HandlerInterceptorAdapter {

	

	public static final Logger LOGGER = LoggerFactory.getLogger( IncomingRequestInterceptor.class );


	/**
	 * Traite les entêtes en entrée
	 * @param request
	 * @param apiController
	 */
	private void manageHeaders(HttpServletRequest request, AbstractAPIController apiController) {
		
		StringBuilder valueLog = new StringBuilder();
		String corrIdHeader =  request.getHeader(ApiConfigEnum.HEADER_CORRELATION_ID);
		if(corrIdHeader != null) {
			valueLog.append("[API=").append(corrIdHeader).append("]");
		}
		else {
			valueLog.append("[API]");
		}
		String jwtToken =  request.getHeader(JwtConfigEnum.JWT_HEADER_AUTH);
		if(jwtToken != null) {
			String idUser = null;
			try {
				idUser = (String)JwtConfigEnum.getJWTClaims(jwtToken).get(JwtConfigEnum.JWT_CLAIM_HEADER_USERID);
				valueLog.append("[idUser=").append(idUser).append("]");
			}
			catch (Exception e) {
				idUser = "?";
			}
			try {
				request.setAttribute("userSession", apiController.getUtilisateur(jwtToken));
			} catch (UserNotAuthorizedException e) {
				LOGGER.warn("[API={}][idUser={}] Impossible d'injecter la userSession. Utilisateur non authorisé", corrIdHeader, idUser);
				request.setAttribute("userSession", null);
			}
		}
		apiController.updateMdckey(valueLog.toString());
	}
	
	

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			if(handlerMethod.getBean() instanceof AbstractAPIController) {
				manageHeaders(request, (AbstractAPIController)handlerMethod.getBean());
			}
		}
		return super.preHandle(request, response, handler);
	}
}
