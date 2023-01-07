package io.github.vzwingma.finances.budget.services.communs.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Header d'un token JWT
 */
@RegisterForReflection
@JsonDeserialize
@JsonSerialize
@Setter
@Getter
@NoArgsConstructor
public class JwtAuthHeader {
    private String alg;
    private String kid;
    private String typ;
}
