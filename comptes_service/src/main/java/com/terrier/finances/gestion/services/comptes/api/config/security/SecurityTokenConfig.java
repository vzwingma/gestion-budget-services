package com.terrier.finances.gestion.services.comptes.api.config.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;
import com.terrier.finances.gestion.services.communs.api.security.filters.JwtTokenAuthenticationFilter;
import com.terrier.finances.gestion.services.communs.api.security.filters.JwtUsernameAndPasswordAuthenticationFilter;

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
	private IncomingRequestInterceptor interceptor;

	
	/**
	 * configuration Sécurité
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// make sure we use stateless session; session won't be used to store user's state.
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
			.csrf().disable()
		// handle an authorized attempts 
		.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {
			LOGGER.warn("Erreur 401 : Accès non autorisé à l'URL [{}]", req.getRequestURI());
			rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} )
		.and()
		// Add a filter to validate user credentials and add token in the response header
		.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), interceptor))
		// Add a filter to validate the tokens with every request
		.addFilterAfter(new JwtTokenAuthenticationFilter(), JwtUsernameAndPasswordAuthenticationFilter.class)
		;
		// authorization requests config
//		.authorizeRequests()
//			.antMatchers("/error").permitAll()
//			.antMatchers("/favicon.ico").permitAll()
//			// Authorize Swagger
//			.antMatchers(HttpMethod.GET, "/swagger-ui*").anonymous()
//			.antMatchers(HttpMethod.GET, "/swagger-resources/**").anonymous()
//			.antMatchers(HttpMethod.GET, "/webjars/**").anonymous()
//			.antMatchers(HttpMethod.GET, "/v2/api-docs/**").anonymous()
//			// Actuators
//			.antMatchers(HttpMethod.GET, "/actuator/**").anonymous()
//			.antMatchers(HttpMethod.GET, "/csrf/**").anonymous()   
//			// Any other request must be authenticated
//			.anyRequest().authenticated(); 
	}


	// Spring has UserDetailsService interface, which can be overriden to provide our implementation for fetching user from database (or any other source).
	// The UserDetailsService object is used by the auth manager to load the user from database.
	// In addition, we need to define the password encoder also. So, auth manager can compare and verify passwords.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	//	auth.userDetailsService(usersDetailsServices).passwordEncoder(passwordEncoder());
	}
	

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
