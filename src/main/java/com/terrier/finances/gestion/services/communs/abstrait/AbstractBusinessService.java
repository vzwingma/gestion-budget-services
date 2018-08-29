package com.terrier.finances.gestion.services.communs.abstrait;

import org.springframework.beans.factory.annotation.Autowired;

import com.terrier.finances.gestion.services.budget.business.OperationsService;
import com.terrier.finances.gestion.services.parametrages.business.ParametragesService;
import com.terrier.finances.gestion.services.utilisateurs.business.AuthenticationService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Classe abstraite d'un service business
 * @author vzwingma
 *
 */
public abstract class AbstractBusinessService {

	@Autowired
	private ParametragesService serviceParams;
	@Autowired
	private AuthenticationService serviceUtilisateurs;

	@Autowired
	private OperationsService serviceOperation;

	/**
	 * @return the serviceParams
	 */
	protected ParametragesService getServiceParams() {
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
	protected AuthenticationService getServiceUtilisateurs() {
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
	protected OperationsService getServiceOperation() {
		return serviceOperation;
	}

	/**
	 * @param serviceOperation the serviceOperation to set
	 */
	public void setServiceOperation(OperationsService serviceOperation) {
		this.serviceOperation = serviceOperation;
	}
	
	
}
