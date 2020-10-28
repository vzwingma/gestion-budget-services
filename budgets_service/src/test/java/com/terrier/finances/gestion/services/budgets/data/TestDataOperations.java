package com.terrier.finances.gestion.services.budgets.data;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Jeu de données Opérations
 */
public class TestDataOperations {


    public static BudgetMensuel getBudgetCompteC1(){
        // Budget
        CompteBancaire c1 = new CompteBancaire();
        c1.setActif(true);
        c1.setId("C1");
        c1.setLibelle("Libelle1");
        c1.setOrdre(1);

        BudgetMensuel bo = new BudgetMensuel();
        bo.setIdCompteBancaire(c1.getId());
        bo.setMois(Month.JANUARY);
        bo.setAnnee(2018);
        bo.setActif(true);
        bo.setId("C1_2018_1");
        bo.getSoldes().setSoldeAtFinMoisCourant(0D);
        bo.getSoldes().setSoldeAtMaintenant(1000D);
        bo.setDateMiseAJour(LocalDateTime.now());
        bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        return bo;

    }

}
