package com.terrier.finances.gestion.services.communs.business;

import java.text.ParseException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budget.business.OperationsService;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;
import com.terrier.finances.gestion.services.parametrages.business.ParametragesService;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Classe abstraite d'un service business
 * @author vzwingma
 *
 */
@Service
public class AbstractBusinessService {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBusinessService.class);
	
	
	/**
	 * Info de version de l'application 
	 */
	private String version;
	private String buildTime;
	
	public AbstractBusinessService(){
		MDC.put("key", "");
		LOGGER.info("[INIT] Service {}", this.getClass().getSimpleName());
	}
	/**
	 * Constructeur permettant de définir les composants utilisés en DATA
	 */

	@Autowired
	private ParametragesService serviceParams;
	@Autowired
	private UtilisateursService serviceUtilisateurs;
	@Autowired
	private OperationsService serviceOperations;
	@Autowired
	private ComptesService serviceComptes;

	/**
	 * @return the serviceParams
	 */
	public ParametragesService getServiceParams() {
		return serviceParams;
	}

	/**
	 * @param serviceParams the serviceParams to set
	 */
	public void setServiceParams(ParametragesService serviceParams) {
		this.serviceParams = serviceParams;
	}

	/**
	 * @param idSession
	 * @return businessSession
	 */
	public UserBusinessSession getBusinessSession(String idSession) throws UserNotAuthorizedException{
		UserBusinessSession userSession = getServiceUtilisateurs().getBusinessSession(idSession);
		if(userSession != null){
			return userSession;
		}
		throw new UserNotAuthorizedException(new StringBuilder().append("L'utilisateur ").append(idSession).append(" n'est pas authentifié").toString());
	}
	
	/**
	 * @return the serviceAuth
	 */
	public UtilisateursService getServiceUtilisateurs() {
		return serviceUtilisateurs;
	}

	/**
	 * @param serviceUtilisateurs the serviceAuth to set
	 */
	public void setServiceUtilisateurs(UtilisateursService serviceUtilisateurs) {
		this.serviceUtilisateurs = serviceUtilisateurs;
	}

	/**
	 * @return the serviceOperation
	 */
	public OperationsService getServiceOperations() {
		return serviceOperations;
	}

	/**
	 * @param serviceOperation the serviceOperation to set
	 */
	public void setServiceOperations(OperationsService serviceOperation) {
		this.serviceOperations = serviceOperation;
	}
	
	
	
	/**
	 * @return the serviceComptes
	 */
	public ComptesService getServiceComptes() {
		return serviceComptes;
	}

	/**
	 * @param serviceComptes the serviceComptes to set
	 */
	public void setServiceComptes(ComptesService serviceComptes) {
		this.serviceComptes = serviceComptes;
	}

	@PreDestroy
	public void endApp(){
		LOGGER.info("[END] Service {}", this.getClass().getSimpleName());
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@Value("${budget.version:CURRENT}")
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the buildTime
	 */
	public String getBuildTime() {
		return buildTime;
	}

	/**
	 * @param utcBuildTime the buildTime to set (en UTC)
	 */
	@Value("${budget.build.time:NOW}")
	public void setBuildTime(String utcBuildTime) {
		try {
			this.buildTime = BudgetDateTimeUtils.getUtcToLocalTime(utcBuildTime);
		} catch (ParseException e) {
			this.buildTime = utcBuildTime;
		}
	}

}
