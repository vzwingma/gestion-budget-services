package io.github.vzwingma.finances.budget.services.operations.test.data;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.utils.BudgetDataUtils;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

/**
 * Jeu de données Budgets
 */
public class MockDataBudgets {


    public static CompteBancaire getCompte(){
        CompteBancaire c1 = new CompteBancaire();
        c1.setActif(true);
        c1.setId("C1");
        c1.setLibelle("Libelle1");
        c1.setProprietaire(new CompteBancaire.Proprietaire());
        c1.getProprietaire().setLogin("test");
        c1.setOrdre(1);
        return c1;
    }

    public static CompteBancaire getCompteInactif(){
        CompteBancaire c2 = new CompteBancaire();
        c2.setActif(false);
        c2.setId("C2");
        c2.setLibelle("Libelle2");
        c2.setProprietaire(new CompteBancaire.Proprietaire());
        c2.getProprietaire().setLogin("test");
        c2.setOrdre(2);
        return c2;
    }

    public static BudgetMensuel getBudgetInactifCompteC1(){
        // Budget
        BudgetMensuel bo = new BudgetMensuel();
        bo.setIdCompteBancaire(getCompte().getId());
        bo.setMois(Month.JANUARY);
        bo.setAnnee(2022);
        bo.setActif(false);
        bo.setId(getCompte().getId()+"_2022_1");

        bo.getSoldes().setSoldeAtFinMoisCourant(0D);
        bo.getSoldes().setSoldeAtMaintenant(1000D);
        bo.setDateMiseAJour(LocalDateTime.now());
        bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);

        return bo;
    }

    public static BudgetMensuel getBudgetActifCompteC1et1operationPrevue(){

        BudgetMensuel budget = new BudgetMensuel();
        budget.setMois(Month.JANUARY);
        budget.setAnnee(2022);
        budget.setIdCompteBancaire(getCompte().getId());
        budget.setId(BudgetDataUtils.getBudgetId(budget.getIdCompteBancaire(), budget.getMois(), budget.getAnnee()));

        budget.setActif(true);
        budget.setDateMiseAJour(LocalDateTime.now().minusDays(1));
        // Soldes
        budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        budget.setListeOperations(new ArrayList<>());
        BudgetDataUtils.razCalculs(budget);
        // Opération
        budget.getListeOperations().add(MockDataOperations.getOperationPrelevement());

        return budget;
    }


    public static BudgetMensuel getBudgetPrecedentCompteC1(){
        // Budget
        BudgetMensuel bo = new BudgetMensuel();
        bo.setIdCompteBancaire(getCompte().getId());
        bo.setMois(Month.DECEMBER);
        bo.setAnnee(2021);
        bo.setActif(false);
        bo.setIdCompteBancaire(getCompte().getId());
        bo.setId(BudgetDataUtils.getBudgetId(bo.getIdCompteBancaire(), bo.getMois(), bo.getAnnee()));

        bo.getSoldes().setSoldeAtFinMoisCourant(1000D);
        bo.getSoldes().setSoldeAtMaintenant(1000D);
        bo.setDateMiseAJour(LocalDateTime.now());
        bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);

        bo.getListeOperations().add(MockDataOperations.getOperationRealisee(getCompte(), 1));
        bo.getListeOperations().add(MockDataOperations.getOperationRealisee(getCompte(), 2));
        return bo;

    }



    public static BudgetMensuel getBudgetActifCompteC1et3operationsRealisees(){

        BudgetMensuel budget = new BudgetMensuel();
        budget.setMois(Month.JANUARY);
        budget.setAnnee(2022);
        budget.setIdCompteBancaire(getCompte().getId());
        budget.setId(BudgetDataUtils.getBudgetId(budget.getIdCompteBancaire(), budget.getMois(), budget.getAnnee()));

        budget.setActif(true);
        budget.setDateMiseAJour(LocalDateTime.now().minusDays(1));
        // Soldes
        budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        budget.setListeOperations(new ArrayList<>());
        BudgetDataUtils.razCalculs(budget);
        // Opération
        budget.getListeOperations().addAll(MockDataOperations.get3LignesOperations(MockDataBudgets.getCompte()));
        BudgetDataUtils.razCalculs(budget);
        return budget;
    }
}
