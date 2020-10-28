package com.terrier.finances.gestion.services.budgets.test.spi;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budgets.data.TestDataOperations;
import com.terrier.finances.gestion.services.budgets.spi.OperationsDatabaseAdaptator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Classe du Service Provider DB des Opérations
 */
class TestOperationsDBAdaptor {

    private OperationsDatabaseAdaptator db;
    private MongoOperations mockMongo;

    @BeforeEach
    public void initMocks() {
        // Préparation
        db = spy(new OperationsDatabaseAdaptator());
        mockMongo = mock(MongoOperations.class);
        db.setMongoOperations(mockMongo);
    }

    @Test
    void testChargeBudgetInDB() throws BudgetNotFoundException {
        // Préparation
        when(mockMongo.findById(eq("C1_2018_1"), eq(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        BudgetMensuel budget = db.chargeBudgetMensuel("C1_2018_1");

        // Vérification
        Assert.assertNotNull(budget);
        Assert.assertEquals("C1_2018_1", budget.getId());
    }

    @Test
    void testChargeBudgetParCompteInDB() throws BudgetNotFoundException {
        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        CompteBancaire c1= new CompteBancaire();
        c1.setId("C1");
        BudgetMensuel compte = db.chargeBudgetMensuel(c1, Month.JANUARY, 2018);

        // Vérification
        Assert.assertNotNull(compte);
        Assert.assertEquals("C1_2018_1", compte.getId());
    }




    @Test
    public void testBudgetActif() throws BudgetNotFoundException{
        // Préparation
        when(mockMongo.findById(anyString(), eq(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        boolean actif = db.isBudgetActif("C1_2018_1");

        // Vérification
        assertTrue(actif);
    }


    @Test
    public void testChargerLignesOperations() throws DataNotFoundException{
        // Préparation
        when(mockMongo.findById(eq("C1_2018_1"), eq(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        List<LigneOperation> operations = db.chargerLignesDepenses("C1_2018_1");

        // Vérification
        assertNotNull(operations);
        assertEquals(2, operations.size());
    }

    @Test
    public void testChargerLibelles() {
        // Préparation
        List<BudgetMensuel> liste = new ArrayList<>();
        liste.add(TestDataOperations.getBudgetCompteC1());
        liste.add(TestDataOperations.getBudgetCompteC2());

        when(mockMongo.find(any(Query.class), eq(BudgetMensuel.class)))
                .thenReturn(liste);

        // Lancement
        Set<String> libelles = db.chargeLibellesOperations("C1", 2018);

        // Vérification
        assertNotNull(libelles);
        assertEquals(1, libelles.size());
    }

    @Test
    public void testSauvegardeBudgetMensuel(){

        // Préparation
        when(mockMongo.save(any(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        String id = db.sauvegardeBudgetMensuel(TestDataOperations.getBudgetCompteC1());

        // Vérification
        assertNotNull(id);
        assertEquals("C1_2018_1", id);
    }


    @Test
    public void getPremierDernierBudgets() throws DataNotFoundException{

        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(BudgetMensuel.class)))
                .thenReturn(TestDataOperations.getBudgetCompteC1()).thenReturn(TestDataOperations.getBudgetCompteC2());

        // Lancement
        BudgetMensuel[] budgets = db.getPremierDernierBudgets("C1");

        // Vérification
        assertEquals("C1_2018_1", budgets[0].getId());
        assertEquals("C1_2018_7", budgets[1].getId());
    }
}