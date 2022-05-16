package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.api.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.comptes.business.ComptesService;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class ComptesResourceTest {


    @Test
    void testInfoEndpoint() {
        given()
          .when().get(BudgetApiUrlEnum.COMPTES_BASE)
          .then()
             .statusCode(200)
                .body(containsString("API Budget - comptes"));
    }

    @Inject
    IComptesAppProvider comptesService;

    @BeforeAll
    public static void init() {
        QuarkusMock.installMockForType(Mockito.mock(ComptesService.class), ComptesService.class);
    }

}