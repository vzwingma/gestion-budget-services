package com.terrier.finances.gestion.services.budgets.data;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;

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

    private static CompteBancaire getCompte(){
        CompteBancaire c1 = new CompteBancaire();
        c1.setActif(true);
        c1.setId("C1");
        c1.setLibelle("Libelle1");
        c1.setOrdre(1);
        return c1;
    }

    public static BudgetMensuel getBudgetCompteC1(){
        // Budget
        BudgetMensuel bo = new BudgetMensuel();
        bo.setIdCompteBancaire(getCompte().getId());
        bo.setMois(Month.JANUARY);
        bo.setAnnee(2018);
        bo.setActif(false);
        bo.setId(getCompte().getId()+"_2018_1");
        bo.getSoldes().setSoldeAtFinMoisCourant(0D);
        bo.getSoldes().setSoldeAtMaintenant(1000D);
        bo.setDateMiseAJour(LocalDateTime.now());
        bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);

        LigneOperation lo = new LigneOperation();
        lo.setId(getCompte().getId()+"B1_L1");
        lo.setEtat(EtatOperationEnum.REALISEE);
        lo.setLibelle("Opération 1");
        bo.getListeOperations().add(lo);
        LigneOperation lo2 = new LigneOperation();
        lo2.setId(getCompte().getId()+"B1_L2");
        lo2.setEtat(EtatOperationEnum.REALISEE);
        lo2.setLibelle("Opération 2");
        bo.getListeOperations().add(lo);
        return bo;

    }

    public static BudgetMensuel getBudgetCompteC2(){
        // Budget
        BudgetMensuel bo = new BudgetMensuel();
        bo.setIdCompteBancaire(getCompte().getId());
        bo.setMois(Month.JULY);
        bo.setAnnee(2018);
        bo.setActif(true);
        bo.setId(getCompte().getId()+"_2018_7");
        bo.getSoldes().setSoldeAtFinMoisCourant(0D);
        bo.getSoldes().setSoldeAtMaintenant(1000D);
        bo.setDateMiseAJour(LocalDateTime.now());
        bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);

        LigneOperation lo = new LigneOperation();
        lo.setId(getCompte().getId()+"B2_L1");
        lo.setEtat(EtatOperationEnum.REALISEE);
        lo.setLibelle("Opération 1");
        bo.getListeOperations().add(lo);
        LigneOperation lo2 = new LigneOperation();
        lo2.setId(getCompte().getId()+"B2_L2");
        lo2.setEtat(EtatOperationEnum.REALISEE);
        lo2.setLibelle("Opération 2");
        bo.getListeOperations().add(lo);
        return bo;

    }
}
