package io.github.vzwingma.finances.budget.services.utilisateurs.api;

import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.MockDataUtilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursAppProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class UtilisateursResourceTest {

    @Test
    public void testInfoEndpoint() {
        given()
          .when().get(BudgetApiUrlEnum.USERS_BASE)
          .then()
             .statusCode(200)
             .body(is("API Utilisateurs"));
    }



    @InjectMock
    IUtilisateursAppProvider parametragesService;
    @Test
    void testGetLastAccessDate() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(parametragesService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(utilisateurExpected));
        // Test
        given() .when().get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL)
                .then()
                    .statusCode(200)
                    .body(Matchers.containsString("lastAccessTime"));
    }


    @Test
    void testGetPreferences() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(parametragesService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(utilisateurExpected));
        // Test
        given() .when().get(BudgetApiUrlEnum.USERS_PREFS_FULL)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("\"preferences\":{\"PREFS_STATUT_NLLE_DEPENSE\":\"Nouvelle\"}"));
    }


    @Test
    void testForUtilisateurUnkown() {
        // Init des données
        Utilisateur utilisateurExpected = MockDataUtilisateur.getTestUtilisateurWithDate();
        Mockito.when(parametragesService.getUtilisateur(Mockito.anyString()))
                .thenReturn(Uni.createFrom().failure(new DataNotFoundException("Utilisateur introuvable")));
        // Test
        given() .when().get(BudgetApiUrlEnum.USERS_ACCESS_DATE_FULL)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("\"lastAccessTime\":null"));
    }

}