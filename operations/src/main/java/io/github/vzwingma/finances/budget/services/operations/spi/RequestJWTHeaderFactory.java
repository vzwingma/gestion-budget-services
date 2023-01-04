package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.*;

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

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();

        String rawAuthJWT = getValidJWTToken(securityContext.getAuthenticationScheme());
        if(rawAuthJWT != null){
            result.add(HttpHeaders.AUTHORIZATION, "Bearer " + rawAuthJWT);
        }
        return result;
    }


    /**
     * Recherche d'un token valide dans le cache
     * @param rawAuthJWT rawtJwt
     */
    private String getValidJWTToken(String rawAuthJWT) {

        // Revalidation de la validité du token
        if(rawAuthJWT != null){
            JWTIdToken idToken = JWTUtils.decodeJWT(rawAuthJWT);
            if(!idToken.isExpired()){
                return rawAuthJWT;
            }
        }
        return null;
    }
}