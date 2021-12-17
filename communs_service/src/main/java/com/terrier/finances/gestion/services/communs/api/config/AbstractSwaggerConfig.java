/**
 * 
 */
package com.terrier.finances.gestion.services.communs.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;

/**
 * Config Swagger
 * @author vzwingma
 *
 */
public abstract class AbstractSwaggerConfig {

    @Bean
    public OpenAPI initOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .openapi("com.terrier.finances.gestion.services");
    }
/**
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)
        	//	.apiInfo(apiInfo())
        		.select()
        		.apis(RequestHandlerSelectors.basePackage("com.terrier.finances.gestion.services"))
        		.paths(PathSelectors.any())
        		.build();                                           
    }
    **/
    /**
     * @return API Info pour Swagger
     */
    private Info apiInfo() {
        return new Info()
                .title("Gestion de Budgets : µService " + getNomService())
                .description("API du service " + getNomService() + " de l'application")
                .contact(new Contact().name("Vincent Zwingmann").email("vincent.zwingmann@gmail.com"))
                .license(new License().name("Apache License Version 2.0"))
                .version("2.0");
    } 
    
    /**
     * @return nom du service
     */
    public abstract String getNomService();
    
    /**
     * @return Config de Sécurité

    @Bean 
    SecurityConfiguration security() { 
    	return SecurityConfigurationBuilder.builder()
    			.realm("Bearer")
    			.enableCsrfSupport(Boolean.TRUE)
    			.appName("Budget Services").build();
    }
     */
}
