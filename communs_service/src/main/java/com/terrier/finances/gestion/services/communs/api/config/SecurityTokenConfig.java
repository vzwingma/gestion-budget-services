package com.terrier.finances.gestion.services.communs.api.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;
import com.terrier.finances.gestion.services.communs.api.security.filters.JwtTokenAuthenticationFilter;

/**
 * Configuration des API. Autorisées par JWT
 * @author vzwingma
 *
 */
@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

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
			try {
				interceptor.manageHeaders(req, null);
			} catch (UserNotAuthorizedException e1) {
				// Rien, c'est le but
			}
			rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} )
		.and()
		// Add a filter to validate the tokens with every request
		.addFilterBefore(new JwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		// authorization requests config
		.authorizeRequests()
			.antMatchers("/error").permitAll()
			.antMatchers("/favicon.ico").permitAll()
			// Authorize Swagger
			.antMatchers(HttpMethod.GET, "/swagger-ui*").anonymous()
			.antMatchers(HttpMethod.GET, "/swagger-resources/**").anonymous()
			.antMatchers(HttpMethod.GET, "/webjars/**").anonymous()
			.antMatchers(HttpMethod.GET, "/v2/api-docs/**").anonymous()
			// Actuators
			.antMatchers(HttpMethod.GET, "/actuator/**").anonymous()
			.antMatchers(HttpMethod.GET, "/csrf/**").anonymous()   
			// Any other request must be authenticated
			.anyRequest().authenticated(); 
	}
}
