package com.terrier.finances.gestion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

/**
 * Service de données en MongoDB fournissant les paramètres
 * @author vzwingma
 *
 */
@Repository
public class ParametragesDatabaseService extends AbstractDatabaseService {

	/**
	 * Liste des catégories
	 */
	private Map<String, CategorieDepense> mapCategories = new HashMap<>();
	/**
	 * Liste des sous catégories
	 */
	private Map<String, CategorieDepense> mapSSCategories = new HashMap<>();	


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesDatabaseService.class);

	/**
	 * @return la liste des catégories
	 */
	public List<CategorieDepense> chargeCategories() {
		if(mapCategories.size() == 0){
			try{
				List<CategorieDepense> listeAllCategories =  getMongoOperation().findAll(CategorieDepense.class);
				// Ajout des catégories
				for (CategorieDepense depenseCategorie : listeAllCategories) {
					if(depenseCategorie.isCategorie()){
						mapCategories.put(depenseCategorie.getId(), depenseCategorie);
					}
				}

				// Ajout des sous catégories
				for (CategorieDepense depenseSousCategorie : listeAllCategories) {
					if(!depenseSousCategorie.isCategorie()){
						CategorieDepense categorieParente = mapCategories.get(depenseSousCategorie.getIdCategorieParente());
						if(categorieParente != null){
							depenseSousCategorie.setCategorieParente(categorieParente);
							categorieParente.getListeSSCategories().add(depenseSousCategorie);
						}
						mapSSCategories.put(depenseSousCategorie.getId(), depenseSousCategorie);
					}
				}
			}
			catch(Exception e){
				return new ArrayList<>();
			}
		}
		return new ArrayList<>(mapCategories.values());
	}



	/**
	 * @return la catégorie
	 * @param id identifiant de la catégorie
	 */
	public CategorieDepense chargeCategorieParId(String id) {

		if(mapCategories.size() == 0){
			chargeCategories();
		}
		// Recherche parmi les catégories
		CategorieDepense categorie = mapCategories.get(id);

		// Sinon les sous catégories
		if(categorie == null){
			categorie = mapSSCategories.get(id);
		}
		return categorie;
	}




	/**
	 * Suppression des données en mémoire
	 */
	public void resetData(){
		mapCategories.clear();
		mapSSCategories.clear();
	}
}
