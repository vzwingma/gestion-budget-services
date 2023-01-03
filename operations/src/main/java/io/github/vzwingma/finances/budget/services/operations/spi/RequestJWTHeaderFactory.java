package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.*;
import java.util.concurrent.ExecutionException;

/**
 * Factory pour injecter le token JWT correspondant à l'utilisateur connecté. S'il existe, s'il n'est pas expiré
 */
public class RequestJWTHeaderFactory implements ClientHeadersFactory {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestJWTHeaderFactory.class);

    @Context
    SecurityContext securityContext;

    @Inject
    @CacheName("jwtCache")
    Cache cacheRawTokens;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        if(cacheRawTokens != null){
            String rawAuthJWT = getValidJWTToken(cacheRawTokens, securityContext.getUserPrincipal().getName());
            if(rawAuthJWT != null){
                result.add(HttpHeaders.AUTHORIZATION, "Bearer " + rawAuthJWT);
            }
        }
        return result;
    }


    /**
     * Recherche d'un token valide dans le cache
     * @param cacheKey cache
     */
    private String getValidJWTToken(Cache cacheKey, String userKey) {

        try {
            String rawAuthJWT = cacheKey.as(CaffeineCache.class).getIfPresent(userKey).get().toString();

            // Revalidation de la validité du token
            if(rawAuthJWT != null){
                JWTIdToken idToken = JWTUtils.decodeJWT(rawAuthJWT);
                if(!idToken.isExpired()){
                    return rawAuthJWT;
                }
                else{
                    cacheKey.as(CaffeineCache.class).invalidate(userKey);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Erreur InterruptedException");
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
        return null;
    }
}