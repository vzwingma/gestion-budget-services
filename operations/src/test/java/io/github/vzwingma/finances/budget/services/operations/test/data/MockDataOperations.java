package io.github.vzwingma.finances.budget.services.operations.test.data;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.model.IdsCategoriesEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.TypeOperationEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Jeu de données Opérations
 */
public class MockDataOperations {

    public static List<LigneOperation> get3LignesOperations(CompteBancaire compte){

        List<LigneOperation> listeOperations = new ArrayList<>(3);
        listeOperations.add(getOperationRealisee(compte, 1));

        listeOperations.add(getOperationRealisee(compte, 2));

        LigneOperation lo3 = getOperationRealisee(compte, 3);
        lo3.setLibelle("[Virement depuis Autre Compte] Opération 3");
        listeOperations.add(lo3);

        return listeOperations;

    }

    public static LigneOperation getOperationRealisee(CompteBancaire compte, int numero){

        LigneOperation lo = new LigneOperation();
        lo.setId(compte.getId()+"B2_L"+numero);
        lo.setEtat(EtatOperationEnum.REALISEE);
        lo.setLibelle("Opération "+numero);
        return lo;

    }

    public static LigneOperation getOperationIntercompte(){
        CategorieOperations dep = new CategorieOperations(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE.getId());
        CategorieOperations cat = new CategorieOperations(IdsCategoriesEnum.TRANSFERT_INTERCOMPTE.getId());
        dep.setCategorieParente(cat);
        LigneOperation test1 = new LigneOperation(dep, "TestIntercompte", TypeOperationEnum.CREDIT, 123D, EtatOperationEnum.PREVUE, false, 0);
        test1.setId("TestIntercompte");
        return test1;
    }
    public static LigneOperation getOperationPrelevement(){
        CategorieOperations dep = new CategorieOperations(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS.getId());
        CategorieOperations cat = new CategorieOperations(IdsCategoriesEnum.PRELEVEMENTS_MENSUELS.getId());
        dep.setCategorieParente(cat);
        LigneOperation test1 = new LigneOperation(dep, "TEST1", TypeOperationEnum.CREDIT, 123D, EtatOperationEnum.PREVUE, false, 0);
        test1.setId("TEST1");
        return test1;
    }

    public static LigneOperation getOperationRemboursement(){
        CategorieOperations dep = new CategorieOperations(IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId());
        CategorieOperations cat = new CategorieOperations(IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId());
        dep.setCategorieParente(cat);
        LigneOperation remboursement = new LigneOperation(dep, "TestRemboursement", TypeOperationEnum.DEPENSE, 123D, EtatOperationEnum.REALISEE, false, 0);
        remboursement.setId("TestRemboursement");
        return remboursement;
    }
}
