package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
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
 * Adapteur du port {@link IComptesAppProvider}
 * @author vzwingma
 *
 */
@Path(BudgetApiUrlEnum.COMPTES_BASE)
public class ComptesResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComptesResource.class);


    @Inject
    IComptesAppProvider services;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> info() {
        return Uni.createFrom().item("API Comptes");
    }

}