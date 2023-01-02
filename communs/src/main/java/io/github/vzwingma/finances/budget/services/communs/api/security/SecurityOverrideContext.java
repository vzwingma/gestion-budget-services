package io.github.vzwingma.finances.budget.services.communs.api.security;

import com.sun.security.auth.UserPrincipal;
import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * SecurityContext issus du token JWT OIDC Google
 */
public class SecurityOverrideContext implements SecurityContext {

    String authorizationValue;
    JWTIdToken idToken;

    public SecurityOverrideContext(ContainerRequestContext requestContext, JWTIdToken idToken){
        this.authorizationValue = requestContext.getHeaderString("Authorization");
        this.idToken = idToken;
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
        return idToken.getPayload().isEmail_verified();
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
