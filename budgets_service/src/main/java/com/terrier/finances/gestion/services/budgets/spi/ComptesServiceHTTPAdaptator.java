/**
 * 
 */
package com.terrier.finances.gestion.services.budgets.spi;

import java.util.Collections;

import com.terrier.finances.gestion.services.budgets.business.ports.IComptesServiceProvider;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

/**
 * Client de l'API Comptes 
 * @author vzwingma
 *
 */
@Service
public class ComptesServiceHTTPAdaptator extends AbstractHTTPClient<CompteBancaire> implements IComptesServiceProvider {

	public ComptesServiceHTTPAdaptator() {
		super(CompteBancaire.class);
	}


	/**
	 * Recherche du compte
	 * @param idCompte id du Compte
	 * @return compte correspondant
	 */
	public CompteBancaire getCompteById(String idCompte) {
		try {
			return callHTTPGetData(BudgetApiUrlEnum.COMPTES_ID_FULL, Collections.singletonMap(BudgetApiUrlEnum.PARAM_ID_COMPTE, idCompte)).block();
		} catch (DataNotFoundException e) {
			LOGGER.error("Erreur lors de la recherche du compte", e);
			return null;
		}
	}

	
	@Override
	public ApiUrlConfigEnum getConfigServiceURI() {
		return ApiUrlConfigEnum.APP_CONFIG_URL_COMPTES;
	}
}
