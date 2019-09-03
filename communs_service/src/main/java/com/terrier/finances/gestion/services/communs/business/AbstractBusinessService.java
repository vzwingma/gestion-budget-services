package com.terrier.finances.gestion.services.communs.business;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.stereotype.Service;

/**
 * Classe abstraite d'un service business
 * @author vzwingma
 *
 */
@Service
public abstract class AbstractBusinessService extends AbstractHealthIndicator {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBusinessService.class);
	
	public AbstractBusinessService(){
		MDC.put("key", "");
		LOGGER.info("[INIT] Service {}", this.getClass().getSimpleName());
	}

	@PreDestroy
	public void endApp(){
		LOGGER.info("[END] Service {}", this.getClass().getSimpleName());
	}


}
