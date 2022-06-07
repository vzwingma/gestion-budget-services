package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.smallrye.mutiny.Uni;

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
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> info() {
        return super.info();
    }
}
