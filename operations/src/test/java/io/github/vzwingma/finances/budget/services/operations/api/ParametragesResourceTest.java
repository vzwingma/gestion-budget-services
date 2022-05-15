package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.operations.business.OperationsService;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetApiUrlEnum;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class BudgetsResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get(BudgetApiUrlEnum.PARAMS_BASE)
          .then()
             .statusCode(200)
             .body(is("API Budgets"));
    }

    @Inject
    IOperationsAppProvider budgetService;

    @BeforeAll
    public static void init() {
        QuarkusMock.installMockForType(Mockito.mock(OperationsService.class), OperationsService.class);
    }

}