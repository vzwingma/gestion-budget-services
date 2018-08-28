package com.terrier.finances.gestion.parametrages.business;

import java.text.ParseException;


import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.data.DataUtils;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.parametrages.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.utilisateurs.data.UtilisateurDatabaseService;

/**
 * Service fournissant les paramètres
 * @author vzwingma
 *
 */
@Service
public class ParametragesService {


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
	@Autowired
	private UtilisateurDatabaseService dataUsers;
	/**
	 * Liste des catégories
	 */
	private List<CategorieDepense> listeCategories;

	
	
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@Value("${budget.version}")
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
	@Value("${budget.build.time}")
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
	@Value("${budget.ui.refresh.period}")
	public void setUiRefreshPeriod(String uiRefreshPeriod) {
		this.uiRefreshPeriod = uiRefreshPeriod;
	}

	
	@Value("${budget.ui.session.validity.period}")
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
		if(listeCategories == null){
			listeCategories = dataParams.chargeCategories();
			LOGGER.info("> Chargement des catégories <");
			listeCategories.stream().forEachOrdered(c -> {
				LOGGER.debug("[{}] {}", c.isActif() ? "v" : "X", c);
				c.getListeSSCategories().stream().forEachOrdered(s -> LOGGER.debug("[{}] 	{}", s.isActif() ? "v" : "X", s));
			});
		}
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
	 * Recherche du compte par id
	 * @param idCompte id du compte
	 * @param utilisateur utilisateur
	 * @return compteBancaire
	 * @throws DataNotFoundException
	 */
	public CompteBancaire getCompteById(String idCompte, String proprietaire) throws DataNotFoundException{
		return dataUsers.chargeCompteParId(idCompte, proprietaire);
	}



	/**
	 * Recherche des comptes d'un utilisateur
	 * @param utilisateur utilisateur
	 * @return liste des comptes bancaires
	 * @throws DataNotFoundException
	 */
	public List<CompteBancaire> getComptesUtilisateur(Utilisateur utilisateur) throws DataNotFoundException{
		return dataUsers.chargeComptes(utilisateur);
	}
	
	
	/**
	 * Reset des données
	 */
	public void resetData(){
		dataParams.resetData();
	}
}
