package com.terrier.finances.gestion.communs.budget.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;

class TestBudgetMensuel {

	@Test
	void testResultat() {

		BudgetMensuel b = new BudgetMensuel();
		b.setSoldes(b.new Soldes());
		b.getSoldes().setSoldeAtFinMoisPrecedent(100D);
		BudgetDataUtils.razCalculs(b);
		LigneOperation o = new LigneOperation();
		o.setSsCategorie(o.new Categorie());
		o.getSsCategorie().setId(IdsCategoriesEnum.SALAIRE.getId());
		o.setValeur(123D);
		o.setTypeOperation(TypeOperationEnum.CREDIT);
		b.getListeOperations().add(o);

		LigneOperation o2 = new LigneOperation();
		o2.setValeur(123D);
		o2.setTypeOperation(TypeOperationEnum.CREDIT);
		b.getListeOperations().add(o2);

		assertEquals(100D, b.getSoldes().getSoldeAtFinMoisCourant());
		
	}
}
