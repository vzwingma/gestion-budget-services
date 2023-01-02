package io.github.vzwingma.finances.budget.services.operations.api.override;
import io.github.vzwingma.finances.budget.services.communs.api.security.AbstractAPISecurityFilter;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import java.io.IOException;


import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class SecurityOverrideFilter extends AbstractAPISecurityFilter implements ContainerRequestFilter {

    @Inject
    @CacheName("jwtCache")
    Cache cacheRawTokens;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        super.filter(requestContext, cacheRawTokens);
    }
}