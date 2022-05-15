package io.github.vzwingma.finances.budget.services.utilisateurs.business.model;

import io.github.vzwingma.finances.budget.services.communs.data.enums.UtilisateurDroitsEnum;
import io.github.vzwingma.finances.budget.services.communs.data.enums.UtilisateurPrefsEnum;
import org.bson.BsonReader;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
 * Test class for {@link UtilisateurPanacheCodec}.
 */
class TestUtilisateurPanacheCodec {


    @Test
    void testDecode() {

        final String utilisateurJSON = "{\"_id\":{\"$oid\":\"54aa7db30bc460e1aeb95596\"},\"login\":\"test\",\"dernierAcces\":{\"$date\":{\"$numberLong\":\"1537219119633\"}},\"prefsUtilisateur\":{\"PREFS_STATUT_NLLE_DEPENSE\":\"prevue\"},\"droits\":{\"DROIT_CLOTURE_BUDGET\":true,\"DROIT_RAZ_BUDGET\":true}}";
        
        BsonReader reader = new JsonReader(utilisateurJSON);
        Utilisateur utilisateur = new UtilisateurPanacheCodec().decode(reader, null);
        assertNotNull(utilisateur);
        assertEquals("test", utilisateur.getLogin());
        assertEquals("54aa7db30bc460e1aeb95596", utilisateur.getId().toString());
        assertNotNull(utilisateur.getDernierAcces());
        // validation des préférences
        assertEquals("prevue", utilisateur.getPrefsUtilisateur().get(UtilisateurPrefsEnum.PREFS_STATUT_NLLE_DEPENSE));
        // validation des droits
        assertEquals(true, utilisateur.getDroits().get(UtilisateurDroitsEnum.DROIT_CLOTURE_BUDGET));
        assertEquals(true, utilisateur.getDroits().get(UtilisateurDroitsEnum.DROIT_RAZ_BUDGET));
    }

    @Test
    void testType(){
        assertEquals(Utilisateur.class, new UtilisateurPanacheCodec().getEncoderClass());
    }


    /*
    @Test : test pour vérifier qu'un documentId est bien généré
     */
    @Test
    void testDocumentId(){
        Utilisateur user = new Utilisateur();
        assertNotNull(new UtilisateurPanacheCodec().getDocumentId(user).toString());

        Utilisateur user2 = new Utilisateur();
        user2.setId(new ObjectId("54aa7db30bc460e1aeb95596"));
        assertEquals("54aa7db30bc460e1aeb95596", (new UtilisateurPanacheCodec().getDocumentId(user2)).asString().getValue());
    }

}
