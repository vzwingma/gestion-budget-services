package com.terrier.finances.gestion.services.parametrages.business;

import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieDepense;
import com.terrier.finances.gestion.communs.utils.data.DataUtils;
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
	 * Info de version de l'application 
	 */
	private String version;
	private String buildTime;
	private String uiRefreshPeriod;
	private String uiValiditySessionPeriod;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesService.class);

	@Autowired
	private ParametragesDatabaseService dataParams;

	/**
	 * Liste des catégories
	 */
	private List<CategorieDepense> listeCategories;

	@PostConstruct
	public void chargeCategories(){
		listeCategories = dataParams.chargeCategories();
		LOGGER.info("> Chargement des catégories <");
		listeCategories.stream().forEachOrdered(c -> {
			LOGGER.debug("[{}] {}", c.isActif() ? "v" : "X", c);
			c.getListeSSCategories().stream().forEachOrdered(s -> LOGGER.debug("[{}]		{}", s.isActif() ? "v" : "X", s));
		});
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
			this.buildTime = DataUtils.getUtcToLocalTime(utcBuildTime);
		} catch (ParseException e) {
			this.buildTime = utcBuildTime;
		}
	}



	/**
	 * @return période de rafraichissement des IHM
	 */
	public String getUiRefreshPeriod() {
		return uiRefreshPeriod;
	}


	/**
	 * période de rafraichissement des IHM
	 * @param uiRefreshPeriod
	 */
	@Value("${budget.ui.refresh.period:1}")
	public void setUiRefreshPeriod(String uiRefreshPeriod) {
		this.uiRefreshPeriod = uiRefreshPeriod;
	}


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
	public List<CategorieDepense> getCategories(){
		return listeCategories;
	}


	/**
	 * @param idCategorie
	 * @return la catégorie ou la sous catégorie correspondante à l'id
	 */
	public CategorieDepense getCategorieById(String idCategorie) throws DataNotFoundException{
		LOGGER.trace("Recherche de la catégorie : {}", idCategorie);
		CategorieDepense ssCategorieDepense = dataParams.chargeCategorieParId(idCategorie);
		if(ssCategorieDepense != null){
			LOGGER.trace(">> : {}", ssCategorieDepense);
			return ssCategorieDepense;
		}
		throw new DataNotFoundException("Catégorie introuvable");
	}



	/**
	 * Reset des données
	 */
	public void resetData(){
		dataParams.resetData();
	}
}
