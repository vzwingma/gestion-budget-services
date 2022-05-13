package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageAppProvider;
import io.github.vzwingma.finances.budget.services.parametrages.test.MockDataCategoriesOperations;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ParametragesResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get(BudgetApiUrlEnum.PARAMS_BASE)
          .then()
             .statusCode(200)
             .body(is("API Parametrages"));
    }

    @InjectMock
    IParametrageAppProvider parametragesService;
    @Test
    void testGetCategories() {
        // Init des données
        Mockito.when(parametragesService.getCategories()).thenReturn(Uni.createFrom().item(MockDataCategoriesOperations.getListeTestCategories()));
        // Test
        given() .when().get(BudgetApiUrlEnum.PARAMS_BASE + BudgetApiUrlEnum.PARAMS_CATEGORIES)
                .then()
                    .statusCode(200)
                    .body(Matchers.containsString(MockDataCategoriesOperations.getListeTestCategories().get(0).getLibelle()));
    }
}