package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.parametrages.api.enums.ParametragesApiUrlEnum;
import io.github.vzwingma.finances.budget.services.parametrages.business.ParametragesService;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageAppProvider;
import io.github.vzwingma.finances.budget.services.parametrages.test.data.MockDataCategoriesOperations;
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
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

@QuarkusTest
class ParametragesResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get("/_info")
          .then()
             .statusCode(200)
             .body(containsStringIgnoringCase("param"));
    }

    @Inject
    IParametrageAppProvider parametragesService;

    @BeforeAll
    public static void init() {
        QuarkusMock.installMockForType(Mockito.mock(ParametragesService.class), ParametragesService.class);
    }

    @Test
    void testGetCategories() {
        // Init des données
        Mockito.when(parametragesService.getCategories()).thenReturn(Uni.createFrom().item(MockDataCategoriesOperations.getListeTestCategories()));
        // Test
        given() .when().get(ParametragesApiUrlEnum.PARAMS_BASE + ParametragesApiUrlEnum.PARAMS_CATEGORIES)
                .then()
                    .statusCode(200)
                    .body(Matchers.containsString(MockDataCategoriesOperations.getListeTestCategories().get(0).getLibelle()));
    }
}