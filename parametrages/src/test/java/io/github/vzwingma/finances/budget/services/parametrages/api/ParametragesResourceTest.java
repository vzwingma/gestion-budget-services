package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthPayload;
import io.github.vzwingma.finances.budget.services.communs.data.model.JWTAuthToken;
import io.github.vzwingma.finances.budget.services.communs.data.model.JwtAuthHeader;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.security.JWTUtils;
import io.github.vzwingma.finances.budget.services.parametrages.api.enums.ParametragesAPIEnum;
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

import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
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
        given()
            .header(HttpHeaders.AUTHORIZATION, getTestJWTAuthHeader())
        .when().get(ParametragesAPIEnum.PARAMS_BASE + ParametragesAPIEnum.PARAMS_CATEGORIES)
        .then()
            .statusCode(200)
            .body(Matchers.containsString(MockDataCategoriesOperations.getListeTestCategories().get(0).getLibelle()));
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