/**
 * 
 */
package com.terrier.finances.gestion.services.parametrages.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Démarrage de l'application
 * @author vzwingma
 *
 */
@SpringBootApplication
public class ParametragesApplication {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesApplication.class);

	@Value("${info.app.version:CURRENT}")
	private String version;
	
	public static void main(String[] args) {
		SpringApplication.run(ParametragesApplication.class);
	}
	
	@PostConstruct
	public void startedApp() {
		LOGGER.info("[INIT] Démarrage des services Paramétrages v{}", this.version);
	}
}
