package io.github.vzwingma.finances.budget.services.parametrages.spi;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de données en MongoDB fournissant les paramètres
 * Adapteur du port {@link io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRepository}
 * @author vzwingma
 *
 */
@ApplicationScoped
public class ParametragesDatabaseAdaptor implements IParametrageRepository { // extends AbstractDatabaseServiceProvider<CategorieOperation>


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesDatabaseAdaptor.class);

	/**
	 * @return la liste des catégories
	 */
	public List<CategorieOperation> chargeCategories() {
			try{
				LOGGER.trace("Chargement des catégories en BDD");
				// Ajout des catégories
				List<CategorieOperation> listeCategories = new ArrayList<>(); //findAll();
				CategorieOperation c1 = new CategorieOperation();
				c1.setId("1");
				c1.setLibelle("Categorie 1");
				c1.setCategorie(true);
				listeCategories.add(c1);

				LOGGER.info("> Chargement des {} catégories <", listeCategories.size());
				listeCategories.stream()
						.filter(c -> c.getListeSSCategories() != null)
						.forEach(c -> {
							LOGGER.debug("[{}][{}] {}", c.isActif() ? "v" : "X", c.getId(), c);
							c.getListeSSCategories().forEach(s -> LOGGER.debug("[{}][{}]\t\t{}", s.isActif() ? "v" : "X", s.getId(), s));
				});
				return listeCategories;
			}
			catch(Exception e){
				LOGGER.error("Erreur lors du chargement des catégories");
				return new ArrayList<>();
			}
		}
	}
