package com.terrier.finances.gestion.services.parametrages;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = { 
		"com.terrier.finances.gestion.services.parametrages.business",
		"com.terrier.finances.gestion.services.communs.data.mongodb",
		"com.terrier.finances.gestion.services.parametrages.api.config.security",
		"com.terrier.finances.gestion.services.communs.api.interceptors"
		})
@PropertySource(value={"classpath:config.properties"}, ignoreResourceNotFound = true)
public class AppConfig {


}
