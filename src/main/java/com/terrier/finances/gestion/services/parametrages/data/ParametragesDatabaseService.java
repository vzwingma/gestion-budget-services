package com.terrier.finances.gestion.services.parametrages.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.communs.data.AbstractDatabaseService;
import com.terrier.finances.gestion.services.parametrages.model.CategorieOperationDTO;
import com.terrier.finances.gestion.services.parametrages.model.transformer.DataTransformerCategorieOperations;

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
	private List<CategorieOperation> listeCategories = new ArrayList<>();
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesDatabaseService.class);

	/**
	 * @return la liste des catégories
	 */
	public List<CategorieOperation> chargeCategories() {
		if(listeCategories.isEmpty()){
			try{
				// Ajout des catégories
				listeCategories = getMongoOperation().findAll(CategorieOperationDTO.class)
						.stream()
						.map(dto -> new DataTransformerCategorieOperations().transformDTOtoBO(dto))
						.collect(Collectors.toList());
			}
			catch(Exception e){
				LOGGER.error("[DB] Erreur lors du chargement des catégories");
			}
		}
		return listeCategories;
	}



	/**
	 * @return la catégorie
	 * @param id identifiant de la catégorie
	 */
	public CategorieOperation chargeCategorieParId(String id) {

		if(listeCategories.isEmpty()){
			chargeCategories();
		}
		if(id != null){
			// Recherche parmi les catégories
			Optional<CategorieOperation> cat = 
					listeCategories
					.parallelStream()
					.filter(c -> id.equals(c.getId()))
					.findFirst();
			if(cat.isPresent()){
				return cat.get();
			}
			// Sinon les sous catégories
			else{
				Optional<CategorieOperation> ssCat = listeCategories
						.parallelStream()
						.flatMap(c -> c.getListeSSCategories().stream())
						.filter(ss -> id.equals(ss.getId()))
						.findFirst();
				if(ssCat.isPresent()){
					return ssCat.get();
				}
			}
		}
		return null;
	}




	/**
	 * Suppression des données en mémoire
	 */
	public void resetData(){
		listeCategories.clear();
	}
}
