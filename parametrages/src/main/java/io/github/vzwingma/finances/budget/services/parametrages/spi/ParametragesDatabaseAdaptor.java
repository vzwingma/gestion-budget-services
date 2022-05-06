package io.github.vzwingma.finances.budget.services.parametrages.spi;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRepository;
import io.smallrye.mutiny.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

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
	public Multi<CategorieOperation> chargeCategories() {

		try {
			LOGGER.trace("Chargement des catégories en BDD");

			Multi<CategorieOperation> getCategories = findAll().stream();
			LOGGER.info("Chargement des catégories en BDD terminé");
			return getCategories;
		} catch (Exception e) {
			LOGGER.error("Erreur lors de la connexion à la BDD", e);
			return Multi.createFrom().failure(new Exception("Erreur lors de la connexion à la BDD"));
		}
	}
}