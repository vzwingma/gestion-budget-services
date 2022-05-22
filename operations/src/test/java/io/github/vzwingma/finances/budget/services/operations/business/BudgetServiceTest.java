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
    @Test
    public void testGetBudgetByParamsCompteActif() {

        // Initialisation
        Mockito.when(mockCompteServiceProvider.getCompteById(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getCompte()));
        Mockito.when(mockOperationDataProvider.chargeBudgetMensuel(any(CompteBancaire.class), any(Month.class), anyInt()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetCompteC1()));
        // Test
        BudgetMensuel budgetCharge = budgetAppProvider.getBudgetMensuel("C1", Month.JANUARY, 2022, "test")
                .await().indefinitely();

        // Assertion
        assertNotNull(budgetCharge);
        assertEquals("C1_2022_1", budgetCharge.getId());
        Mockito.verify(budgetAppProvider, Mockito.times(1)).getBudgetMensuel("C1", Month.JANUARY, 2022, "test");
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
                        Uni.createFrom().item(MockDataBudgets.getBudgetCompteC1()));
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
        BudgetMensuel budget = MockDataBudgets.getBudgetCompteC3OperationPrevue();
        assertNotNull(budget);

        this.budgetAppProvider.calculBudget(budget);
        assertEquals(0, budget.getSoldes().getSoldeAtMaintenant().intValue());
        assertEquals(123, budget.getSoldes().getSoldeAtFinMoisCourant().intValue());

        budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        this.budgetAppProvider.calculBudget(budget);
        assertEquals(0, budget.getSoldes().getSoldeAtMaintenant().intValue());
        assertEquals(123, budget.getSoldes().getSoldeAtFinMoisCourant().intValue());
    }
}
