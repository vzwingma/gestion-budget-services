package io.github.vzwingma.finances.budget.services.comptes.business;

import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
public class ComptesServiceTest {

    private IComptesAppProvider comptesAppProvider;
    private IComptesRepository comptesRepository;

    @BeforeEach
    public void setup() {
    }

}
