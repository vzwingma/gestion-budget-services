package io.github.vzwingma.finances.budget.services.parametrages.business;

import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRepository;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageRequest;
import io.github.vzwingma.finances.budget.services.parametrages.test.TestDataCategoriesOperations;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ParametragesServiceTest {

    private IParametrageRequest parametrageRequest;
    private IParametrageRepository spi;

    @BeforeEach
    public void setup() {
        spi = Mockito.mock(IParametrageRepository.class);
        parametrageRequest = Mockito.spy(new ParametragesService(spi));

        Mockito.when(spi.chargeCategories()).thenReturn(Multi.createFrom().items(TestDataCategoriesOperations.getListeTestCategories().stream()));
    }

    @Test
    void testGetListeCategories(){
        // Lancement du test
        List<CategorieOperation> listeCat = parametrageRequest.getCategories().await().indefinitely();
        // Vérification
        assertNotNull(listeCat);
        assertEquals(1, listeCat.size());
        // 1 seul appel à la BDD
        Mockito.verify(spi, Mockito.times(1)).chargeCategories();
        assertEquals(1, listeCat.get(0).getListeSSCategories().size());
    }
}
