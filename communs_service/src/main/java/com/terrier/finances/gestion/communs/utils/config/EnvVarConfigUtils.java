/*

 */
package com.terrier.finances.gestion.communs.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;

/**
 * @author vzwingma
 *
 */
public class EnvVarConfigUtils {


	private static final Logger LOGGER = LoggerFactory.getLogger( EnvVarConfigUtils.class );

	private EnvVarConfigUtils() {
		// Constructeur privé
	}
	
	/**
	 * Retourne la valeur string de la variable d'environnement
	 * @param cle clé d'URL
	 * @return valeur de la clé
	 */
	public static String getStringEnvVar(ApiUrlConfigEnum cle){
		String envVar = System.getenv(cle.name());
		if(envVar != null) {
			return envVar;
		}
		else {
			if(LOGGER.isWarnEnabled()) {
				LOGGER.warn("La clé {} n'est pas définie. Utilisation de la valeur par défaut : {} ", cle.name(), cle.getDefaultValue());
			}
			return cle.getDefaultValue();
		}
	}
}
