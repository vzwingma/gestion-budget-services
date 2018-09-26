package com.terrier.finances.gestion.services.communs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.terrier.finances.gestion.services.communs.data.mongodb.MongoDBConfigEnum;

@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = { 
		"com.terrier.finances.gestion.services",
		"com.terrier.finances.gestion.services.comptes.business",
		"com.terrier.finances.gestion.services.parametrages.business",
		"com.terrier.finances.gestion.services.statut.business",
		"com.terrier.finances.gestion.services.utilisateurs.business",
		"com.terrier.finances.gestion.services.communs.api.interceptors"})
@PropertySource(value={"classpath:config.properties"}, ignoreResourceNotFound = true)
public class AppConfig {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
	/**
	 * Retourne la valeur string de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	public static String getStringEnvVar(MongoDBConfigEnum cle){
		String envVar = System.getenv(cle.name());
		if(envVar != null) {
			return envVar;
		}
		else {
			if(LOGGER.isWarnEnabled()) {
				LOGGER.warn("La clé {} n'est définie. Utilisation de la valeur par défaut : {} ", cle.name(), cle.getDefaultValue());
			}
			 return cle.getDefaultValue();
		}
	}
	




	/**
	 * Retourne la valeur int de la variable d'environnement
	 * @param cle
	 * @return valeur de la clé
	 */
	public static Integer getIntgerEnvVar(MongoDBConfigEnum cle){
		String env = System.getenv(cle.name());
		try{
			return Integer.parseInt(env);
		}
		catch(NumberFormatException e){
			if(LOGGER.isWarnEnabled()) {
				LOGGER.error("La clé {}={} n'est pas un nombre. La valeur par défaut : {} ", cle.name(), env, cle.getIntDefaultValue());
			}
			return cle.getIntDefaultValue();
		}
	}
}
