package com.terrier.finances.gestion.services.communs.api.config.security;

import org.springframework.beans.factory.annotation.Value;

/**
 * Configu JWT
 * @author vzwingma
 *
 */
public class JwtConfig {
	public static final String JWT_AUTH_HEADER = "Authorization";

	public static final  String JWT_AUTH_PREFIX = "Bearer : ";


	public static final  int JWT_EXPIRATION_S = 86400;

	@Value("${security.jwt.secret:JwtSecretKey}")
	public static final  String JWT_SECRET_KEY = "JwtSecretKey";

}
