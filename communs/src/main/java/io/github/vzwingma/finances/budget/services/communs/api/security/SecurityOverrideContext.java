package io.github.vzwingma.finances.budget.services.communs.api.security;

import com.sun.security.auth.UserPrincipal;
import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * SecurityContext issus du token JWT OIDC Google
 */
public class SecurityOverrideContext implements SecurityContext {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityOverrideContext.class);
    private final String authorizationValue;
    private final JWTIdToken idToken;

    @Getter
    private final String rawBase64Token;


    public SecurityOverrideContext(ContainerRequestContext requestContext, JWTIdToken idToken, String rawBase64Token){
        this.authorizationValue = requestContext.getHeaderString("Authorization");
        this.idToken = idToken;
        this.rawBase64Token = rawBase64Token;
    }

    @Override
    public Principal getUserPrincipal() {
        if(idToken != null){
            String login = this.idToken.getPayload().getGiven_name().substring(0, 1).toLowerCase() + this.idToken.getPayload().getFamily_name().substring(0, 7).toLowerCase();
            return new UserPrincipal(login);
        }
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        if(idToken.isExpired()){
            LOG.warn("L'utilisateur [{}] n'a pas de token JWT valide", getUserPrincipal().getName());
            return false;
        }
        return true;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return authorizationValue == null ? null : authorizationValue.split(" ")[0].trim();
    }
}
