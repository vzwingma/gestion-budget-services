package com.terrier.finances.gestion.services.utilisateurs.api.config;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.communs.api.config.AbstractSwaggerConfig;

import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * Config Swagger
 * @author vzwingma
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends AbstractSwaggerConfig {                                    
    
    @Override
	public String getDescription() {
		return "Swagger des API Utilisateurs de l'application de gestion de budget";
	}

	@Override
	public String getTitle() {
		return "Services Utilisateurs de Gestion de Budget";
	}
}