package com.terrier.finances.gestion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;

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
	 * @return la liste des catégories
	 */
	public List<CategorieDepense> chargeCategories() {
		if(mapCategories.size() == 0){
			try{
				List<CategorieDepense> listeAllCategories =  getMongoOperation().findAll(CategorieDepense.class);
				// Ajout des catégories
				listeAllCategories
				.stream()
				.filter(CategorieDepense::isCategorie)
				.forEach(c -> mapCategories.put(c.getId(), c));

				// Ajout des sous catégories
				listeAllCategories
				.stream()
				.filter(c -> !c.isCategorie())
				.forEach(c -> {
					CategorieDepense categorieParente = mapCategories.get(c.getIdCategorieParente());
					if(categorieParente != null){
						c.setCategorieParente(categorieParente);
						categorieParente.getListeSSCategories().add(c);
					}
					mapSSCategories.put(c.getId(), c);
				});
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
