package com.terrier.finances.gestion.services.parametrages.business;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRepository;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRequest;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

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
@Service
@NoArgsConstructor
public class ParametragesService extends AbstractBusinessService implements IParametrageRequest {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesService.class);
	/**
	 * Service Provider Interface des données
	 */
	private IParametrageRepository dataParams;

	public ParametragesService(@Autowired IParametrageRepository parametrageRepository){
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
			this.listeCategories = dataParams.chargeCategories();
		}
		return listeCategories.stream().map(this::cloneCategorie).collect(Collectors.toList());
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

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Paramétrages");
	}
}
