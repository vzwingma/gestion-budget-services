package io.github.vzwingma.finances.budget.services.operations.business;


import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationRepository;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service fournissant les budgets
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor
public class OperationsService implements IOperationsAppProvider {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsService.class);
	/**
	 * Service Provider Interface des données
	 */
	@Inject
	IOperationRepository dataParams;

	public OperationsService(IOperationRepository budgetsRepository){
		this.dataParams = budgetsRepository;
	}

}
