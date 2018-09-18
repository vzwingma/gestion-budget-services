/**
 * 
 */
package com.terrier.finances.gestion.services.budget.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Month;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.budget.model.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.services.budget.model.transformer.DataTransformerLigneOperation;

/**
 * @author PVZN02821
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDataTransformerBudget {

	@Spy
	private DataTransformerBudget transformer;
	
	@Spy
	private DataTransformerLigneOperation dataTransformerLigneDepense;
	
	private static CategorieOperation cat1;
	private static CategorieOperation cat2;
	private static CategorieOperation ssCat11;
	private static CategorieOperation ssCat12;
	private static CategorieOperation ssCat21;
	private static CategorieOperation ssCat22;	
	
	static {
		
		cat1 = new CategorieOperation();
		
		ssCat11 = new CategorieOperation();
		ssCat11.setActif(true);
		ssCat11.setCategorie(false);
		ssCat11.setId("SS CAT11");
		ssCat11.setLibelle("SS CATEGORIE 11");
		ssCat11.setCategorieParente(cat1);
		
		ssCat12 = new CategorieOperation();
		ssCat12.setActif(true);
		ssCat12.setCategorie(false);
		ssCat12.setId("SS CAT12");
		ssCat12.setLibelle("SS CATEGORIE 12");
		ssCat12.setCategorieParente(cat1);
		
		
		cat1.setActif(true);
		cat1.setCategorie(true);
		cat1.setId("CAT1");
		cat1.setLibelle("CATEGORIE 1");
		cat1.getListeSSCategories().add(ssCat11);
		cat1.getListeSSCategories().add(ssCat12);
		
		
		ssCat21 = new CategorieOperation();
		ssCat21.setActif(true);
		ssCat21.setCategorie(false);
		ssCat21.setId("SS CAT21");
		ssCat21.setLibelle("SS CATEGORIE 21");
		
		ssCat22 = new CategorieOperation();
		ssCat22.setActif(true);
		ssCat22.setCategorie(false);
		ssCat22.setId("SS CAT22");
		ssCat22.setLibelle("SS CATEGORIE 22");
		
		cat2 = new CategorieOperation();
		cat2.setActif(true);
		cat2.setCategorie(true);
		cat2.setId("CAT2");
		cat2.setLibelle("CATEGORIE 2");
		cat2.getListeSSCategories().add(ssCat21);
		cat2.getListeSSCategories().add(ssCat22);
	}
	
	private BasicTextEncryptor e = new BasicTextEncryptor();
	
	@Before
	public void initTransformer() throws DataNotFoundException{
		e.setPassword("test");
		transformer.setDataTransformerLigneDepense(dataTransformerLigneDepense);
	}
	
	
	@Test
	public void testTransformBOtoDTOtoBO(){
		// Ini
		BudgetMensuel bo = new BudgetMensuel();
		bo.setCompteBancaire(new CompteBancaire());
		bo.getCompteBancaire().setId("Compte_Test");
		bo.setMois(Month.AUGUST);
		bo.setAnnee(2018);
		bo.setId("TEST");
		bo.setActif(false);
		
		bo.setSoldeFin(123D);
		bo.setSoldeNow(12D);
		
		bo.getTotalParSSCategories().put(ssCat11.getId(), new Double[]{11D, 311D});
		bo.getTotalParSSCategories().put(ssCat12.getId(), new Double[]{12D, 312D});
		bo.getTotalParSSCategories().put(ssCat21.getId(), new Double[]{21D, 321D});
		bo.getTotalParSSCategories().put(ssCat22.getId(), new Double[]{22D, 322D});
		
		bo.getTotalParCategories().put(cat1.getId(), new Double[]{1111D, 11311D});
		bo.getTotalParCategories().put(cat2.getId(), new Double[]{1112D, 11312D});
		
		
		/**
		 * Transformation en DTO
		 */
		BudgetMensuelDTO dto = transformer.transformBOtoDTO(bo, e);
		assertNotNull(dto);
		
		assertNotEquals(bo.getSoldeFin(), dto.getFinArgentAvance());
		assertNotEquals(bo.getSoldeNow(), dto.getNowArgentAvance());
		
	
		/**
		 * Transformation en BO
		 */
		BudgetMensuel bo2 = transformer.transformDTOtoBO(dto, e);
		assertEquals(bo.getSoldeFin(), bo2.getSoldeFin(), 1);
		assertEquals(bo.getSoldeNow(), bo2.getSoldeNow(), 1);
		
		assertEquals(bo.getTotalParCategories().get(cat1)[0], bo2.getTotalParCategories().get(cat1)[0], 1);
		assertEquals(bo.getTotalParCategories().get(cat1)[1], bo2.getTotalParCategories().get(cat1)[1], 1);
		
		assertEquals(bo.getTotalParSSCategories().get(ssCat11)[0], bo2.getTotalParSSCategories().get(ssCat11)[0], 1);
		assertEquals(bo.getTotalParSSCategories().get(ssCat11)[1], bo2.getTotalParSSCategories().get(ssCat11)[1], 1);
	}
}
