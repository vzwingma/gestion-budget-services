package io.github.vzwingma.finances.budget.services.comptes.business;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import io.github.vzwingma.finances.budget.services.comptes.test.MockDataComptes;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
public class ComptesServiceTest {

    private IComptesAppProvider comptesAppProvider;
    private IComptesRepository comptesRepository;

    @BeforeEach
    public void setup() {
        comptesRepository = Mockito.mock(IComptesRepository.class);
        comptesAppProvider = Mockito.spy(new ComptesService(comptesRepository));
    }

    @Test
    public void testGetComptes(){

        Mockito.when(comptesRepository.chargeComptes(Mockito.eq("test"))).thenReturn(Multi.createFrom().items(MockDataComptes.getListeComptes().stream()));

        List<CompteBancaire> comptes = comptesAppProvider.getComptesUtilisateur("test").await().indefinitely();
        Assertions.assertNotNull(comptes);
        Assertions.assertEquals(3, comptes.size());
        Assertions.assertEquals("Libelle0", comptes.get(0).getLibelle());

    }

    @Test
    public void testGetCompteById(){

        Mockito.when(comptesRepository.chargeCompteParId(Mockito.eq("A3"), Mockito.anyString())).thenReturn(Uni.createFrom().item(MockDataComptes.getCompte1()));

        CompteBancaire compte = comptesAppProvider.getCompteById("A3", "test").await().indefinitely();
        Assertions.assertNotNull(compte);
        Assertions.assertEquals("Libelle1", compte.getLibelle());

    }

    @Test
    public void testGetCompteActif(){
        Mockito.when(comptesRepository.isCompteActif(Mockito.eq("A3"))).thenReturn(Uni.createFrom().item(Boolean.TRUE));

        Assertions.assertTrue(comptesAppProvider.isCompteActif("A3").await().indefinitely());
    }


}
