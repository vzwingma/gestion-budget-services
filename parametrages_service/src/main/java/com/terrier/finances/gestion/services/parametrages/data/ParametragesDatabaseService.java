package com.terrier.finances.gestion.services.parametrages.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;

/**
 * Service de données en MongoDB fournissant les paramètres
 * @author vzwingma
 *
 */
@Repository
public class ParametragesDatabaseService extends AbstractDatabaseService<CategorieOperation> {

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
				LOGGER.trace("Chargement des catégories en BDD");
				// Ajout des catégories
				listeCategories = findAll()
						.stream()
						.collect(Collectors.toList());
				
				LOGGER.info("> Chargement des {} catégories <", listeCategories.size());
				listeCategories.stream().forEachOrdered(c -> {
					LOGGER.debug("[{}][{}] {}", c.isActif() ? "v" : "X", c.getId(), c);
					c.getListeSSCategories().stream().forEachOrdered(s -> LOGGER.debug("[{}][{}]\t\t{}", s.isActif() ? "v" : "X", s.getId(), s));
				});
			}
			catch(Exception e){
				LOGGER.error("Erreur lors du chargement des catégories");
			}
		}
		return listeCategories.stream().map(this::cloneCategorie).collect(Collectors.toList());
	}






	/**
	 * @return la catégorie
	 * @param id identifiant de la catégorie
	 */
	public CategorieOperation getCategorieParId(String id) {
		CategorieOperation categorie = BudgetDataUtils.getCategorieById(id, chargeCategories());
		LOGGER.trace("Categorie by id [{}]->[{}]", id, categorie);
		return categorie;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	private CategorieOperation cloneCategorie(CategorieOperation categorie) {
		if(categorie != null){
			CategorieOperation clone = new CategorieOperation();
			clone.setId(categorie.getId());
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
				clone.setListeSSCategories(setSSCatsClones);
			}
			return clone;
		}
		return null;
	}
}
