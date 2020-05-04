package com.terrier.finances.gestion.services.communs.api.converters;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Month;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utilisateur.model.api.AuthLoginAPIObject;

/**
 * Test de converters
 * @author vzwingma
 *
 */
public class TestConverters {

	@Test
	public void testConverterAPIObject() throws HttpMessageNotWritableException, IOException{
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		
		assertFalse(converter.canRead(List.class, MediaType.APPLICATION_JSON));
		assertFalse(converter.canWrite(List.class, MediaType.APPLICATION_JSON));

		AuthLoginAPIObject auth = new AuthLoginAPIObject("Test", "Test");
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(auth, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"login\":\"Test\",\"motDePasse\":\"Test\"}", out.getBody().toString());
		
		assertTrue(converter.canRead(CompteBancaire.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(CompteBancaire.class, MediaType.APPLICATION_JSON));
	}
	
	

	
	
	@Test
	public void testConvertBudget() throws Exception {

		// Budget
		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);

		BudgetMensuel bo = new BudgetMensuel();
		bo.setCompteBancaire(c1);
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(false);
		bo.setId("BUDGETTEST");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		Calendar c = Calendar.getInstance();
		bo.setDateMiseAJour(c);
		bo.setResultatMoisPrecedent(0D);
		
		CategorieOperation cat = new CategorieOperation();
		cat.setCategorie(true);
		cat.setId("IdTest");
		cat.setLibelle("LibelleTest");
		bo.getTotalParCategories().put(cat.getId(), new Double[]{ 100D, 200D});
		
		CategorieOperation ssCat = new CategorieOperation();
		ssCat.setCategorie(false);
		ssCat.setId("IdTest");
		ssCat.setLibelle("LibelleTest");
		bo.getTotalParSSCategories().put(ssCat.getId(), new Double[]{ 100D, 200D});

		
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		assertTrue(converter.canWrite(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canRead(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(bo, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"id\":\"BUDGETTEST\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":false,\"dateMiseAJour\":"+c.getTimeInMillis()+",\"compteBancaire\":{\"id\":\"C1\",\"libelle\":\"Libelle1\",\"itemIcon\":null,\"ordre\":1,\"actif\":true},\"moisPrecedentResultat\":0.0,\"listeOperations\":[],\"totalParCategories\":{\"IdTest\":[100.0,200.0]},\"totalParSSCategories\":{\"IdTest\":[100.0,200.0]},\"soldeNow\":0.0,\"soldeFin\":0.0,\"newBudget\":false}", out.getBody().toString());
		
		HttpInputMessage in = new MockHttpInputMessage(out.getBody().toString().getBytes());
		AbstractAPIObjectModel modelRead = converter.read(BudgetMensuel.class, in);
		assertTrue(modelRead instanceof BudgetMensuel);
		
		BudgetMensuel boRead = (BudgetMensuel)modelRead;
		assertEquals(bo.getId(), boRead.getId());
		assertEquals(bo.getSoldeFin(), boRead.getSoldeFin(), 1);
		assertEquals(bo.getSoldeNow(), boRead.getSoldeNow(), 1);
		
		assertEquals(1, boRead.getTotalParCategories().size());
		assertEquals("IdTest", boRead.getTotalParCategories().keySet().iterator().next());
		assertEquals(1, boRead.getTotalParSSCategories().size());
		assertEquals("IdTest", boRead.getTotalParSSCategories().keySet().iterator().next());
	}
	
	
	@Test
	public void testConvertMap() throws IOException{
		// Budget
		BudgetMensuel bo = new BudgetMensuel();
		bo.setId("BUDGETTEST");
		bo.setSoldeFin(0D);
		bo.setSoldeNow(1000D);
		
		CategorieOperation cat = new CategorieOperation();
		cat.setCategorie(true);
		cat.setId("IdTest");
		cat.setLibelle("LibelleTest");
		bo.getTotalParCategories().put(cat.getId(), new Double[]{ 100D, 200D});
		
		CategorieOperation ssCat = new CategorieOperation();
		ssCat.setCategorie(false);
		ssCat.setId("IdTest");
		ssCat.setLibelle("LibelleTest");
		bo.getTotalParSSCategories().put(ssCat.getId(), new Double[]{ 100D, 200D});
		
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		assertTrue(converter.canRead(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(bo, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"id\":\"BUDGETTEST\",\"mois\":null,\"annee\":0,\"actif\":false,\"dateMiseAJour\":null,\"compteBancaire\":null,\"moisPrecedentResultat\":null,\"listeOperations\":[],\"totalParCategories\":{\"IdTest\":[100.0,200.0]},\"totalParSSCategories\":{\"IdTest\":[100.0,200.0]},\"soldeNow\":1000.0,\"soldeFin\":0.0,\"newBudget\":false}", out.getBody().toString());
		
		HttpInputMessage in = new MockHttpInputMessage(out.getBody().toString().getBytes());
		AbstractAPIObjectModel modelRead = converter.read(BudgetMensuel.class, in);
		assertTrue(modelRead instanceof BudgetMensuel);
		
		BudgetMensuel boRead = (BudgetMensuel)modelRead;
		assertEquals(1, boRead.getTotalParCategories().size());
		assertEquals("IdTest", boRead.getTotalParCategories().keySet().iterator().next());
		assertEquals(1, boRead.getTotalParSSCategories().size());
		assertEquals("IdTest", boRead.getTotalParSSCategories().keySet().iterator().next());
	}
	
}

