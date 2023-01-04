package io.github.vzwingma.finances.budget.services.communs.utils;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTIdToken;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import static org.junit.jupiter.api.Assertions.*;

import io.vertx.core.json.DecodeException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Classe de test JWT
 */
public class TestJWTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TestJWTUtils.class);
    private static final String ID_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjhlMGFjZjg5MWUwOTAwOTFlZjFhNWU3ZTY0YmFiMjgwZmQxNDQ3ZmEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1NTA0MzE5MjgxMzgtZWRlc3RqMjhyazVhMGVtazU0NnA3aWkyOGRsNWJvYzUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1NTA0MzE5MjgxMzgtZWRlc3RqMjhyazVhMGVtazU0NnA3aWkyOGRsNWJvYzUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxMDI1MjcyMjA5NTAwNzY2ODgiLCJlbWFpbCI6InZpbmNlbnQuendpbmdtYW5uQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiSzZBNjRfUlJyMm5KbVk1YWNBanVjdyIsIm5hbWUiOiJWaW5jZW50IFp3aW5nbWFubiIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BRWRGVHA0VjVITGx1dktDNWdJYW9GRFU4a1Q0emJmSk94dE5lRmNYTjM4NnA1bz1zOTYtYyIsImdpdmVuX25hbWUiOiJWaW5jZW50IiwiZmFtaWx5X25hbWUiOiJad2luZ21hbm4iLCJsb2NhbGUiOiJmciIsImlhdCI6MTY3MjY2MDAwMiwiZXhwIjoxNjcyNjYzNjAyfQ.afhBX1myxxHsqqhAB8aksbBQo0Si6v141rAIC-RGNE6zjoJXIkJsN9dPpHLjP9VXJzFNIZLa8O01qwLBZj6qF4vFOqHgrVKVIwGL0UNpbvdf8yfHd401EexFpxn1UwUccC2DnDANxA3s4DZXNAVKIraMBPC5AtKDmbguGdh5Gh1s4mQtPNPy_f9hxhAumOiHAwrAzxdFsrsQR003WNbnWNCknvu87vp3ZUWO5yMvPVtAo0_Eyyg4HoZMX6XeRs6vKf6OY4NLAOkH0z8BgCqgQ2yV68RROPQ2Ic3icbAQANa3GxD5cqQ5YTJQ_hcsIt1y2XD9r9rGETBk3nzEH9tnCQ";


    @Test
    public void testDecode(){

        JWTIdToken token = JWTUtils.decodeJWT(ID_TOKEN);
        assertNotNull(token);
        assertNotNull(token.getHeader());
        assertEquals("RS256", token.getHeader().getAlg());

        assertNotNull(token.getPayload());
        assertEquals("https://accounts.google.com", token.getPayload().getIss());
        assertNotNull(token.issuedAt());
        assertEquals("2023-01-02T12:46:42", token.issuedAt().toString());
        assertNotNull(token.expiredAt());
        assertEquals("2023-01-02T13:46:42", token.expiredAt().toString());

        LOG.info(LocalDateTime.now().toString());
        LOG.info(token.expiredAt().toString());
        assertTrue(token.isExpired());

        JWTUtils.encodeJWT(token);

    }

    @Test
    public void testDecodeBadToken() {

        assertThrows(DecodeException.class, () -> JWTUtils.decodeJWT("BaDToken" + ID_TOKEN));
    }

    @Test
    public void testEncode(){
        JWTIdToken token = JWTUtils.decodeJWT(ID_TOKEN);
        String encode = JWTUtils.encodeJWT(token);
        assertNotNull(encode);
        assertEquals("eyJhbGciOiJSUzI1NiIsImtpZCI6IjhlMGFjZjg5MWUwOTAwOTFlZjFhNWU3ZTY0YmFiMjgwZmQxNDQ3ZmEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1NTA0MzE5MjgxMzgtZWRlc3RqMjhyazVhMGVtazU0NnA3aWkyOGRsNWJvYzUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1NTA0MzE5MjgxMzgtZWRlc3RqMjhyazVhMGVtazU0NnA3aWkyOGRsNWJvYzUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxMDI1MjcyMjA5NTAwNzY2ODgiLCJlbWFpbCI6InZpbmNlbnQuendpbmdtYW5uQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiSzZBNjRfUlJyMm5KbVk1YWNBanVjdyIsIm5hbWUiOiJWaW5jZW50IFp3aW5nbWFubiIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BRWRGVHA0VjVITGx1dktDNWdJYW9GRFU4a1Q0emJmSk94dE5lRmNYTjM4NnA1bz1zOTYtYyIsImdpdmVuX25hbWUiOiJWaW5jZW50IiwiZmFtaWx5X25hbWUiOiJad2luZ21hbm4iLCJsb2NhbGUiOiJmciIsImlhdCI6MTY3MjY2MDAwMiwiZXhwIjoxNjcyNjYzNjAyfQ", encode);
    }

    @Test
    public void testValidToken(){
        String rawToken = generateValidToken();
        assertNotNull(rawToken);
        JWTIdToken token = JWTUtils.decodeJWT(rawToken);
        assertFalse(token.isExpired());
    }

    public static String generateValidToken(){
        JWTIdToken token = JWTUtils.decodeJWT(ID_TOKEN);

        token.getPayload().setIat(BudgetDateTimeUtils.getSecondsFromLocalDateTime(LocalDateTime.now()));
        token.getPayload().setExp(BudgetDateTimeUtils.getSecondsFromLocalDateTime(LocalDateTime.now().plusHours(1)));
        return JWTUtils.encodeJWT(token);
    }

}
