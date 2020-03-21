package com.terrier.finances.gestion.services.comptes.api.config;

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
	public String getNomService() {
		return " Comptes";
	} 
}