package io.github.vzwingma.finances.budget.services.communs.api;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource de base pour les API REST de l'application
 */
public abstract  class AbstractAPIResource {

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    @GET
    @Path("/_info")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> info() {
        return Uni.createFrom().item("API Budget - " + applicationName + " - " + applicationVersion);
    }


}
