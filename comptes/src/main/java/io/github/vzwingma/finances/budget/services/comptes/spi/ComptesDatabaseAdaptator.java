package io.github.vzwingma.finances.budget.services.comptes.spi;

import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service de données en MongoDB fournissant les comptes.
 * Adapteur du port {@link io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository}
 * @author vzwingma
 *
 */
@ApplicationScoped
public class ComptesDatabaseAdaptator implements IComptesRepository { // extends AbstractDatabaseServiceProvider<CategorieOperation>


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesDatabaseAdaptator.class);

}