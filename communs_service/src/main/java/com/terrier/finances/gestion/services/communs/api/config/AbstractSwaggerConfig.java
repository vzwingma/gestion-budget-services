/**
 * 
 */
package com.terrier.finances.gestion.services.communs.api.config;

import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

/**
 * Config Swagger
 * @author vzwingma
 *
 */
public abstract class AbstractSwaggerConfig {

    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(apiInfo())
        		.select()
        		.apis(RequestHandlerSelectors.basePackage("com.terrier.finances.gestion.services"))
        		.paths(PathSelectors.any())
        		.build();                                           
    }
    
    /**
     * @return API Info pour Swagger
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gestion de Budgets : µService " + getNomService())
                .description("API du service " + getNomService() + " de l'application")
                .contact(new Contact("Vincent Zwingmann", "", "vincent.zwingmann@gmail.com"))
                .license("Apache License Version 2.0")
                .version("2.0")
                .build();
    } 
    
    /**
     * @return nom du service
     */
    public abstract String getNomService();
    
    /**
     * @return Config de Sécurité
     */
    @Bean 
    SecurityConfiguration security() { 
    	return SecurityConfigurationBuilder.builder()
    			.realm("Bearer")
    			.enableCsrfSupport(Boolean.TRUE)
    			.appName("Budget Services").build();
    }

}
