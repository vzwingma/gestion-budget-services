package io.github.vzwingma.finances.budget.services.utilisateurs.api;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
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

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class UtilisateursResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get(UtilisateursApiUrlEnum.USERS_BASE)
          .then()
             .statusCode(200)
                .body(containsString("API Budget - utilisateurs"));
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
        given() .when().get(UtilisateursApiUrlEnum.USERS_BASE + UtilisateursApiUrlEnum.USERS_ACCESS_DATE)
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
        given() .when().get(UtilisateursApiUrlEnum.USERS_BASE + UtilisateursApiUrlEnum.USERS_PREFS)
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
        given() .when().get(UtilisateursApiUrlEnum.USERS_BASE + UtilisateursApiUrlEnum.USERS_ACCESS_DATE)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("\"lastAccessTime\":null"));
    }

}