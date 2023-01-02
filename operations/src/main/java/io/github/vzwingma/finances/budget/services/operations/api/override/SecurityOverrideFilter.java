package io.github.vzwingma.finances.budget.services.operations.api.override;
import io.github.vzwingma.finances.budget.services.communs.api.security.AbstractAPISecurityFilter;

import java.io.IOException;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class SecurityOverrideFilter extends AbstractAPISecurityFilter implements ContainerRequestFilter {

    public void filter(ContainerRequestContext requestContext) throws IOException {
        super.filter(requestContext);
    }
}