package com.terrier.finances.gestion.services.communs.abstrait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.services.budget.business.OperationsService;
import com.terrier.finances.gestion.services.parametrages.business.ParametragesService;
import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;
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
	
	public AbstractBusinessService(){
		LOGGER.info("[INIT] Service {}", this.getClass().getSimpleName());
	}
	/**
	 * Constructeur permettant de définir les composants utilisés en DATA
	 */

	@Autowired
	private ParametragesService serviceParams;
	@Autowired
	private AuthenticationService serviceUtilisateurs;
	@Autowired
	private OperationsService serviceOperation;

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

	public UserBusinessSession getBusinessSession(String idSession){
		return getServiceUtilisateurs().getBusinessSession(idSession);
	}
	
	/**
	 * @return the serviceAuth
	 */
	public AuthenticationService getServiceUtilisateurs() {
		return serviceUtilisateurs;
	}

	/**
	 * @param serviceUtilisateurs the serviceAuth to set
	 */
	public void setServiceUtilisateurs(AuthenticationService serviceUtilisateurs) {
		this.serviceUtilisateurs = serviceUtilisateurs;
	}

	/**
	 * @return the serviceOperation
	 */
	public OperationsService getServiceOperation() {
		return serviceOperation;
	}

	/**
	 * @param serviceOperation the serviceOperation to set
	 */
	public void setServiceOperation(OperationsService serviceOperation) {
		this.serviceOperation = serviceOperation;
	}
	
	
}
