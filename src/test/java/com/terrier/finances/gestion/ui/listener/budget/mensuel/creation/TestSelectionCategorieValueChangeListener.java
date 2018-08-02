package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;

public class TestSelectionCategorieValueChangeListener {

	
	@Test
	public void addSsCategoriesActives(){
		
		List<CategorieDepense> ssCategories = new ArrayList<>();
		
		CategorieDepense catActive = new CategorieDepense();
		catActive.setActif(true);
		catActive.setId("ACTIVE");
		ssCategories.add(catActive);
		
		CategorieDepense catInactive = new CategorieDepense();
		catInactive.setActif(false);
		catInactive.setId("INACTIVE");
		ssCategories.add(catInactive);
		
		assertEquals(1, ssCategories.stream().filter(cat -> cat.isActif()).count());
		
	}
}
