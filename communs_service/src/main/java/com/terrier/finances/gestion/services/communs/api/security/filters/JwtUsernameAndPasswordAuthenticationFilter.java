package com.terrier.finances.gestion.services.communs.api.security.filters;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;

/**
 * Authentification par login/mdp sur l'URL : {@link BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL}
 * @author vzwingma
 *
 */
@Deprecated
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter   {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthenticationFilter.class);

	
	// Auth manager pour l'authentification
	private AuthenticationManager authManager;

//	private UtilisateursService usersDetailsServices;

	private IncomingRequestInterceptor interceptor;
	
	
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, IncomingRequestInterceptor interceptor) {
		this.authManager = authManager;
//		this.usersDetailsServices = usersDetailsServices;
		this.interceptor = interceptor;

		// By default, UsernamePasswordAuthenticationFilter listens to "/login" path. Override de l'URL d'authentification
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL, HttpMethod.POST.name()));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Logger
			interceptor.manageHeaders(request, null);
			// 1. Get credentials from request
			JsonFactory factory = new JsonFactory();
			factory.setCharacterEscapes(new JsonpCharacterEscapes());
			ObjectMapper mapper = new ObjectMapper(factory).disableDefaultTyping();
			AuthLoginAPIObject creds = mapper.readValue(request.getInputStream(), AuthLoginAPIObject.class);
			LOGGER.info("[idUser=?] Authentification de [{}]", creds.getLogin());
			// 2. Create auth object (contains credentials) which will be used by auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					creds.getLogin(), creds.getMotDePasse(), Collections.emptyList());

			// 3. Authentication manager authenticate the user, and use UserDetailsServiceImpl::loadUserByUsername() method to load the user.
			return authManager.authenticate(authToken);

		} catch (IOException e) {
			throw new BadCredentialsException("Impossible de lire " + request);
		}
	}

	/**
	 * Génération d'un token JWT avec les données de l'utilisateur
	 *  (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#successfulAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain, org.springframework.security.core.Authentication)
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

//		try {
//			Utilisateur utilisateur = this.usersDetailsServices.successfullAuthentication(auth);
//			LOGGER.info("[idUser={}] Utilisateur [{}] authentifié", utilisateur.getId(), auth.getName());
//			Long now = Calendar.getInstance().getTimeInMillis();
//			String token = Jwts.builder()
//					.setSubject(auth.getName())
//					.setId(UUID.randomUUID().toString())
//					// Convert to list of strings. 
//					.claim(JwtConfigEnum.JWT_CLAIM_HEADER_AUTORITIES, auth.getAuthorities().stream()
//							.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//					.claim(JwtConfigEnum.JWT_CLAIM_HEADER_USERID, utilisateur.getId())
//					.setIssuedAt(new Date(now))
//					.setIssuer("Budget-Services")
//					.setExpiration(new Date(now + JwtConfigEnum.JWT_EXPIRATION_S * 1000))  // in milliseconds
//					.signWith(SignatureAlgorithm.HS512, JwtConfigEnum.JWT_SECRET_KEY.getBytes())
//					.compact();
//
//			// Add token to header
//			response.addHeader(JwtConfigEnum.JWT_HEADER_AUTH, JwtConfigEnum.JWT_HEADER_AUTH_PREFIX + token);
//			LOGGER.trace("Token [{}]", response.getHeader(JwtConfigEnum.JWT_HEADER_AUTH));
//		} catch (DataNotFoundException e) {
//			LOGGER.error("Impossible de charger les données de l'utilisateur [{}]", auth.getName());
//		}
		try {
			interceptor.postHandle(request, response, null, null);
		} catch (Exception e) {
			LOGGER.error("Erreur ",e );
		}
	}
}