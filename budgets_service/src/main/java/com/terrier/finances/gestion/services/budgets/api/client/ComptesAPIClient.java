/**
 * 
 */
package com.terrier.finances.gestion.services.budgets.api.client;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

/**
 * Client de l'API Comptes 
 * @author vzwingma
 *
 */
@Service
public class ComptesAPIClient extends AbstractHTTPClient {

	/**
	 * 
	 * @param idCompte id du Compte
	 * @param idUser id User
	 * @return compte correspondant
	 */
	public CompteBancaire getCompteById(String idCompte, String idUser) {
		try {
			return callHTTPGetData(BudgetApiUrlEnum.COMPTES_ID_FULL, Collections.singletonMap(BudgetApiUrlEnum.PARAM_ID_COMPTE, idCompte), CompteBancaire.class).block();
		} catch (UserNotAuthorizedException | DataNotFoundException e) {
			LOGGER.error("Erreur lors de la recherche du compte", e);
			return null;
		}
	}

	
	@Override
	public ApiUrlConfigEnum getConfigServiceURI() {
		return ApiUrlConfigEnum.APP_CONFIG_URL_COMPTES;
	}

}
