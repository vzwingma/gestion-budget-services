package com.terrier.finances.gestion.services.communs.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configuration des API. Autorisées par OpenID
 * @author vzwingma
 *
 */
@EnableWebSecurity
public class SecurityOIdCConfig extends WebSecurityConfigurerAdapter {


	public static final Logger LOGGER = LoggerFactory.getLogger( SecurityOIdCConfig.class );

	/**
	 * configuration Sécurité
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.info("[INIT] Security OpenIDConnect");
		http
		.csrf().disable()
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
			.anyRequest().authenticated()
		.and()
			.oauth2Client();
	}
}
