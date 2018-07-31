package com.terrier.finances.gestion.business.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import org.junit.Test;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.data.ValidationResult;

public class TestValidator {


	private OperationValidator validator = new OperationValidator();

	@Test
	public void testValidator(){

		LigneDepense operation = new LigneDepense(true);


		// Ligne nulle
		assertEquals(ValidationResult.error("").isError(), validator.apply(operation, null).isError());

		operation.setCategorie(new CategorieDepense());
		operation.setSsCategorie(new CategorieDepense());
		operation.setEtat(EtatLigneDepenseEnum.PREVUE);
		operation.setLibelle("TEST LIBELLE");
		operation.setTypeDepense(TypeDepenseEnum.DEPENSE);
		operation.setValeur(-123.13f);
		operation.setValeurS("-123.13");
		// Ligne OK
		ValidationResult r = validator.apply(operation, null);
		assertEquals(ValidationResult.ok().isError(), r.isError());
	}


	@Test
	public void testValue(){

		LigneDepense operation = new LigneDepense(true);

		// Ligne nulle
		operation.setCategorie(new CategorieDepense());
		operation.setSsCategorie(new CategorieDepense());
		operation.setEtat(EtatLigneDepenseEnum.PREVUE);
		operation.setLibelle("TEST LIBELLE");
		operation.setTypeDepense(TypeDepenseEnum.DEPENSE);
		operation.setValeurS("NaN");
		// Ligne OK
		ValidationResult r = validator.apply(operation, null);
		assertEquals(ValidationResult.error("").isError(), r.isError());
	}



	

	@Test
	public void testValidatorCredit(){
		
		
		LigneDepense operation = new LigneDepense(true);
		operation.setCategorie(new CategorieDepense());
		operation.setSsCategorie(new CategorieDepense());
		operation.getSsCategorie().setId(BusinessDepensesService.ID_SS_CAT_SALAIRE);
		operation.setEtat(EtatLigneDepenseEnum.PREVUE);
		operation.setLibelle("TEST LIBELLE");		

		operation.setValeur(-123);
		operation.setValeurS("-123");
		operation.setTypeDepense(TypeDepenseEnum.DEPENSE);
		assertEquals(ValidationResult.error("").isError(), validator.apply(operation, null).isError());
	}
	
	
	@Test
	public void testValidatorDebit(){
			
		LigneDepense operation = new LigneDepense(true);
		operation.setCategorie(new CategorieDepense());
		operation.setSsCategorie(new CategorieDepense());
		operation.getSsCategorie().setId("26a4b966-ffff-ffff-8611-a5ba4b518ef5");
		operation.setEtat(EtatLigneDepenseEnum.PREVUE);
		operation.setLibelle("TEST LIBELLE");	
		operation.setValeur(123);
		operation.setValeurS("123");
		operation.setTypeDepense(TypeDepenseEnum.CREDIT);
		assertEquals(ValidationResult.error("").isError(), validator.apply(operation, null).isError());
	}
	
}
