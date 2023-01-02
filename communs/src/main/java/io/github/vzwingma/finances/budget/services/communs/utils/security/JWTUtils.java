package io.github.vzwingma.finances.budget.services.communs.utils.security;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
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
        try {
            String[] chunks = base64JWT.split("\\.");
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            return new JWTIdToken(Json.decodeValue(header, JWTIdToken.JWTHeader.class), Json.decodeValue(payload, JWTIdToken.JWTPayload.class));
        }
        catch (Exception e){
            LOG.error("Erreur lors du décodage du token [{}]", base64JWT);
            throw new DecodeException("Erreur lors du décodage du token");
        }
    }


    public static String encodeJWT(JWTIdToken jwt) throws DecodeException {
        LOG.trace("Encodage du Token JWT : {}", jwt);

        Base64.Encoder encoder = Base64.getUrlEncoder();
        try {
            String headerJson = Json.encode(jwt.getHeader());
            String payloadJson = Json.encode(jwt.getPayload());
            String chunks0 = encoder.encodeToString(headerJson.getBytes()).replaceAll("==", "");
            String chunks1 = encoder.encodeToString(payloadJson.getBytes()).replaceAll("==", "");
            return chunks0+"."+chunks1;
        }
        catch (Exception e){
            LOG.error("Erreur lors de l'encodage du token [{}]", jwt);
            throw new EncodeException("Erreur lors de l'encodage du token");
        }
    }
}
