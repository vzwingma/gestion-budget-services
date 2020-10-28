package com.terrier.finances.gestion.services.utilisateurs.test.business;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.utilisateurs.business.UtilisateursService;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;
import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRepository;
import com.terrier.finances.gestion.services.utilisateurs.business.port.IUtilisateursRequest;
import com.terrier.finances.gestion.services.utilisateurs.test.data.TestDataUtilisateur;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Classe de test de l'hexagone Utilisateurs Service
 */
public class TestUtilisateursService {


    private IUtilisateursRepository spi;
    private IUtilisateursRequest utilisateurService;

    @BeforeEach
    public void initMocks(){
        // Préparation
        spi = mock(IUtilisateursRepository.class);
        utilisateurService = spy(new UtilisateursService(spi));
    }

    @Test
    public void testGetUtilisateur() throws DataNotFoundException {
        // Préparation
        when(spi.chargeUtilisateur(eq("Test"))).thenReturn(TestDataUtilisateur.getTestUtilisateur());

        // Lancement
        Utilisateur userTest = utilisateurService.getUtilisateur("Test");

        //Vérification
        Assert.assertNotNull(userTest);
        Assert.assertEquals("345345", userTest.getId());

        verify(spi, times(1)).chargeUtilisateur(anyString());
        verify(spi, times(1)).majUtilisateur(any(Utilisateur.class));
    }

    @Test
    public void testGetUtilisateurKO() throws DataNotFoundException {
        // Préparation
        when(spi.chargeUtilisateur(anyString())).thenThrow(DataNotFoundException.class);

        // Lancement
        Utilisateur userTest = utilisateurService.getUtilisateur("Test2");

        //Vérification
        Assert.assertNull(userTest);
        verify(spi, times(1)).chargeUtilisateur(anyString());
    }
}
