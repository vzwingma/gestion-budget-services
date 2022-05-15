package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * Controleur REST -
 * Adapteur du port {@link IOperationsAppProvider}
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.BUDGET_BASE)
public class OperationsResource extends AbstractAPIResource {

    private static final Logger LOG = LoggerFactory.getLogger(OperationsResource.class);


    @Inject
    IOperationsAppProvider operationsService;

}