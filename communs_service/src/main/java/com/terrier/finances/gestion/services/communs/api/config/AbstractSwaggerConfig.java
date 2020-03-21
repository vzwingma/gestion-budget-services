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
                .title(getTitle())
                .description(getDescription())
                .contact(new Contact("Vincent Zwingmann", "", "vincent.zwingmann@gmail.com"))
                .license("Apache License Version 2.0")
                .version("2.0")
                .build();
    } 
    
    /**
     * @return description de l'API
     */
    public abstract String getDescription();
    
    /**
     * @return titre de l'API
     */
    public abstract String getTitle();
    
    /**
     * @return Config de Sécurité
     */
    @Bean 
    SecurityConfiguration security() { 
    	return new SecurityConfiguration(null, null, "Bearer", "Budget Services", "Bearer jwt_access_token", null, Boolean.TRUE); 
    }

}
