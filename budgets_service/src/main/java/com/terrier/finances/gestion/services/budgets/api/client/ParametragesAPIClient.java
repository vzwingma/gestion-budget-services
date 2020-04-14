package com.terrier.finances.gestion.services.budgets.api.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
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
public class ParametragesAPIClient extends AbstractHTTPClient<CategorieOperation> {


	private List<CategorieOperation> categoriesSsCategories;

	public ParametragesAPIClient() {
		super(CategorieOperation.class);
	}
	/**
	 * Liste des catégories
	 * @return liste de catégories
	 */
	public List<CategorieOperation> getCategories(){
		if(categoriesSsCategories == null) {
			try {
				categoriesSsCategories = new ArrayList<>();
				List<CategorieOperation> categories = callHTTPGetListData(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL)
						.block();
				categoriesSsCategories.addAll(categories);
				categories.stream()
				.forEach(c -> {
					c.getListeSSCategories().stream().forEach(ssCats -> {
						ssCats.setCategorieParente(c);
					});
					categoriesSsCategories.addAll(c.getListeSSCategories());	
				});

			} catch (UserNotAuthorizedException | DataNotFoundException e) {
				LOGGER.warn("Impossible de charger les catégories");
			}
		}
		return categoriesSsCategories;
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
	public String getCorrId() {
		// Pas de surcharge du CorrId. Transmis par HTTP Header
		return null;
	}


	@Override
	public ApiUrlConfigEnum getConfigServiceURI() {
		return ApiUrlConfigEnum.APP_CONFIG_URL_PARAMS;
	}

}
