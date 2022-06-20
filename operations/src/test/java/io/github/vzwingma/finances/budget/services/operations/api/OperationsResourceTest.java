package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.operations.api.enums.OperationsApiUrlEnum;
import io.github.vzwingma.finances.budget.services.operations.business.BudgetService;
import io.github.vzwingma.finances.budget.services.operations.business.OperationsService;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.test.data.MockDataBudgets;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import java.time.Month;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.*;

@QuarkusTest
class OperationsResourceTest {

    @Test
    void testInfoEndpoint() {
        given()
          .when().get("/_info")
          .then()
             .statusCode(200)
                .body(containsString("operations"));
    }

    @Inject
    IBudgetAppProvider budgetService;

    @Inject
    IOperationsAppProvider operationsService;
    @BeforeAll
    public static void init() {
        QuarkusMock.installMockForType(Mockito.mock(BudgetService.class), BudgetService.class);
        QuarkusMock.installMockForType(Mockito.mock(OperationsService.class), OperationsService.class);
    }

    /**
     * POST Set Actif OperationsApiUrlEnum.BUDGET_ETAT
     */
    @Test
    void tesSetEtatActif() {
        // Init des données
        Mockito.when(budgetService.setBudgetActif(anyString(), anyBoolean()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et1operationPrevue()));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_ETAT.replace(OperationsApiUrlEnum.PARAM_ID_BUDGET, "1")
                +"?actif=true";

        given() .when().post(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("true"));
    }

    /**
     * Test OperationsApiUrlEnum.BUDGET_ETAT
     */
    @Test
    void testIsActifOK() {
        // Init des données
        Mockito.when(budgetService.isBudgetMensuelActif(anyString()))
                .thenReturn(Uni.createFrom().item(Boolean.TRUE));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_ETAT.replace(OperationsApiUrlEnum.PARAM_ID_BUDGET, "1")
                +"?actif=true";

        given() .when().get(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("true"));
    }

    @Test
    void testIsActifNOK() {
        // Init des données
        Mockito.when(budgetService.isBudgetMensuelActif(anyString()))
                .thenReturn(Uni.createFrom().item(Boolean.FALSE));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_ETAT.replace(OperationsApiUrlEnum.PARAM_ID_BUDGET, "1")
                +"?actif=true";

        given() .when().get(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("false"));
    }

    @Test
    void testGetBudgetById() {
        // Init des données
        Mockito.when(budgetService.getBudgetMensuel(anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et1operationPrevue()));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_ID.replace(OperationsApiUrlEnum.PARAM_ID_BUDGET, "1");

        given() .when().get(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("TEST1"));
    }



    @Test
    void testGetBudgetByParams() {
        // Init des données
        Mockito.when(budgetService.getBudgetMensuel(anyString(), any(Month.class), anyInt()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et1operationPrevue()));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_QUERY + "?idCompte=1&mois=1&annee=2020";

        given() .when().get(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("TEST1"));
    }



    @Test
    void testGetBudgetByParamsKO() {
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE + OperationsApiUrlEnum.BUDGET_QUERY;

        given() .when().get(url)
                .then()
                .statusCode(500);
    }


    @Test
    void testReinitBudget() {
        // Init des données
        Mockito.when(budgetService.reinitialiserBudgetMensuel(anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et1operationPrevue()));
        // Test
        String url = OperationsApiUrlEnum.BUDGET_BASE
                + OperationsApiUrlEnum.BUDGET_ID.replace(OperationsApiUrlEnum.PARAM_ID_BUDGET, "1");

        given() .when().delete(url)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("TEST1"));
    }
}