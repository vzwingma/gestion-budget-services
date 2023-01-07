package io.github.vzwingma.finances.budget.services.communs.data.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Header d'un token JWT
 */
@Introspected
@Setter
@Getter
@NoArgsConstructor
public class JwtAuthHeader {
    private String alg;
    private String kid;
    private String typ;
}
