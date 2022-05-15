package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Controleur REST -
 * Adapteur du port {@link IOperationsAppProvider}
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.BUDGET_BASE)
public class OperationsResource {

    private static final Logger LOG = LoggerFactory.getLogger(OperationsResource.class);


    @Inject
    IOperationsAppProvider budgetService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> info() {
        return Uni.createFrom().item("API Opérations");
    }


}