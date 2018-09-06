package com.terrier.finances.gestion.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"com.terrier.finances.gestion.services.utilisateurs.business"})	
public class TestRealAuthServices {

}
