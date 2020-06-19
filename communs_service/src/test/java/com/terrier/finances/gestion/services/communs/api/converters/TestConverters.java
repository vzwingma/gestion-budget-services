package com.terrier.finances.gestion.services.communs.api.converters;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.budget.model.v12.TotauxCategorie;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;

/**
 * Test de converters
 * @author vzwingma
 *
 */
class TestConverters {

	@Test
	void testConverterAPIObject() throws HttpMessageNotWritableException, IOException{
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		
		assertFalse(converter.canRead(List.class, MediaType.APPLICATION_JSON));
		assertFalse(converter.canWrite(List.class, MediaType.APPLICATION_JSON));

		UtilisateurPrefsAPIObject auth = new UtilisateurPrefsAPIObject();
		auth.setIdUtilisateur("Test");
		auth.setLastAccessTime(0L);
		auth.setPreferences(new HashMap<>());
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(auth, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"idUtilisateur\":\"Test\",\"lastAccessTime\":0,\"preferences\":{}}", out.getBody().toString());
		
		assertTrue(converter.canRead(CompteBancaire.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(CompteBancaire.class, MediaType.APPLICATION_JSON));
	}
	
	

	
	
	@Test
	void testConvertBudget() throws Exception {

		// Budget
		CompteBancaire c1 = new CompteBancaire();
		c1.setActif(true);
		c1.setId("C1");
		c1.setLibelle("Libelle1");
		c1.setOrdre(1);

		BudgetMensuel bo = new BudgetMensuel();
		bo.setIdCompteBancaire(c1.getId());
		bo.setMois(Month.JANUARY);
		bo.setAnnee(2018);
		bo.setActif(false);
		bo.setId("BUDGETTEST");
		bo.getSoldes().setSoldeAtFinMoisCourant(0D);
		bo.getSoldes().setSoldeAtMaintenant(1000D);
		LocalDateTime t = LocalDateTime.of(2020, 10, 1, 12, 0, 0);
		bo.setDateMiseAJour(t);
		bo.getSoldes().setSoldeAtFinMoisPrecedent(0D);
		
		CategorieOperation cat = new CategorieOperation();
		cat.setCategorie(true);
		cat.setId("IdTest");
		cat.setLibelle("LibelleTest");
		bo.getTotauxParCategories().put(cat.getId(), new TotauxCategorie());
		bo.getTotauxParCategories().get(cat.getId()).setLibelleCategorie(cat.getLibelle());
		bo.getTotauxParCategories().get(cat.getId()).ajouterATotalAtMaintenant(100D);
		bo.getTotauxParCategories().get(cat.getId()).ajouterATotalAtFinMoisCourant(200D);
		
		CategorieOperation ssCat = new CategorieOperation();
		ssCat.setCategorie(false);
		ssCat.setId("IdTest");
		ssCat.setLibelle("LibelleTest");
		bo.getTotauxParSSCategories().put(ssCat.getId(), new TotauxCategorie());
		bo.getTotauxParSSCategories().get(ssCat.getId()).setLibelleCategorie(ssCat.getLibelle());
		bo.getTotauxParSSCategories().get(ssCat.getId()).ajouterATotalAtMaintenant(100D);
		bo.getTotauxParSSCategories().get(ssCat.getId()).ajouterATotalAtFinMoisCourant(200D);

		
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		assertTrue(converter.canWrite(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canRead(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(bo, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"id\":\"BUDGETTEST\",\"mois\":\"JANUARY\",\"annee\":2018,\"actif\":false,\"newBudget\":false,\"dateMiseAJour\":[2020,10,1,12,0],\"idCompteBancaire\":\"C1\",\"listeOperations\":[],\"soldes\":{\"soldeAtFinMoisPrecedent\":0.0,\"soldeAtMaintenant\":1000.0,\"soldeAtFinMoisCourant\":0.0},\"totauxParCategories\":{\"IdTest\":{\"libelleCategorie\":\"LibelleTest\",\"totalAtMaintenant\":100.0,\"totalAtFinMoisCourant\":200.0}},\"totauxParSSCategories\":{\"IdTest\":{\"libelleCategorie\":\"LibelleTest\",\"totalAtMaintenant\":100.0,\"totalAtFinMoisCourant\":200.0}}}", out.getBody().toString());
		
		HttpInputMessage in = new MockHttpInputMessage(out.getBody().toString().getBytes());
		AbstractAPIObjectModel modelRead = converter.read(BudgetMensuel.class, in);
		assertTrue(modelRead instanceof BudgetMensuel);
		
		BudgetMensuel boRead = (BudgetMensuel)modelRead;
		assertEquals(bo.getId(), boRead.getId());
		assertEquals(bo.getSoldes().getSoldeAtFinMoisCourant(), boRead.getSoldes().getSoldeAtFinMoisCourant(), 1);
		assertEquals(bo.getSoldes().getSoldeAtMaintenant(), boRead.getSoldes().getSoldeAtMaintenant(), 1);
		
		assertEquals(1, boRead.getTotauxParCategories().size());
		assertEquals("IdTest", boRead.getTotauxParCategories().keySet().iterator().next());
		assertEquals(1, boRead.getTotauxParSSCategories().size());
		assertEquals("IdTest", boRead.getTotauxParSSCategories().keySet().iterator().next());
	}
	
	
	@Test
	void testConvertMap() throws IOException{
		// Budget
		BudgetMensuel bo = new BudgetMensuel();
		bo.setId("BUDGETTEST");
		bo.getSoldes().setSoldeAtFinMoisCourant(0D);
		bo.getSoldes().setSoldeAtMaintenant(1000D);
		
		CategorieOperation cat = new CategorieOperation();
		cat.setCategorie(true);
		cat.setId("IdTest");
		cat.setLibelle("LibelleTest");
		bo.getTotauxParCategories().put(cat.getId(), new TotauxCategorie());
		bo.getTotauxParCategories().get(cat.getId()).setLibelleCategorie(cat.getLibelle());
		bo.getTotauxParCategories().get(cat.getId()).ajouterATotalAtMaintenant(100D);
		bo.getTotauxParCategories().get(cat.getId()).ajouterATotalAtFinMoisCourant(200D);

		
		CategorieOperation ssCat = new CategorieOperation();
		ssCat.setCategorie(false);
		ssCat.setId("IdTest");
		ssCat.setLibelle("LibelleTest");
		bo.getTotauxParSSCategories().put(ssCat.getId(), new TotauxCategorie());
		bo.getTotauxParSSCategories().get(ssCat.getId()).setLibelleCategorie(ssCat.getLibelle());
		bo.getTotauxParSSCategories().get(ssCat.getId()).ajouterATotalAtMaintenant(100D);
		bo.getTotauxParSSCategories().get(ssCat.getId()).ajouterATotalAtFinMoisCourant(200D);
		
		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		assertTrue(converter.canRead(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canWrite(BudgetMensuel.class, MediaType.APPLICATION_JSON));
		
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(bo, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"id\":\"BUDGETTEST\",\"mois\":null,\"annee\":0,\"actif\":false,\"newBudget\":false,\"dateMiseAJour\":null,\"idCompteBancaire\":null,\"listeOperations\":[],\"soldes\":{\"soldeAtFinMoisPrecedent\":0.0,\"soldeAtMaintenant\":1000.0,\"soldeAtFinMoisCourant\":0.0},\"totauxParCategories\":{\"IdTest\":{\"libelleCategorie\":\"LibelleTest\",\"totalAtMaintenant\":100.0,\"totalAtFinMoisCourant\":200.0}},\"totauxParSSCategories\":{\"IdTest\":{\"libelleCategorie\":\"LibelleTest\",\"totalAtMaintenant\":100.0,\"totalAtFinMoisCourant\":200.0}}}", out.getBody().toString());
		
		HttpInputMessage in = new MockHttpInputMessage(out.getBody().toString().getBytes());
		AbstractAPIObjectModel modelRead = converter.read(BudgetMensuel.class, in);
		assertTrue(modelRead instanceof BudgetMensuel);
		
		BudgetMensuel boRead = (BudgetMensuel)modelRead;
		assertEquals(1, boRead.getTotauxParCategories().size());
		assertEquals("IdTest", boRead.getTotauxParCategories().keySet().iterator().next());
		assertEquals(1, boRead.getTotauxParSSCategories().size());
		assertEquals("IdTest", boRead.getTotauxParSSCategories().keySet().iterator().next());
	}
	
	
	
	/**
	 * Parse Ligne
	 * @throws IOException
	 */
	@Test
	void testConvertLigneOperation() throws IOException {

		LigneOperation operation = new LigneOperation();
		operation.setId("OP1");
		operation.setEtat(EtatOperationEnum.REALISEE);
		operation.setLibelle("Operation 1");
		operation.setPeriodique(false);
		operation.setTagDerniereOperation(false);
		operation.setCategorie(operation.new Categorie());
		operation.getCategorie().setId("CAT1");
		operation.getCategorie().setLibelle("CAT1");
		operation.setSsCategorie(operation.new Categorie());
		operation.getSsCategorie().setId("SsCAT1");
		operation.getSsCategorie().setLibelle("SsCAT1");
		operation.setValeur(123D);
		operation.setTypeOperation(TypeOperationEnum.CREDIT);
		operation.setAutresInfos(operation.new AddInfos());
		operation.getAutresInfos().setAuteur("MOI");
		LocalDateTime t = LocalDateTime.of(2020, 10, 1, 12, 0, 0);
		operation.getAutresInfos().setDateOperation(t);
		operation.getAutresInfos().setDateMaj(t);

		APIObjectMessageConverter<AbstractAPIObjectModel> converter = new APIObjectMessageConverter<>();
		assertTrue(converter.canWrite(LigneOperation.class, MediaType.APPLICATION_JSON));
		assertTrue(converter.canRead(LigneOperation.class, MediaType.APPLICATION_JSON));
		
		HttpOutputMessage out = new MockHttpOutputMessage();
		converter.write(operation, MediaType.APPLICATION_JSON, out);
		assertEquals("{\"id\":\"OP1\",\"libelle\":\"Operation 1\",\"categorie\":{\"id\":\"CAT1\",\"libelle\":\"CAT1\"},\"ssCategorie\":{\"id\":\"SsCAT1\",\"libelle\":\"SsCAT1\"},\"typeOperation\":\"CREDIT\",\"etat\":\"REALISEE\",\"valeur\":123.0,\"periodique\":false,\"tagDerniereOperation\":false,\"autresInfos\":{\"dateCreate\":null,\"dateOperation\":[2020,10,1,12,0],\"dateMaj\":[2020,10,1,12,0],\"auteur\":\"MOI\"}}", out.getBody().toString());

		HttpInputMessage in = new MockHttpInputMessage(out.getBody().toString().getBytes());
		AbstractAPIObjectModel modelRead = converter.read(LigneOperation.class, in);
		assertTrue(modelRead instanceof LigneOperation);
		
		LigneOperation operationRead = (LigneOperation)modelRead;
		assertEquals("OP1", operationRead.getId());
		assertEquals("MOI", operationRead.getAutresInfos().getAuteur());
	}
	
}

