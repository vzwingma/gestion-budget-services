package com.terrier.finances.gestion.services.budgets.api.config;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.communs.api.config.AbstractSwaggerConfig;

import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Config Swagger
 * @author vzwingma
 *
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig extends AbstractSwaggerConfig {                                    

	@Override
	public String getNomService() {
		return "Opérations";
	} 


}