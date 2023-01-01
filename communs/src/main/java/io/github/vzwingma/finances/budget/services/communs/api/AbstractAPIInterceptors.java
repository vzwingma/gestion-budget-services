package io.github.vzwingma.finances.budget.services.communs.api;

import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.netty.handler.codec.HeadersUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Interceptor for logging et sécurité des requests et des responses
 */
public abstract class AbstractAPIInterceptors {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Logger requête
     * @param requestContext context de la requête
     */
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        // Replace pattern-breaking characters
        String path = requestContext.getUriInfo().getPath().replaceAll("[\n\r\t]", "_");

        requestContext.getHeaders().forEach((h, v) -> {
        //    LOG.info("{}:{}", h, v);
        });
        getAuthBearer(requestContext.getHeaders().get(HttpHeaders.AUTHORIZATION.toLowerCase(Locale.ROOT)));
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


    private String getAuthBearer(List<String> authBearer){
        if(authBearer != null && authBearer.size() > 0) {
            Optional<String> accessToken = authBearer.stream()
                                                    .filter(a -> a.startsWith("Bearer "))
                                                    .map(a -> a.replaceAll("Bearer ", ""))
                                                    .findFirst();
            return accessToken.orElse(null);
        }
        else{
            LOG.warn("Auth is null");
            return null;
        }
    }
}
