package com.terrier.finances.gestion.services.parametrages.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(apiInfo())
        		.select()
        		.apis(RequestHandlerSelectors.basePackage("com.terrier.finances.gestion.services"))
        		.paths(PathSelectors.any())
        		.build();                                           
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Services Paramétrage de Gestion de Budget")
                .description("Swagger des API Paramétrages de l'application de gestion de budget")
                .contact(new Contact("Vincent Zwingmann", "", "vincent.zwingmann@gmail.com"))
                .license("Apache License Version 2.0")
                .version("2.0")
                .build();
    } 
    
    
    @Bean SecurityConfiguration security() { return new SecurityConfiguration(null, null, "Bearer", "Budget Services", "Bearer jwt_access_token", null, Boolean.TRUE); }
}