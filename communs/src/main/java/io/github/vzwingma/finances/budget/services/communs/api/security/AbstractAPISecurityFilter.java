package io.github.vzwingma.finances.budget.services.communs.api.security;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CaffeineCache;
import io.vertx.core.json.DecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * Filtre HTTP de sécurité des API, vérification du token JWT et des rôles
 */
public class AbstractAPISecurityFilter implements ContainerRequestFilter {


    private final Logger LOG = LoggerFactory.getLogger(AbstractAPISecurityFilter.class);



    /**
     * Filtre de sécurité sur JWT
     * @param requestContext requête
     * @throws IOException erreur de décodage
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        filter(requestContext, null);
    }
    /**
     * Filtre de sécurité sur JWT
     * @param requestContext requête
     * @throws DecodeException erreur de décodage
     */
    public void filter(ContainerRequestContext requestContext, Cache cacheKey) throws DecodeException {

        String auth = getAuthBearer(requestContext.getHeaders().get(HttpHeaders.AUTHORIZATION.toLowerCase(Locale.ROOT)));
        if(auth != null){
            JWTIdToken idToken = JWTUtils.decodeJWT(auth);
            requestContext.setSecurityContext(new SecurityOverrideContext(requestContext, idToken, auth));
            if(cacheKey != null && !idToken.isExpired()){
                LOG.trace("Mise en cache du token de {}", idToken.getPayload().getName());
                cacheKey.as(CaffeineCache.class).put(requestContext.getSecurityContext().getUserPrincipal().getName(), CompletableFuture.completedFuture(auth));
            }
        }
        else{
            requestContext.setSecurityContext(new AnonymousSecurityContext());
        }
    }


    protected String getAuthBearer(List<String> authBearer){
        if(authBearer != null && authBearer.size() > 0) {
            Optional<String> accessToken = authBearer.stream()
                    .filter(a -> a.startsWith("Bearer "))
                    .map(a -> a.replaceAll("Bearer ", ""))
                    .findFirst();
            return accessToken.orElse(null);
        }
        else{
            LOG.trace("Auth is null");
            return null;
        }
    }
}