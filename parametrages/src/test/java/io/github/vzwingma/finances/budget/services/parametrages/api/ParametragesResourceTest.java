package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

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

}