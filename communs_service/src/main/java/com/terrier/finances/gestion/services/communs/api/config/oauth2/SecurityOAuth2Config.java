package com.terrier.finances.gestion.services.communs.api.config.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration des API. Autorisées par OpenID
 * @author vzwingma
 *
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityOAuth2Config extends ResourceServerConfigurerAdapter {



	public static final Logger LOGGER = LoggerFactory.getLogger( SecurityOAuth2Config.class );

	/**
	 * configuration Sécurité
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		LOGGER.info("[INIT] Security OpenIDConnect"); // par la le Github remote Server Token Service
			http
				.cors().and()
				.csrf().disable()
				// authorization requests config
				.authorizeRequests()
					.antMatchers("/error").permitAll()
					.antMatchers("/favicon.ico").permitAll()
					// Authorize Swagger
					.antMatchers(HttpMethod.GET, "/swagger-ui/**").anonymous()
					.antMatchers(HttpMethod.GET, "/swagger-resources/**").anonymous()
					.antMatchers(HttpMethod.GET, "/webjars/**").anonymous()
					.antMatchers(HttpMethod.GET, "/v2/api-docs/**").anonymous()
					// Actuators
					.antMatchers(HttpMethod.GET, "/actuator/**").anonymous()
					.antMatchers(HttpMethod.GET, "/csrf/**").anonymous()
					// Any other request must be authenticated
					.anyRequest().authenticated()
					;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource()
	{
		LOGGER.warn("Paramétrage CORS allow by default");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
}
