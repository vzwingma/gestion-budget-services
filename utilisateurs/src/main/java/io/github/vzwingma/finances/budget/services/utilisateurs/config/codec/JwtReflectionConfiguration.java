package io.github.vzwingma.finances.budget.services.utilisateurs.config.codec;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthPayload;
import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthToken;
import io.github.vzwingma.finances.budget.services.communs.data.model.JwtAuthHeader;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = { JWTAuthToken.class, JWTAuthPayload.class, JwtAuthHeader.class })
public class JwtReflectionConfiguration   {
}
