package com.terrier.finances.gestion.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

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
	 * @param buildTime the buildTime to set
	 */
	@Value("${budget.build.time}")
	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}

	
	
	/**
	 * @return liste des catégories
	 */
	public List<CategorieDepense> getCategories() throws DataNotFoundException{
		if(listeCategories == null){
			listeCategories = dataParams.chargeCategories();
			LOGGER.info("> Chargement des catégories <");
			for (CategorieDepense categorie : listeCategories) {
				LOGGER.info("[{}] {}", categorie.isActif() ? "v" : "X", categorie);
				for (CategorieDepense ssCategorie : categorie.getListeSSCategories()) {
					LOGGER.info("[{}] 	{}", ssCategorie.isActif() ? "v" : "X", ssCategorie);	
				}
			}
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
	public CompteBancaire getCompteById(String idCompte, Utilisateur utilisateur) throws DataNotFoundException{
		return dataParams.chargeCompteParId(idCompte, utilisateur);
	}
	

	/**
	 * Recherche des comptes d'un utilisateur
	 * @param utilisateur utilisateur
	 * @return liste des comptes bancaires
	 * @throws DataNotFoundException
	 */
	public List<CompteBancaire> getComptesUtilisateur(Utilisateur utilisateur) throws DataNotFoundException{
		return dataParams.chargeComptes(utilisateur);
	}
	
	
	/**
	 * Reset des données
	 */
	public void resetData(){
		dataParams.resetData();
	}
}
