package io.github.vzwingma.finances.budget.services.communs.utils.security;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * Classe utilitaire de décodage du token JWT ID_TOKEN de Google
 */
public class JWTUtils {


    private static final Logger LOG = LoggerFactory.getLogger(JWTUtils.class);

    /**
     * Décodage d'un token JWT
     * @param base64JWT token en Base64
     * @throws DecodeException de décodage si le token n'est pas bien formé
     */
    public static JWTIdToken decodeJWT(String base64JWT) throws DecodeException {
        LOG.trace("Décodage du Token JWT : {}", base64JWT);
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = base64JWT.split("\\.");
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        return new JWTIdToken(Json.decodeValue(header, JWTIdToken.JWTHeader.class), Json.decodeValue(payload, JWTIdToken.JWTPayload.class));
    }
}
