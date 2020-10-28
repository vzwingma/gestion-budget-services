package com.terrier.finances.gestion.services.budgets.test.spi;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testChargeBudgetInDB() throws DataNotFoundException, BudgetNotFoundException {
        // Préparation
        when(mockMongo.findById(eq("C1_2018_1"), eq(BudgetMensuel.class))).thenReturn(TestDataOperations.getBudgetCompteC1());

        // Lancement
        BudgetMensuel budget = db.chargeBudgetMensuel("C1_2018_1");

        // Vérification
        Assert.assertNotNull(budget);
        Assert.assertEquals("C1_2018_1", budget.getId());
    }

    @Test
    void testChargeBudgetParIdInDB() throws DataNotFoundException, BudgetNotFoundException {
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

}