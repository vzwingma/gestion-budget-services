package com.terrier.finances.gestion.services;

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
public class ServicesApplication {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicesApplication.class);

	@Value("${info.app.version:CURRENT}")
	private String version;
	
	public static void main(String[] args) {
		SpringApplication.run(ServicesApplication.class);
	}
	
	@PostConstruct
	public void startedApp() {
		LOGGER.info("[INIT] Démarrage de l'application Services v{}", this.version);
	}
}
