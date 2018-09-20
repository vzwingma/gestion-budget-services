package com.terrier.finances.gestion.services.communs.api.security.filters;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrier.finances.gestion.communs.api.security.JwtConfig;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Authentification par login/mdp sur l'URL : {@link BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL}
 * @author vzwingma
 *
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter   {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthenticationFilter.class);

	// Auth manager pour l'authentification
	private AuthenticationManager authManager;

	private UtilisateursService usersDetailsServices;


	private Map<String, String> attempts = new HashMap<>();

	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, UtilisateursService usersDetailsServices) {
		this.authManager = authManager;
		this.usersDetailsServices = usersDetailsServices;

		// By default, UsernamePasswordAuthenticationFilter listens to "/login" path. Override de l'URL d'authentification
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL, HttpMethod.POST.name()));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. Get credentials from request
			AuthLoginAPIObject creds = new ObjectMapper().readValue(request.getInputStream(), AuthLoginAPIObject.class);
			LOGGER.info("[API][idUser=?] Authentification de [{}]", creds.getLogin());

			// 2. Create auth object (contains credentials) which will be used by auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					// Le mot de passe est en clair
					creds.getLogin(), creds.getMotDePasse(), Collections.emptyList());

			// Partage de la clé temporairement, le temps de faire l'authentification.
			attempts.put(creds.getLogin(),  creds.getMotDePasse());
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

		LOGGER.info("[API][idUser={}] Utilisateur authentifié", auth.getName());
		Utilisateur utilisateur = usersDetailsServices.successfulAuthentication(auth, attempts.get(auth.getName()));
		// Une fois que cette partie est faite. On efface l'attempt.
		attempts.remove(auth.getName());


		Long now = Calendar.getInstance().getTimeInMillis();
		String token = Jwts.builder()
				.setSubject(utilisateur.getLogin())
				.setId(UUID.randomUUID().toString())
				// Convert to list of strings. 
				.claim(JwtConfig.JWT_CLAIM_AUTORITIES_HEADER, auth.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.claim(JwtConfig.JWT_CLAIM_USERID_HEADER, utilisateur.getId())
				.setIssuedAt(new Date(now))
				.setIssuer("Budget-Services v" + usersDetailsServices.getVersion())
				.setExpiration(new Date(now + JwtConfig.JWT_EXPIRATION_S * 1000))  // in milliseconds
				.signWith(SignatureAlgorithm.HS512, JwtConfig.JWT_SECRET_KEY.getBytes())
				.compact();

		// Add token to header
		response.addHeader(JwtConfig.JWT_AUTH_HEADER, JwtConfig.JWT_AUTH_PREFIX + token);
		LOGGER.debug("[API][idUser={}] Token [{}]", auth.getName(), response.getHeader(JwtConfig.JWT_AUTH_HEADER));
	}


}