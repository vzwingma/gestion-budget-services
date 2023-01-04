package io.github.vzwingma.finances.budget.services.communs.api.security;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
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


/**
 * Filtre HTTP de sécurité des API, vérification du token JWT et des rôles
 */
public class AbstractAPISecurityFilter implements ContainerRequestFilter {


    private final Logger LOG = LoggerFactory.getLogger(AbstractAPISecurityFilter.class);


    /**
     * Filtre de sécurité sur JWT
     * @param requestContext requête
     * @throws DecodeException erreur de décodage
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String auth = getAuthBearerFromHeaders(requestContext.getHeaders().get(HttpHeaders.AUTHORIZATION.toLowerCase(Locale.ROOT)));
        if (auth != null) {
            try {
                JWTIdToken idToken = JWTUtils.decodeJWT(auth);
                requestContext.setSecurityContext(new SecurityOverrideContext(idToken, auth));
                return;
            } catch (DecodeException e) {
                LOG.error("Erreur lors du décodage du token JWT : {}", auth);
            }
        }
        requestContext.setSecurityContext(new AnonymousSecurityContext());
    }


    /**
     * Récupération de l'Auth Bearer à partir des entête
     * @param authBearer liste des entêtes
     * @return l'auth Bearer si elle existe
     */
    protected String getAuthBearerFromHeaders(List<String> authBearer){
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