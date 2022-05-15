package io.github.vzwingma.finances.budget.services.utilisateurs.spi;


import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.MockDataUtilisateur;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestUtilisateursDBAdaptor {

    private UtilisateurDatabaseAdaptor db;
    private ReactiveMongoCollection mockMongo;


    @BeforeEach
    void initMocks(){
        // Préparation
        db = Mockito.spy(new UtilisateurDatabaseAdaptor());
        mockMongo = Mockito.mock(ReactiveMongoCollection.class);

    }

    @Test
    void testChargeUtilisateurInDB()  {

        // Préparation
        ReactivePanacheQuery mockQuery = Mockito.mock(ReactivePanacheQuery.class);
        Mockito.when(mockQuery.singleResult()).thenReturn(Uni.createFrom().item(MockDataUtilisateur.getTestUtilisateur()));
        Mockito.when(db.find(Mockito.anyString(), Mockito.anyString())).thenReturn(mockQuery);

        // Lancement
        Utilisateur user = db.chargeUtilisateur("Test").await().indefinitely();

        // Vérification
        Assertions.assertNotNull(user);
        Assertions.assertEquals("345345", user.getId());
        Assertions.assertEquals("Test", user.getLogin());
    //    Mockito.verify(mockMongo, Mockito.times(1)).findOne(any(Query.class), eq(Utilisateur.class));
    }

}
