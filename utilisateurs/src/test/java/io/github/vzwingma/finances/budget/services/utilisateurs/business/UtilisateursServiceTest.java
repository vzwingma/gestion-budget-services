package io.github.vzwingma.finances.budget.services.utilisateurs.business;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.MockDataUtilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursRepository;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.ports.IUtilisateursAppProvider;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Classe de test du service UtilisateursService
 */
public class UtilisateursServiceTest {


    private IUtilisateursAppProvider appProvider;
    private IUtilisateursRepository serviceDataProvider;

    @BeforeEach
    public void setup() {
        serviceDataProvider = Mockito.mock(IUtilisateursRepository.class);
        appProvider = Mockito.spy(new UtilisateursService(serviceDataProvider));

        Mockito.when(serviceDataProvider.chargeUtilisateur(Mockito.anyString())).thenReturn(Uni.createFrom().item(MockDataUtilisateur.getTestUtilisateur()));
    }

    @Test
    void testGetUtilisateur() throws DataNotFoundException {
        // Lancement du test
        Utilisateur utilisateur = appProvider.getUtilisateur("Test").await().indefinitely();
        // Vérification
        assertNotNull(utilisateur);
        assertEquals("54aa7db30bc460e1aeb95596", utilisateur.getId().toString());
        assertNotNull(utilisateur.getDernierAcces());
        // 1 seul appel à la BDD pour charger l'utilisateur et 1 pour le mettre à jour
        Mockito.verify(serviceDataProvider, Mockito.times(1)).chargeUtilisateur(Mockito.anyString());
        Mockito.verify(serviceDataProvider, Mockito.times(1)).majUtilisateur(utilisateur);
    }
}
