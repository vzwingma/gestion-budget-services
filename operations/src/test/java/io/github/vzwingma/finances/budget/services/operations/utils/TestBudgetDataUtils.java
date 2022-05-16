package io.github.vzwingma.finances.budget.services.operations.utils;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.BudgetNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestBudgetDataUtils {

	@Test
	void testDoubleFromString(){
		assertNull(BudgetDataUtils.getValueFromString(null));
		assertNull(BudgetDataUtils.getValueFromString("123/0"));
		assertEquals(123.3D, BudgetDataUtils.getValueFromString("123.3"));
		assertEquals(123.3D, BudgetDataUtils.getValueFromString("123,3"));
		assertEquals(-123.3D, BudgetDataUtils.getValueFromString("-123,3"));
	}
	

	@Test
	void testMaxDateOperations(){
		
		LocalDateTime c = LocalDateTime.now();
		LigneOperation depense1 = new LigneOperation();
		depense1.setId("Op1");
		depense1.setAutresInfos(new LigneOperation.AddInfos());
		depense1.getAutresInfos().setDateOperation(c);

		LigneOperation depense2 = new LigneOperation();
		depense2.setId("Op2");
		c = c.withDayOfMonth(28);
		depense2.setAutresInfos(new LigneOperation.AddInfos());
		depense2.getAutresInfos().setDateOperation(c);
		
		LigneOperation depense3 = new LigneOperation();
		depense3.setId("Op3");
		LocalDateTime c3 = LocalDateTime.now().withDayOfMonth(12).withMonth(10).withYear(2050);
		depense3.setAutresInfos(new LigneOperation.AddInfos());
		depense3.getAutresInfos().setDateOperation(c3);

		List<LigneOperation> depenses = new ArrayList<>(Arrays.asList(depense1, depense2, depense3));
		LocalDate cd = BudgetDataUtils.getMaxDateListeOperations(depenses);
		
		assertEquals(Month.OCTOBER.getValue(), cd.get(ChronoField.MONTH_OF_YEAR));
	}
	
	
	@Test
	void getBudgetId(){
		CompteBancaire c1 = new CompteBancaire();
		c1.setId("ING");
		assertEquals("ING_2018_01", BudgetDataUtils.getBudgetId(c1.getId(), Month.JANUARY, 2018));
	}
	
	
	@Test
	void getAnneeFromBudgetId() throws BudgetNotFoundException {
		String id1 = "ING_2018_01";
		
		assertEquals(Integer.valueOf(2018), BudgetDataUtils.getAnneeFromBudgetId(id1));
		assertEquals(Month.JANUARY, BudgetDataUtils.getMoisFromBudgetId(id1));
		assertEquals("ING", BudgetDataUtils.getCompteFromBudgetId(id1));
		
		String id2 = "ingdirectV_2018_08";

		assertEquals(Integer.valueOf(2018), BudgetDataUtils.getAnneeFromBudgetId(id2));
		assertEquals(Month.AUGUST, BudgetDataUtils.getMoisFromBudgetId(id2));
		assertEquals("ingdirectV", BudgetDataUtils.getCompteFromBudgetId(id2));
	}
	

	
	@Test
	void testGetCategorie(){
		
		
		List<CategorieOperations> categoriesFromDB = new ArrayList<>();
		CategorieOperations catAlimentation = new CategorieOperations();
		catAlimentation.setId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
		catAlimentation.setActif(true);
		catAlimentation.setCategorie(true);
		catAlimentation.setLibelle("Alimentation");


		CategorieOperations ssCatCourse = new CategorieOperations();
		ssCatCourse.setActif(true);
		ssCatCourse.setCategorie(false);
		ssCatCourse.setId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		ssCatCourse.setLibelle("Courses");
		ssCatCourse.setCategorieParente(catAlimentation);
		catAlimentation.setListeSSCategories(new HashSet<>());
		catAlimentation.getListeSSCategories().add(ssCatCourse);
		categoriesFromDB.add(catAlimentation);


		CategorieOperations cat = BudgetDataUtils.getCategorieById("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", categoriesFromDB);
		assertNotNull(cat);

		CategorieOperations ssCat = BudgetDataUtils.getCategorieById("467496e4-9059-4b9b-8773-21f230c8c5c6", categoriesFromDB);
		assertNotNull(ssCat);
		assertNotNull(ssCat.getCategorieParente());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", ssCat.getCategorieParente().getId());
	}
	
	
	@Test
	void testDeleteTagFromString(){
		assertNull(BudgetDataUtils.deleteTagFromString(null));

		assertEquals("Libellé Opération", BudgetDataUtils.deleteTagFromString("Libellé Opération"));


		assertEquals("Libellé Opération", BudgetDataUtils.deleteTagFromString("[Virement depuis @utre Compte] Libellé Opération"));
	}

	@Test
	void testGetCategorieById(){
		
		List<CategorieOperations> categoriesFromDB = new ArrayList<>();
		
		for (int i = 0; i < 9; i++) {

			CategorieOperations cat = new CategorieOperations();
			cat.setId("ID" + i);
			cat.setActif(true);
			cat.setCategorie(true);
			cat.setLibelle("CAT" + i);
			
			for (int j = 0; j < 9; j++) {
				CategorieOperations ssCat = new CategorieOperations();
				ssCat.setActif(true);
				ssCat.setCategorie(false);
				ssCat.setId("ID" + i + j);
				ssCat.setLibelle("SSCAT" + j);
				ssCat.setCategorieParente(cat);
				cat.setListeSSCategories(new HashSet<>());
				cat.getListeSSCategories().add(ssCat);
				
				
			}
			categoriesFromDB.add(cat);
		}

		CategorieOperations cat = BudgetDataUtils.getCategorieById("ID8", categoriesFromDB);
		assertNotNull(cat);

		CategorieOperations ssCat = BudgetDataUtils.getCategorieById("ID88", categoriesFromDB);
		assertNotNull(ssCat);
		assertNotNull(ssCat.getCategorieParente());
		assertEquals("ID8", ssCat.getCategorieParente().getId());
	}
}
