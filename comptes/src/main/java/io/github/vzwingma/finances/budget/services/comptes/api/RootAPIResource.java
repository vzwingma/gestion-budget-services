package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.github.vzwingma.finances.budget.services.communs.data.model.Info;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource de base pour les API REST de l'application
 */
@Path("/")
public class RootAPIResource extends AbstractAPIResource {
    @GET
    @Path("_info")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Uni<Info> info() {
        return super.info();
    }
}
