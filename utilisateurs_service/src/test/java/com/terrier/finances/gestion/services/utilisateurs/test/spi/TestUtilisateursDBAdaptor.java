package com.terrier.finances.gestion.services.utilisateurs.test.spi;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;
import com.terrier.finances.gestion.services.utilisateurs.spi.UtilisateurDatabaseAdaptor;
import com.terrier.finances.gestion.services.utilisateurs.test.data.TestDataUtilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test de l'adaptor {@link com.terrier.finances.gestion.services.utilisateurs.spi.UtilisateurDatabaseAdaptor}
 */
public class TestUtilisateursDBAdaptor {

    private UtilisateurDatabaseAdaptor db;
    private MongoOperations mockMongo;


    @BeforeEach
    void initMocks(){
        // Préparation
        db = spy(new UtilisateurDatabaseAdaptor());
        mockMongo = mock(MongoOperations.class);
        db.setMongoOperations(mockMongo);
    }

    @Test
    void testChargeUtilisateurInDB() throws DataNotFoundException {

        // Préparation
        when(mockMongo.findOne(any(Query.class), eq(Utilisateur.class))).thenReturn(TestDataUtilisateur.getTestUtilisateur());

        // Lancement
        Utilisateur user = db.chargeUtilisateur("Test");

        // Vérification
        assertNotNull(user);
        assertEquals("345345", user.getId());
        assertEquals("Test", user.getLogin());
        verify(mockMongo, times(1)).findOne(any(Query.class), eq(Utilisateur.class));
    }


    @Test
    void testMajUtilisateur() throws DataNotFoundException {

        // Préparation
        when(mockMongo.save(any(Utilisateur.class))).thenReturn(TestDataUtilisateur.getTestUtilisateur());

        // Lancement
        db.save(TestDataUtilisateur.getTestUtilisateur());

        // Vérification
        verify(mockMongo, times(1)).save(any(Utilisateur.class));
    }
}
