package com.terrier.finances.gestion.services.budgets.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAsync
@EnableWebMvc
@ComponentScan(basePackages = { 
		"com.terrier.finances.gestion.services.communs.api.config",	
		"com.terrier.finances.gestion.services.communs.api.interceptors",
		"com.terrier.finances.gestion.services.communs.spi.mongodb",
		"com.terrier.finances.gestion.services.budgets.api",
		"com.terrier.finances.gestion.services.budgets.business",
		"com.terrier.finances.gestion.services.budgets.spi",

		})
@PropertySource(value={"classpath:config.properties"}, ignoreResourceNotFound = true)
public class AppConfig {


}