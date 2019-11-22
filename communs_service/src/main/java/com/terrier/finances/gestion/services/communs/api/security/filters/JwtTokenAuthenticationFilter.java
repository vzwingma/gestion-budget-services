package com.terrier.finances.gestion.services.communs.api.security.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.terrier.finances.gestion.communs.api.security.JwtConfigEnum;

import io.jsonwebtoken.Claims;

/**
 * Authentification par Token JWT
 * @author vzwingma
 *
 */
public class JwtTokenAuthenticationFilter extends  OncePerRequestFilter {
    

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		// 1. get the authentication header. Tokens are supposed to be passed in the authentication header
		String header = request.getHeader(JwtConfigEnum.JWT_HEADER_AUTH);
		
		// 2. validate the header and check the prefix
		if(header == null || !header.startsWith(JwtConfigEnum.JWT_HEADER_AUTH_PREFIX)) {
			chain.doFilter(request, response);  		// If not valid, go to the next filter.
			return;
		}
		
		// If there is no token provided and hence the user won't be authenticated. 
		// It's Ok. Maybe the user accessing a public path or asking for a token.
		
		// All secured paths that needs a token are already defined and secured in config class.
		// And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.
		
		
		try {	// exceptions might be thrown in creating the claims if for example the token is expired
			// 4. Validate the token
			Claims claims = JwtConfigEnum.getJWTClaims(header);
			
			String username = claims.getSubject();
            if(username != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");
                if(authorities == null) {
                	authorities = new ArrayList<String>();
                }
                // 5. Create auth object
                // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
                // It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                 username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                 LOGGER.trace("JWT Auth : OK [{}]", auth);
                 // 6. Authenticate the user
                 // Now, user is authenticated
                 SecurityContextHolder.getContext().setAuthentication(auth);
            }

		} catch (Exception e) {
			// In case of failure. Make sure it's clear; so guarantee user won't be authenticated
			SecurityContextHolder.clearContext();
		}
		// go to the next filter in the filter chain
		chain.doFilter(request, response);
	}
}