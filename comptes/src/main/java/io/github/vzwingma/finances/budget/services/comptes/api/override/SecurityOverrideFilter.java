package io.github.vzwingma.finances.budget.services.comptes.api.override;

import io.github.vzwingma.finances.budget.services.communs.api.security.AbstractAPISecurityFilter;
import io.vertx.core.json.DecodeException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class SecurityOverrideFilter extends AbstractAPISecurityFilter implements ContainerRequestFilter {


    public void filter(ContainerRequestContext requestContext) throws IOException {
        super.filter(requestContext);
    }
}