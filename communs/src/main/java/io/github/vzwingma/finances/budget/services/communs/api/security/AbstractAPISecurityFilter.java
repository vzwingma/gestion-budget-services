package io.github.vzwingma.finances.budget.services.communs.api.security;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.UserAccessForbiddenException;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


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
        String auth = getAuthBearer(requestContext.getHeaders().get(HttpHeaders.AUTHORIZATION.toLowerCase(Locale.ROOT)));

        if(auth != null){
            JWTIdToken idToken = JWTUtils.decodeJWT(auth);
            if(!idToken.isExpired()){
                requestContext.setSecurityContext(new SecurityOverrideContext(requestContext, idToken));
            }
            else{
                throw new UserAccessForbiddenException("Le token est expiré");
            }
        }
        else{
            throw new UserAccessForbiddenException("Le token JWT est manquant");
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
            LOG.warn("Auth is null");
            return null;
        }
    }
}