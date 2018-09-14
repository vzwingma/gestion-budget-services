package com.terrier.finances.gestion.services.communs.api.config.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.communs.api.config.RestCorsFilter;

/**
 * Configuration des API. Authorisées par JWT
 * @author vzwingma
 *
 */
@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtConfig jwtConfig;
	
	

	@Override
  	protected void configure(HttpSecurity http) throws Exception {
    	http
		.csrf().disable()
		    // make sure we use stateless session; session won't be used to store user's state.
	 	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		    // handle an authorized attempts 
		    .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)) 	
		.and()
		   // Add a filter to validate the tokens with every request
		.addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
		// authorization requests config
		.authorizeRequests()
		   // Authorise Authenticate
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
	
	@Bean
  	public JwtConfig jwtConfig() {
    	   return new JwtConfig();
  	}
}
