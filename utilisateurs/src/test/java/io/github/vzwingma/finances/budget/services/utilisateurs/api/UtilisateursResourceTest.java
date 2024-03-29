package io.github.vzwingma.finances.budget.services.utilisateurs.api;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthPayload;
import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthToken;
import io.github.vzwingma.finances.budget.services.communs.data.model.JwtAuthHeader;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import io.github.vzwingma.finances.budget.services.utilisateurs.api.enums.UtilisateursAPIEnum;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.UtilisateursService;
import io.github.vzwingma.finances.budget.services.utilisateurs.test.data.MockDataUtilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursAppProvider;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

@QuarkusTest
class UtilisateursResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get("/_info")
          .then()
             .statusCode(200)
                .body(containsStringIgnoringCase("utilisateurs"));
    }


    @Inject
    IUtilisateursAppProvider utilisateurService;

    @BeforeAll
    public static void init() {
        QuarkusMock.installMockForType(Mockito.mock(UtilisateursService.class), UtilisateursService.class);
    }
    @Test
    void testGetLastAccessDate() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(utilisateurService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(utilisateurExpected));
        // Test
        given()
            .header(HttpHeaders.AUTHORIZATION, getTestJWTAuthHeader())
        .when()
            .get(UtilisateursAPIEnum.USERS_BASE + UtilisateursAPIEnum.USERS_ACCESS_DATE)
        .then()
            .statusCode(200)
            .body(Matchers.containsString("lastAccessTime"));
    }


    @Test
    void testGetPreferences() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(utilisateurService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(utilisateurExpected));
        // Test
        given()
                .header(HttpHeaders.AUTHORIZATION, getTestJWTAuthHeader())
                .when()
                .get(UtilisateursAPIEnum.USERS_BASE + UtilisateursAPIEnum.USERS_PREFS)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("\"preferences\":{\"PREFS_STATUT_NLLE_DEPENSE\":\"Nouvelle\"}"));
    }


    @Test
    void testForUtilisateurUnkown() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(utilisateurService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().failure(new DataNotFoundException("Utilisateur introuvable")));
        // Test
        given()
            .header(HttpHeaders.AUTHORIZATION, getTestJWTAuthHeader())
        .when()
            .get(UtilisateursAPIEnum.USERS_BASE + UtilisateursAPIEnum.USERS_ACCESS_DATE)
        .then()
            .statusCode(200)
            .body(Matchers.containsString("\"lastAccessTime\":null"));
    }


    private String getTestJWTAuthHeader(){
        JwtAuthHeader h = new JwtAuthHeader();
        JWTAuthPayload p = new JWTAuthPayload();
        p.setName("Test");
        p.setFamily_name("Test");
        p.setGiven_name("Test");
        p.setIat(BudgetDateTimeUtils.getSecondsFromLocalDateTime(LocalDateTime.now()));
        p.setExp(BudgetDateTimeUtils.getSecondsFromLocalDateTime(LocalDateTime.now().plusHours(1)));
        return "Bearer " + JWTUtils.encodeJWT(new JWTAuthToken(h, p));
    }
}