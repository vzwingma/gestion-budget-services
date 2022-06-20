package io.github.vzwingma.finances.budget.services.communs.api;

import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

/**
 * Interceptor for logging des requests et des responses
 */
public abstract class AbstractAPILoggerInterceptor {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Logger requête
     * @param requestContext context de la requête
     */
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        // Replace pattern-breaking characters
        String path = requestContext.getUriInfo().getPath().replaceAll("[\n\r\t]", "_");
        LOG.debug("[HTTP][uri:{} {}]", requestContext.getMethod(), path);
    }

    /**
     * Logger réponse
     * @param responseContext context de la réponse
     */
    public void postMatchingFilter(ContainerResponseContext responseContext) {
        BusinessTraceContext.getclear();
        LOG.debug("[HTTP][{}] {}", responseContext.getStatus(), responseContext.getStatusInfo().getReasonPhrase());
    }

}
