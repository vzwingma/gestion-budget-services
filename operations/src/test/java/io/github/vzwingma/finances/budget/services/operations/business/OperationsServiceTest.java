package io.github.vzwingma.finances.budget.services.operations.business;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.IdsCategoriesEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.github.vzwingma.finances.budget.services.operations.spi.IParametragesServiceProvider;
import io.github.vzwingma.finances.budget.services.operations.test.data.MockDataBudgets;
import io.github.vzwingma.finances.budget.services.operations.test.data.MockDataOperations;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class OperationsServiceTest {

    private OperationsService operationsAppProvider;

    private IParametragesServiceProvider parametragesServiceProvider;
    private IBudgetAppProvider budgetAppProvider;
    private IOperationsRepository mockOperationDataProvider;

    @BeforeEach
    public void setup() {
        mockOperationDataProvider = Mockito.mock(IOperationsRepository.class);
        operationsAppProvider = Mockito.spy(new OperationsService());
        budgetAppProvider = Mockito.mock(BudgetService.class);
        operationsAppProvider.setDataOperationsProvider(mockOperationDataProvider);
        operationsAppProvider.setBudgetService(budgetAppProvider);
        parametragesServiceProvider = Mockito.mock(IParametragesServiceProvider.class);
        operationsAppProvider.setParametragesService(parametragesServiceProvider);
    }

    @Test
    void testSetDerniereOperationKO(){
        // When
        Mockito.when(budgetAppProvider.getBudgetMensuel(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et3operationsRealisees()));
        // Test

        CompletionException thrown = assertThrows(CompletionException.class, () -> operationsAppProvider.setLigneAsDerniereOperation("Test", "Test")
                .await().indefinitely());
        assertEquals("io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException", thrown.getMessage());
        Mockito.verify(mockOperationDataProvider, Mockito.never()).sauvegardeBudgetMensuel(Mockito.any());
    }


    @Test
    void testSetDerniereOperation(){
        // When
        Mockito.when(budgetAppProvider.getBudgetMensuel(Mockito.anyString()))
                .thenReturn(Uni.createFrom().item(MockDataBudgets.getBudgetActifCompteC1et3operationsRealisees()));
        // Test
        assertTrue(operationsAppProvider.setLigneAsDerniereOperation("Test", "C1B2_L3").await().indefinitely());
        Mockito.verify(mockOperationDataProvider, Mockito.times(1)).sauvegardeBudgetMensuel(Mockito.any());
    }


    @Test
    void testUpdateOperation(){

        // When
        List<LigneOperation> listeOperations = new ArrayList<>();
        listeOperations.add(MockDataOperations.getOperationPrelevement());
        // Opération à ajouter
        LigneOperation operation = MockDataOperations.getOperationPrelevement();
        operation.setEtat(EtatOperationEnum.REALISEE);
        // Test
        List<LigneOperation> operationsAJour = operationsAppProvider.addOperation(listeOperations, operation);
        assertEquals(1, operationsAJour.size());
        assertEquals(EtatOperationEnum.REALISEE, operationsAJour.get(0).getEtat());
        assertNotNull(operationsAJour.get(0).getAutresInfos().getDateOperation());
    }



    @Test
    void testAddOperation(){

        // When
        List<LigneOperation> listeOperations = new ArrayList<>();
        listeOperations.add(MockDataOperations.getOperationPrelevement());
        // Opération à ajouter
        LigneOperation operation = MockDataOperations.getOperationPrelevement();
        operation.setId("Test2");
        operation.setEtat(EtatOperationEnum.REALISEE);
        // Test
        List<LigneOperation> operationsAJour = operationsAppProvider.addOperation(listeOperations, operation);
        assertEquals(2, operationsAJour.size());
    }


    @Test
    void testAddOperationIntercompte(){

        // When
        List<LigneOperation> listeOperations = new ArrayList<>();
        listeOperations.add(MockDataOperations.getOperationPrelevement());
        // Opération à ajouter
        LigneOperation operation = MockDataOperations.getOperationIntercompte();
        operation.setEtat(EtatOperationEnum.REALISEE);
        // Test
        List<LigneOperation> operationsAJour = operationsAppProvider.addOperationIntercompte(listeOperations, operation, "vers " + operation.getLibelle());
        assertEquals(2, operationsAJour.size());
        assertEquals(EtatOperationEnum.PREVUE, operationsAJour.get(1).getEtat());
    }


    @Test
    void testAddOperationIntercompteReportee(){

        // When
        List<LigneOperation> listeOperations = new ArrayList<>();
        listeOperations.add(MockDataOperations.getOperationPrelevement());
        // Opération à ajouter
        LigneOperation operation = MockDataOperations.getOperationIntercompte();
        operation.setEtat(EtatOperationEnum.REPORTEE);
        // Test
        List<LigneOperation> operationsAJour = operationsAppProvider.addOperationIntercompte(listeOperations, operation, "vers " + operation.getLibelle());
        assertEquals(2, operationsAJour.size());
        assertEquals(EtatOperationEnum.REPORTEE, operationsAJour.get(1).getEtat());
    }

    @Test
    void testAddOperationRemboursementCatFailure(){

        // When
        Mockito.when(parametragesServiceProvider.getCategorieParId(Mockito.anyString()))
                .thenReturn(Uni.createFrom().failure(new DataNotFoundException("Impossible de trouver la catégorie")));

        // Test
        CompletionException thrown = Assertions.assertThrows(CompletionException.class, () -> operationsAppProvider.createOperationRemboursement(MockDataOperations.getOperationRemboursement()).await().indefinitely());
        assertEquals("io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException", thrown.getMessage());
        Mockito.verify(mockOperationDataProvider, Mockito.never()).sauvegardeBudgetMensuel(Mockito.any());

    }

    @Test
    void testAddOperationRemboursementCatUnkown(){

        // When
        Mockito.when(parametragesServiceProvider.getCategorieParId(Mockito.anyString())).thenReturn(Uni.createFrom().nullItem());

        // Test
        CompletionException thrown = Assertions.assertThrows(CompletionException.class, () -> operationsAppProvider.createOperationRemboursement(MockDataOperations.getOperationRemboursement()).await().indefinitely());
        assertEquals("io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException", thrown.getMessage());
        Mockito.verify(mockOperationDataProvider, Mockito.never()).sauvegardeBudgetMensuel(Mockito.any());

    }

    @Test
    void testAddOperationRemboursement(){

        // When
        CategorieOperations dep = new CategorieOperations(IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId());
        CategorieOperations cat = new CategorieOperations(IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId());
        dep.setCategorieParente(cat);
        Mockito.when(parametragesServiceProvider.getCategorieParId(Mockito.anyString())).thenReturn(Uni.createFrom().item(dep));
        // Test
        LigneOperation operationRemb = operationsAppProvider.createOperationRemboursement(MockDataOperations.getOperationRemboursement()).await().indefinitely();

        assertNotNull(operationRemb);
        assertEquals("[Remboursement] TestRemboursement", operationRemb.getLibelle());
    }
}
