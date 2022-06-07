package io.github.vzwingma.finances.budget.services.communs.api;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Resource de base pour les API REST de l'application
 */

public abstract class AbstractAPIResource {

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    public Uni<String> info() {
        return Uni.createFrom().item("API Budget - " + applicationName + " - " + applicationVersion);
    }


}
