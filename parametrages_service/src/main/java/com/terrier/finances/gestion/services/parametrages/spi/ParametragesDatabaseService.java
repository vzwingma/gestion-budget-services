package com.terrier.finances.gestion.services.parametrages.spi;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.services.communs.data.mongodb.AbstractDatabaseService;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de données en MongoDB fournissant les paramètres
 * @author vzwingma
 *
 */
@Repository
public class ParametragesDatabaseService extends AbstractDatabaseService<CategorieOperation> implements IParametrageRepository {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesDatabaseService.class);

	/**
	 * @return la liste des catégories
	 */
	public List<CategorieOperation> chargeCategories() {
			try{
				LOGGER.trace("Chargement des catégories en BDD");
				// Ajout des catégories
				List<CategorieOperation> listeCategories = findAll();
				
				LOGGER.info("> Chargement des {} catégories <", listeCategories.size());
				listeCategories.forEach(c -> {
					LOGGER.debug("[{}][{}] {}", c.isActif() ? "v" : "X", c.getId(), c);
					c.getListeSSCategories().forEach(s -> LOGGER.debug("[{}][{}]\t\t{}", s.isActif() ? "v" : "X", s.getId(), s));
				});
				return listeCategories;
			}
			catch(Exception e){
				LOGGER.error("Erreur lors du chargement des catégories");
				return null;
			}
		}
	}
