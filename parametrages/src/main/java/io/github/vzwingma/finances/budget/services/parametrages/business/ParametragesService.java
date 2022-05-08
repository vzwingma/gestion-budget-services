package io.github.vzwingma.finances.budget.services.parametrages.business;


import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRepository;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRequest;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service fournissant les paramètres
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor
public class ParametragesService implements IParametrageRequest {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesService.class);
	/**
	 * Service Provider Interface des données
	 */
	@Inject
	private IParametrageRepository dataParams;

	public ParametragesService(IParametrageRepository parametrageRepository){
		this.dataParams = parametrageRepository;
	}

	/**
	 * Liste des catégories en cache
	 * (A usage interne uniquement !!! Pour réponse : Clonage obligatoire)
	 */
	private List<CategorieOperation> listeCategories = new ArrayList<>();

	/**
	 * @return liste des catégories
	 */
	public List<CategorieOperation> getCategories(){
		if(listeCategories.isEmpty()){

			listeCategories = dataParams.chargeCategories()
				.subscribe().asStream()
				.filter(c -> c.getListeSSCategories() != null)
				.peek(c -> {
					LOGGER.debug("[{}][{}] {}", c.isActif() ? "v" : "X", c.getId(), c);
					c.getListeSSCategories().forEach(s -> LOGGER.debug("[{}][{}]\t\t{}", s.isActif() ? "v" : "X", s.getId(), s));
				})
				.map(this::cloneCategorie)
				.collect(Collectors.toList());
			return listeCategories;
		}
		else {
			return listeCategories;
		}
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	private CategorieOperation cloneCategorie(CategorieOperation categorie) {
		if(categorie != null && categorie.isActif()){
			CategorieOperation clone = new CategorieOperation();
			clone.setId(categorie.getId());
			clone.setActif(categorie.isActif());
			clone.setCategorie(categorie.isCategorie());
			// Pas de clone de la catégorie parente pour éviter les récursions
			clone.setLibelle(categorie.getLibelle());
			Set<CategorieOperation> setSSCatsClones = new HashSet<>();
			if(categorie.getListeSSCategories() != null && !categorie.getListeSSCategories().isEmpty()){

				categorie.getListeSSCategories()
						.stream()
						// #125
						.filter(CategorieOperation::isActif)
						.forEach(ssC -> {
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
