package com.terrier.finances.gestion.services.comptes.test.business;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;
import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRepository;
import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRequest;
import com.terrier.finances.gestion.services.comptes.test.data.TestDataComptes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test de l'hexagone Compte Service
 */
public class TestComptesService {

    // Préparation
    IComptesRepository spi;
    IComptesRequest comptesRequest;

    @BeforeEach
    public void initMocks(){
        // Préparation
        spi = mock(IComptesRepository.class);
        comptesRequest = spy(new ComptesService(spi));
    }

    @Test
    public void testGetListeComptes() throws DataNotFoundException {

        // Préparation
        when(spi.chargeComptes(anyString())).thenReturn(TestDataComptes.getListeComptes());

        // Lancement du test
        List<CompteBancaire> listeComptes = comptesRequest.getComptesUtilisateur("Test");

        // Vérification
        assertNotNull(listeComptes);
        assertEquals(3, listeComptes.size());
        // 1 seul appel à la BDD
        verify(spi, times(1)).chargeComptes(anyString());
    }


    @Test
    public void testCompteParId() throws DataNotFoundException {

        // Préparation
        when(spi.chargeCompteParId(anyString(), any())).thenReturn(TestDataComptes.getCompte1());

        // Lancement du test
        CompteBancaire compte = comptesRequest.getCompteById("C1", "Test");

        // Vérification
        assertNotNull(compte);
        assertEquals("C1", compte.getId());
        // 1 seul appel à la BDD
        verify(spi, times(1)).chargeCompteParId(anyString(), anyString());
    }



    @Test
    public void testCompteActif() throws DataNotFoundException {

        // Préparation
        when(spi.isCompteActif(anyString())).thenReturn(true);

        // Lancement du test
        Boolean compteActif = comptesRequest.isCompteActif("C1");

        // Vérification
        assertTrue(compteActif);
    }
}
