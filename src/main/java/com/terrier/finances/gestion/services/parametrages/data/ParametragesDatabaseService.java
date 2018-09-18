package com.terrier.finances.gestion.services.parametrages.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
	 * Liste des catégories (A usage interne uniquement !!! Pour réponse : Clonage obligatoire)
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
				LOGGER.info("[BD] Chargement des catégories en BDD");
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
		return listeCategories.stream().map(c -> cloneCategorie(c)).collect(Collectors.toList());
	}






	/**
	 * @return la catégorie
	 * @param id identifiant de la catégorie
	 */
	public CategorieOperation chargeCategorieParId(String id) {

		CategorieOperation categorie = null;
		List<CategorieOperation> listeCategories = chargeCategories();

		if(id != null){
			// Recherche parmi les catégories
			Optional<CategorieOperation> cat = listeCategories.parallelStream()
					.filter(c -> id.equals(c.getId()))
					.findFirst();
			if(cat.isPresent()){
				categorie = cat.get();
			}
			// Sinon les sous catégories
			else{
				Optional<CategorieOperation> ssCats = listeCategories.parallelStream()
						.flatMap(c -> c.getListeSSCategories().stream())
						.filter(ss -> id.equals(ss.getId()))
						.findFirst();
				if(ssCats.isPresent()){
					categorie = ssCats.get();
				}
			}
		}
		LOGGER.trace("[DB] Categorie by id [{}]->[{}]", id, categorie);
		return categorie;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	private CategorieOperation cloneCategorie(CategorieOperation categorie) {
		if(categorie != null){
			CategorieOperation clone = new CategorieOperation(categorie.getId());
			clone.setActif(categorie.isActif());
			clone.setCategorie(categorie.isCategorie());
			// Pas de clone de la catégorie parente pour éviter les récursions
			clone.setLibelle(categorie.getLibelle());
			Set<CategorieOperation> setSSCatsClones = new HashSet<>();
			if(categorie.getListeSSCategories() != null && !categorie.getListeSSCategories().isEmpty()){

				categorie.getListeSSCategories().stream().forEach(ssC -> {
					CategorieOperation ssCClone = cloneCategorie(ssC);
					// Réinjection de la catégorie parente
					ssCClone.setCategorieParente(clone);
					setSSCatsClones.add(ssCClone);
				});
			}
			clone.getListeSSCategories().addAll(setSSCatsClones);
			return clone;
		}
		return null;
	}
}
