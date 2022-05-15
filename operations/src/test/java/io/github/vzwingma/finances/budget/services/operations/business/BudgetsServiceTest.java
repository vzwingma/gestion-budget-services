package io.github.vzwingma.finances.budget.services.operations.business;

import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

@QuarkusTest
public class BudgetsServiceTest {

    private IOperationsAppProvider budgetsAppProvider;
    private IOperationRepository budgetsServiceProvider;

    @BeforeEach
    public void setup() {
        budgetsServiceProvider = Mockito.mock(IOperationRepository.class);
        budgetsAppProvider = Mockito.spy(new OperationsService(budgetsServiceProvider));
    }

}
