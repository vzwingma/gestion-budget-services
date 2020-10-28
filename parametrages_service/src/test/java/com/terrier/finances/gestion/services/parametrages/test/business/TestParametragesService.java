package com.terrier.finances.gestion.services.parametrages.test.business;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.business.ParametragesService;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRepository;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRequest;
import com.terrier.finances.gestion.services.parametrages.test.data.TestDataCategorieOperation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Classe de test de l'hexagone Parametrage Service
 */
public class TestParametragesService {

    @Test
    public void testGetListeCategories(){

        // Préparation
        IParametrageRepository spi = mock(IParametrageRepository.class);
        IParametrageRequest parametrageRequest = spy(new ParametragesService(spi));

        when(spi.chargeCategories()).thenReturn(TestDataCategorieOperation.getListeTestCategories());

        // Lancement du test
        List<CategorieOperation> listeCat = parametrageRequest.getCategories();
        // Rechargement (et usage du cache)
        List<CategorieOperation> listeCat2 = parametrageRequest.getCategories();
        // Vérification
        assertNotNull(listeCat);
        assertEquals(1, listeCat.size());
        // 1 seul appel à la BDD
        verify(spi, times(1)).chargeCategories();
        assertNotNull(listeCat2);
        assertEquals(1, listeCat2.size());
        assertEquals(1, listeCat2.get(0).getListeSSCategories().size());
    }

}
