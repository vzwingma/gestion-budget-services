package io.github.vzwingma.finances.budget.services.parametrages.spi;


import io.github.vzwingma.finances.budget.services.communs.data.parametrages.model.CategorieOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Classe du Service Provider DB des paramétrages
 */
class TestParametragesDBAdaptor {

    private ParametragesDatabaseAdaptor db;
//    private MongoOperations mockMongo;

    @BeforeEach
    public void initMocks(){
        // Préparation
        db = Mockito.spy(new ParametragesDatabaseAdaptor());
        //mockMongo = mock(MongoOperations.class);
        //db.setMongoOperations(mockMongo);
    }

    @Test
    void testChargeCategoriesInDB(){

        // Préparation
        //Mockito.when(db.findAll(CategorieOperation.class)).thenReturn(TestDataCategorieOperation.getListeTestCategories());

        // Lancement
        List<CategorieOperation> cats = db.chargeCategories();

        // Vérification
        assertNotNull(cats);
        assertEquals(1, cats.size());
       // assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", cats.iterator().next().getId());
       // assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", cats.iterator().next().getListeSSCategories().iterator().next().getId());
    }
}
