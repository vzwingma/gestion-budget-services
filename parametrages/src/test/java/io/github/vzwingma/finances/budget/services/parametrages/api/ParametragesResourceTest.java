package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ParametragesResourceTest {

    @Test
    public void testInfoEndpoint() {
        given()
          .when().get("/parametres/v2")
          .then()
             .statusCode(200)
             .body(is("API Parametrages"));
    }

}