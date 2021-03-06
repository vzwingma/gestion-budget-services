/**
 * 
 */
package com.terrier.finances.gestion.services.budgets.config;

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
public class OperationsApplication {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsApplication.class);

	@Value("${info.app.version:CURRENT}")
	private String version;
	
	public static void main(String[] args) {
		SpringApplication.run(OperationsApplication.class);
	}
	
	@PostConstruct
	public void startedApp() {
		LOGGER.info("[INIT] Démarrage des services Opérations v{}", this.version);
	}
}
