package io.github.vzwingma.finances.budget.services.operations.test.data;

import io.github.vzwingma.finances.budget.services.communs.data.enums.IdsCategoriesEnum;
import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.TypeOperationEnum;
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
        CompteBancaire c1 = new CompteBancaire();
        c1.setActif(false);
        c1.setId("C2");
        c1.setLibelle("Libelle2");
        c1.setProprietaire(new CompteBancaire.Proprietaire());
        c1.getProprietaire().setLogin("test");
        c1.setOrdre(2);
        return c1;
    }

    public static BudgetMensuel getBudgetCompteC1(){
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

        bo.getListeOperations().addAll(MockDataOperations.get3LignesOperations(getCompte()));
        return bo;
    }

    public static BudgetMensuel getBudgetCompteC3OperationPrevue(){

        BudgetMensuel budget = new BudgetMensuel();
        budget.setActif(true);
        budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
        budget.setListeOperations(new ArrayList<>());
        BudgetDataUtils.razCalculs(budget);
        CategorieOperations dep = new CategorieOperations(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
        CategorieOperations cat = new CategorieOperations(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS);
        dep.setCategorieParente(cat);

        LigneOperation test1 = new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, 123D, EtatOperationEnum.PREVUE, false);
        test1.setId("TEST1");
        budget.getListeOperations().add(test1);

        budget.setMois(Month.JANUARY);
        budget.setAnnee(2022);
        budget.setIdCompteBancaire(getBudgetCompteC1().getIdCompteBancaire());
        budget.setId(BudgetDataUtils.getBudgetId(getBudgetCompteC1().getIdCompteBancaire(), budget.getMois(), budget.getAnnee()));
        return budget;
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

        bo.getListeOperations().add(MockDataOperations.getOperationRealisee(getCompte(), 1));
        bo.getListeOperations().add(MockDataOperations.getOperationRealisee(getCompte(), 2));
        return bo;

    }
}
