package io.github.vzwingma.finances.budget.services.utilisateurs.business.model;

import io.github.vzwingma.finances.budget.services.communs.data.enums.UtilisateurPrefsEnum;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Mock data d'un utilisateur
 */
public class MockDataUtilisateur {



    public static Utilisateur getTestUtilisateur(){
        Utilisateur userOK = new Utilisateur();
        userOK.setId(new ObjectId("54aa7db30bc460e1aeb95596"));
        userOK.setLogin("Test");
        userOK.setPrefsUtilisateur(new HashMap<>());
        userOK.getPrefsUtilisateur().put(UtilisateurPrefsEnum.PREFS_STATUT_NLLE_DEPENSE, "Nouvelle");
        return userOK;
    }

    public static Utilisateur getTestUtilisateurWithDate(){
        Utilisateur userOK = getTestUtilisateur();
        userOK.setDernierAcces(LocalDateTime.now());
        return userOK;
    }
}
