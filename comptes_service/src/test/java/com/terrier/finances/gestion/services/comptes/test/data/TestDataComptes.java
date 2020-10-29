package com.terrier.finances.gestion.services.comptes.test.data;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Jeu de données Comptes
 */
public class TestDataComptes {

    public static List<CompteBancaire> getListeComptes(){

        List<CompteBancaire> comptes = new ArrayList<CompteBancaire>();
        CompteBancaire c1 = new CompteBancaire();
        c1.setActif(true);
        c1.setId("C1");
        c1.setLibelle("Libelle1");
        c1.setOrdre(1);
        c1.setProprietaire(c1.new Proprietaire());
        c1.getProprietaire().setLogin("Test");
        comptes.add(c1);
        CompteBancaire c2 = new CompteBancaire();
        c2.setActif(true);
        c2.setId("C2");
        c2.setLibelle("Libelle2");
        c2.setOrdre(2);
        c2.setProprietaire(c1.new Proprietaire());
        c2.getProprietaire().setLogin("Test");
        comptes.add(c2);
        CompteBancaire a3 = new CompteBancaire();
        a3.setActif(true);
        a3.setId("A3");
        a3.setLibelle("ALibelle3");
        a3.setOrdre(0);
        comptes.add(a3);
        return comptes;
    }

    public static CompteBancaire getCompte1(){
        return getListeComptes().get(0);
    }

}
