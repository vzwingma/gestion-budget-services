package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service de données en MongoDB fournissant les opérations.
 * Adapteur du port {@link IOperationRepository}
 * @author vzwingma
 *
 */
@ApplicationScoped
public class OperationDatabaseAdaptor implements IOperationRepository { // extends AbstractDatabaseServiceProvider<CategorieOperation>


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationDatabaseAdaptor.class);

}