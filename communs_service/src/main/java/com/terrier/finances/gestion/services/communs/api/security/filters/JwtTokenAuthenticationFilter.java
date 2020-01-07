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
		
		String header = request.getHeader(JwtConfigEnum.JWT_HEADER_AUTH);
		if(header == null || !header.startsWith(JwtConfigEnum.JWT_HEADER_AUTH_PREFIX)) {
			chain.doFilter(request, response);  		// If not valid, go to the next filter.
			return;
		}
		try {	// exceptions might be thrown in creating the claims if for example the token is expired
			Claims claims = JwtConfigEnum.getJWTClaims(header);
			String username = claims.getSubject();
            if(username != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");
                if(authorities == null) {
                	authorities = new ArrayList<String>();
                }
                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                 username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                 LOGGER.trace("JWT Auth : OK [{}]", auth);
                 // Now, user is authenticated
                 SecurityContextHolder.getContext().setAuthentication(auth);
            }

		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}
		chain.doFilter(request, response);
	}
}