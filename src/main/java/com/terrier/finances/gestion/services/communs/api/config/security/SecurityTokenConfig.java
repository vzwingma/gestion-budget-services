package com.terrier.finances.gestion.services.communs.api.config.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.communs.api.security.filters.JwtTokenAuthenticationFilter;
import com.terrier.finances.gestion.services.communs.api.security.filters.JwtUsernameAndPasswordAuthenticationFilter;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;

/**
 * Configuration des API. Autorisées par JWT
 * @author vzwingma
 *
 */
@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenConfig.class);
	@Autowired
	private UtilisateursService usersDetailsServices;


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		// make sure we use stateless session; session won't be used to store user's state.
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		// handle an authorized attempts 
		.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {
			LOGGER.warn("[SEC] Erreur 401 : Accès non autorisé à l'URL [{}]", req.getRequestURI());
			rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} )
		.and()
		// Add a filter to validate user credentials and add token in the response header
		.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), usersDetailsServices))
		// Add a filter to validate the tokens with every request
		.addFilterAfter(new JwtTokenAuthenticationFilter(), JwtUsernameAndPasswordAuthenticationFilter.class)
		// authorization requests config
		.authorizeRequests()
		// Authorize Authenticate
		.antMatchers(HttpMethod.POST, BudgetApiUrlEnum.USERS_AUTHENTICATE_FULL).permitAll()
		// Authorize Swagger
		.antMatchers(HttpMethod.GET, "/swagger-ui*").permitAll()
		.antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
		.antMatchers(HttpMethod.GET, "/webjars/**").permitAll()
		.antMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll()
		// Supervision : Autorisée
		.antMatchers(HttpMethod.GET, "/admin/v1/statut").permitAll()		   
		// must be an admin 
		.antMatchers(HttpMethod.GET, "/admin/v1/password/**").hasRole("ADMIN")
		// Any other request must be authenticated
		.anyRequest().authenticated(); 
	}


	// Spring has UserDetailsService interface, which can be overriden to provide our implementation for fetching user from database (or any other source).
	// The UserDetailsService object is used by the auth manager to load the user from database.
	// In addition, we need to define the password encoder also. So, auth manager can compare and verify passwords.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usersDetailsServices).passwordEncoder(passwordEncoder());
	}
	

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
