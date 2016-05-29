package com.terrier.finances.gestion.model.business.budget;

import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.junit.Test;

import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;

public class TestLigneDepense {

	
	@Test
	public void testUpdateProperty(){
		LigneDepense old = new LigneDepense();
		old.setAuteur("A1");
		old.setDateMaj(Calendar.getInstance().getTime());
		old.setDateOperation(Calendar.getInstance().getTime());
		old.setDerniereOperation(false);
		old.setEtat(EtatLigneDepenseEnum.PREVUE);
		old.setId("123");
		old.setLibelle("Dépense A1");
		old.setNotes("notes");
		old.setPeriodique(Boolean.FALSE);
		old.setTypeDepense(TypeDepenseEnum.DEPENSE);
		old.setValeur(123);
		
		assertFalse(old.updateProperty("123", "Notes", String.class, "notes"));
	}
}
