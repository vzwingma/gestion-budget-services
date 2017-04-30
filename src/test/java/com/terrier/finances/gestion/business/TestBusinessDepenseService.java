package com.terrier.finances.gestion.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class TestBusinessDepenseService {

	
	@Test
	public void testSuggestion(){
		BusinessDepensesService businessDepense = new BusinessDepensesService();
		businessDepense.setLibellesDepensesForAutocomplete.add("aaaa");
		businessDepense.setLibellesDepensesForAutocomplete.add("bbbb");
		businessDepense.setLibellesDepensesForAutocomplete.add("cccc");
		
		List<String> resultat = businessDepense.suggestDescription("aa", 3);
		assertNotNull(resultat);
		assertEquals(1, resultat.size());
		
		resultat = businessDepense.suggestDescription("ab", 3);
		assertEquals(0, resultat.size());
		
		resultat = businessDepense.suggestDescription("AA", 3);
		assertEquals(1, resultat.size());
		

		resultat = businessDepense.suggestDescription("Aa", 3);
		assertEquals(1, resultat.size());
	}
}
