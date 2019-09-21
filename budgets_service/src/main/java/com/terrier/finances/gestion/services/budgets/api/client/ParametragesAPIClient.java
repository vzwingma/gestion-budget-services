package com.terrier.finances.gestion.services.budgets.api.client;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

/**
 * Client vers l'API Parametrages
 * @author vzwingma
 *
 */
@Service
public class ParametragesAPIClient extends AbstractHTTPClient {

	
	private List<CategorieOperation> categories;
	
	
	//PostConstruct
	public List<CategorieOperation> getCategories(){
		if(categories == null) {
			try {
				categories = callHTTPGetListData(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL, null, CategorieOperation.class)
				.collectList().block();
			} catch (UserNotAuthorizedException | DataNotFoundException e) {
			LOGGER.warn("Impossible de charger les catégories");
			}
		}
		return categories;
	}
	
	/**
	 * Recherche d'une catégorie
	 * @param id de la catégorie
	 * @return catégorie correspondante. Null sinon
	 */
	public CategorieOperation getCategorieParId(String id) {
		Optional<CategorieOperation> categorie =  getCategories().stream().filter(c -> id.equals(c.getId())).findFirst();
		if(categorie.isPresent()) {
			return categorie.get();
		}
		return null;
	}
	
	@Override
	public String getBaseURL() {
		return "http://localhost:8091";
	}

}
