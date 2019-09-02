package com.terrier.finances.gestion.services.parametrages.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.parametrages.data.ParametragesDatabaseService;

/**
 * Service fournissant les paramètres
 * @author vzwingma
 *
 */
@Service
public class ParametragesService extends AbstractBusinessService {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesService.class);

	private String uiValiditySessionPeriod;


	@Autowired
	private ParametragesDatabaseService dataParams;


	@Value("${budget.ui.session.validity.period:10}")
	public void setUiValiditySessionPeriod(String uiValiditySessionPeriod){
		LOGGER.info("Suivi des sessions utilisateurs. Durée de validité d'une session : {} minutes", uiValiditySessionPeriod);
		this.uiValiditySessionPeriod = uiValiditySessionPeriod;
	}



	/**
	 * @return the uiValiditySessionPeriod
	 */
	public String getUiValiditySessionPeriod() {
		return uiValiditySessionPeriod;
	}

	/**
	 * @return liste des catégories
	 */
	public List<CategorieOperation> getCategories(){
		return dataParams.chargeCategories();
	}


	/**
	 * @param idCategorie
	 * @return la catégorie ou la sous catégorie correspondante à l'id
	 */
	public CategorieOperation getCategorieById(String idCategorie) throws DataNotFoundException{
		LOGGER.trace("Recherche de la catégorie : {}", idCategorie);
		CategorieOperation ssCategorieDepense = dataParams.getCategorieParId(idCategorie);
		if(ssCategorieDepense != null){
			LOGGER.trace(">> : {}", ssCategorieDepense);
			return ssCategorieDepense;
		}
		throw new DataNotFoundException("Catégorie introuvable");
	}
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Paramétrages");
	}
	
}
