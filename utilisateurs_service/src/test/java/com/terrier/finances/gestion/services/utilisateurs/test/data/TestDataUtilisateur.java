package com.terrier.finances.gestion.services.utilisateurs.test.data;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Jeu de données Catégorie
 */
public class TestDataUtilisateur {

    public static Utilisateur getTestUtilisateur(){
        Utilisateur userOK = new Utilisateur();
        userOK.setId("345345");
        userOK.setLogin("Test");
        userOK.setDernierAcces(LocalDateTime.now());
        userOK.setPrefsUtilisateur(new HashMap<>());
        userOK.getPrefsUtilisateur().put(UtilisateurPrefsEnum.PREFS_STATUT_NLLE_DEPENSE, "Nouvelle");
        return userOK;
    }

}
