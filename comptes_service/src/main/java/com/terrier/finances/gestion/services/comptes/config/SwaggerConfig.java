package com.terrier.finances.gestion.services.comptes.config;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.communs.api.config.AbstractSwaggerConfig;
/**
 * Config Swagger
 * @author vzwingma
 *
 */
@Configuration
public class SwaggerConfig extends AbstractSwaggerConfig {                                    


	@Override
	public String getNomService() {
		return " Comptes";
	} 
}