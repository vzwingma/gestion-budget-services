package io.github.vzwingma.finances.budget.services.operations.business;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IComptesServiceProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.github.vzwingma.finances.budget.services.operations.test.data.MockDataBudgets;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Month;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@QuarkusTest
public class BudgetServiceTest {

    private IOperationsAppProvider operationsAppProvider;

    private IBudgetAppProvider budgetAppProvider;
    private IOperationsRepository mockOperationDataProvider;
    private IComptesServiceProvider mockCompteServiceProvider;

    @BeforeEach
    public void setup() {
        mockOperationDataProvider = Mockito.mock(IOperationsRepository.class);
        mockCompteServiceProvider = Mockito.mock(IComptesServiceProvider.class);

        operationsAppProvider = Mockito.spy(new OperationsService());
        budgetAppProvider = Mockito.spy(new BudgetService(mockOperationDataProvider, mockCompteServiceProvider, operationsAppProvider));

    }

    @Test
    public void testGetBudgetWithNoCompte() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().failure(new DataNotFoundException("Compte introuvable")));

        // Test
        CompletionException thrown = assertThrows(CompletionException.class, () -> budgetAppProvider.getBudgetMensuel("C1", Month.JANUARY, 2020, "test")
                .await().indefinitely());
        assertEquals("io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException", thrown.getMessage());
    }


    /**
     * Test du chargement nominal d'un budget actif sur compte actif
     */
    @Test
    public void testGetBudgetInactifSurCompteActif() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getCompte()));

        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), eq(Month.JANUARY), eq(2022)))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetInactifCompteC1()));

        // Test
        BudgetMensuel budgetCharge = budgetAppProvider.getBudgetMensuel("C1", Month.JANUARY, 2022, "test")
                .await().indefinitely();

        // Assertion
        assertNotNull(budgetCharge);
        assertEquals("C1_2022_1", budgetCharge.getId());
        Mockito.verify(budgetAppProvider, Mockito.times(1)).getBudgetMensuel(eq("C1"), any(Month.class), anyInt(), eq("test"));
    }


    /**
     * Test du chargement nominal d'un budget actif sur compte actif
     */
    @Test
    public void testGetBudgetActifSurCompteActif() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getCompte()));

        BudgetMensuel b1 = MockDataBudgets.getBudgetActifCompteC1et1operationPrevue();
        BudgetMensuel b0 = MockDataBudgets.getBudgetPrecedentCompteC1();

        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), eq(Month.JANUARY), eq(2022)))
                .thenReturn(Uni.createFrom().item(b1));
        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), eq(Month.DECEMBER), eq(2021)))
                .thenReturn(Uni.createFrom().item(b0));


        // Test
        BudgetMensuel budgetCharge = budgetAppProvider.getBudgetMensuel("C1", Month.JANUARY, 2022, "test")
                .await().indefinitely();

        // Assertion
        assertNotNull(budgetCharge);
        assertEquals("C1_2022_01", budgetCharge.getId());
        // bien le recacul du solde de fin de budget précédent
        assertEquals(b0.getSoldes().getSoldeAtFinMoisCourant(), budgetCharge.getSoldes().getSoldeAtFinMoisPrecedent());
        // bien le recalcul global des soldes


        Mockito.verify(mockOperationDataProvider, Mockito.times(2)).chargeBudgetMensuel(any(CompteBancaire.class), any(Month.class), anyInt());
        Mockito.verify(mockOperationDataProvider, Mockito.times(1)).sauvegardeBudgetMensuel(any(BudgetMensuel.class));
    }

    @Test
    public void testGetNoBudgetCompteActif() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getCompte()));
        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), any(Month.class), anyInt()))
                .thenReturn(
                        Uni.createFrom().failure(new DataNotFoundException("Budget introuvable")));
        // Test
        BudgetMensuel budgetCharge = budgetAppProvider.getBudgetMensuel("C1", Month.MAY, 2022, "test")
                .await().indefinitely();

        // Assertion
        assertNotNull(budgetCharge);
        assertEquals("C1_2022_05", budgetCharge.getId());
        Mockito.verify(budgetAppProvider, Mockito.times(1)).getBudgetMensuel("C1", Month.MAY, 2022, "test");
        Mockito.verify(mockOperationDataProvider, Mockito.times(1)).sauvegardeBudgetMensuel(any(BudgetMensuel.class));
    }


    @Test
    public void testGetBudgetByParamsCompteInactif() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getCompteInactif()));
        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), any(Month.class), anyInt()))
                .thenReturn(
                  //      Uni.createFrom().failure(new DataNotFoundException("Budget introuvable")),
                        Uni.createFrom().item(MockDataBudgets.getBudgetInactifCompteC1()));
        // Test
        BudgetMensuel budgetCharge = budgetAppProvider.getBudgetMensuel("C1", Month.JANUARY, 2022, "test")
                .await().indefinitely();

        // Assertion
        assertNotNull(budgetCharge);
        assertEquals("C1_2022_1", budgetCharge.getId());
        Mockito.verify(budgetAppProvider, Mockito.times(1)).getBudgetMensuel("C1", Month.JANUARY, 2022, "test");
    }



    /**
     * Test #121
     */
    @Test
    void testCalculBudget(){
        assertNotNull(this.operationsAppProvider);
        BudgetMensuel budget = MockDataBudgets.getBudgetActifCompteC1et1operationPrevue();
        assertNotNull(budget);

        this.budgetAppProvider.recalculSoldes(budget);
        assertEquals(0, budget.getSoldes().getSoldeAtMaintenant().intValue());
        assertEquals(123, budget.getSoldes().getSoldeAtFinMoisCourant().intValue());

        budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        this.budgetAppProvider.recalculSoldes(budget);
        assertEquals(0, budget.getSoldes().getSoldeAtMaintenant().intValue());
        assertEquals(123, budget.getSoldes().getSoldeAtFinMoisCourant().intValue());
    }
}
