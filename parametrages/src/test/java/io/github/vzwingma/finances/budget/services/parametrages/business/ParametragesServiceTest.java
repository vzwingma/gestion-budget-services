package io.github.vzwingma.finances.budget.services.parametrages.business;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametragesRepository;
import io.github.vzwingma.finances.budget.services.parametrages.business.ports.IParametrageAppProvider;
import io.github.vzwingma.finances.budget.services.parametrages.test.data.MockDataCategoriesOperations;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ParametragesServiceTest {

    private IParametrageAppProvider parametrageAppProvider;
    private IParametragesRepository parametrageServiceProvider;

    @BeforeEach
    public void setup() {
        parametrageServiceProvider = Mockito.mock(IParametragesRepository.class);
        parametrageAppProvider = Mockito.spy(new ParametragesService(parametrageServiceProvider));

        Mockito.when(parametrageServiceProvider.chargeCategories()).thenReturn(Multi.createFrom().items(MockDataCategoriesOperations.getListeTestCategories().stream()));
    }

    @Test
    void testGetListeCategories(){
        // Lancement du test
        List<CategorieOperations> listeCat = parametrageAppProvider.getCategories().await().indefinitely();
        // Vérification
        assertNotNull(listeCat);
        assertEquals(1, listeCat.size());
        // 1 seul appel à la BDD
        Mockito.verify(parametrageServiceProvider, Mockito.times(1)).chargeCategories();
        assertEquals(1, listeCat.get(0).getListeSSCategories().size());
    }
}
