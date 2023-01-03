package io.github.vzwingma.finances.budget.services.operations.api.override;
import io.github.vzwingma.finances.budget.services.communs.api.security.AbstractAPISecurityFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class SecurityOverrideFilter extends AbstractAPISecurityFilter implements ContainerRequestFilter {

    public void filter(ContainerRequestContext requestContext) throws IOException {
        super.filter(requestContext);
    }
}