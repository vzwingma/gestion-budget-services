package com.terrier.finances.gestion.services.comptes.test.spi;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.comptes.spi.ComptesDatabaseAdaptor;
import com.terrier.finances.gestion.services.comptes.test.data.TestDataComptes;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/**
 * Classe du Service Provider DB des Compte
 */
class TestComptesDBAdaptor {

    private ComptesDatabaseAdaptor db;
    private MongoOperations mockMongo;

    @BeforeEach
    public void initMocks() {
        // Préparation
        db = spy(new ComptesDatabaseAdaptor());
        mockMongo = mock(MongoOperations.class);
        db.setMongoOperations(mockMongo);
    }

    @Test
    void testChargeComptesInDB() throws DataNotFoundException {
        // Préparation
        when(mockMongo.find(any(Query.class), eq(CompteBancaire.class))).thenReturn(TestDataComptes.getListeComptes());

        // Lancement
        List<CompteBancaire> listeCompte = db.chargeComptes("Test");

        // Vérification
        Assert.assertNotNull(listeCompte);
        Assert.assertEquals("ALibelle3", listeCompte.get(0).getLibelle());
    }

    @Test
    void testChargeComptesParIdInDB() throws DataNotFoundException {
        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(CompteBancaire.class))).thenReturn(TestDataComptes.getCompte1());

        // Lancement
        CompteBancaire compte = db.chargeCompteParId("C1","Test");

        // Vérification
        Assert.assertNotNull(compte);
        Assert.assertEquals("Libelle1", compte.getLibelle());
    }

    @Test
    void testChargeComptesParIdNotProprietaireInDB() {
        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(CompteBancaire.class))).thenReturn(TestDataComptes.getCompte1());

        // Lancement
        // Vérification
        assertThrows(DataNotFoundException.class, () -> db.chargeCompteParId("C1","Test2"));
    }


    @Test
    void testCompteActif() throws DataNotFoundException {
        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(CompteBancaire.class))).thenReturn(TestDataComptes.getCompte1());

        // Lancement
        Boolean isActif = db.isCompteActif("C1");

        // Vérification
        Assert.assertTrue(isActif);
    }
}