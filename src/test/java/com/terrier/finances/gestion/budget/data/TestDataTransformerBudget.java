/**
 * 
 */
package com.terrier.finances.gestion.budget.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Month;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.terrier.finances.gestion.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.budget.model.transformer.DataTransformerBudget;
import com.terrier.finances.gestion.budget.model.transformer.DataTransformerLigneDepense;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.parametrages.data.ParametragesDatabaseService;

/**
 * @author PVZN02821
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDataTransformerBudget {

	
	@Mock
	private ParametragesDatabaseService parametrageMockService;
	
	@Spy
	private DataTransformerBudget transformer;
	
	@Spy
	private DataTransformerLigneDepense dataTransformerLigneDepense;
	
	private static CategorieDepense cat1;
	private static CategorieDepense cat2;
	private static CategorieDepense ssCat11;
	private static CategorieDepense ssCat12;
	private static CategorieDepense ssCat21;
	private static CategorieDepense ssCat22;	
	
	static {
		ssCat11 = new CategorieDepense();
		ssCat11.setActif(true);
		ssCat11.setCategorie(false);
		ssCat11.setId("SS CAT11");
		ssCat11.setLibelle("SS CATEGORIE 11");
		ssCat11.setIdCategorieParente("CAT1");
		
		ssCat12 = new CategorieDepense();
		ssCat12.setActif(true);
		ssCat12.setCategorie(false);
		ssCat12.setId("SS CAT12");
		ssCat12.setLibelle("SS CATEGORIE 12");
		ssCat12.setIdCategorieParente("CAT1");
		
		cat1 = new CategorieDepense();
		cat1.setActif(true);
		cat1.setCategorie(true);
		cat1.setId("CAT1");
		cat1.setLibelle("CATEGORIE 1");
		cat1.getListeIdsSSCategories().add("SS CAT11");
		cat1.getListeIdsSSCategories().add("SS CAT12");
		cat1.getListeSSCategories().add(ssCat11);
		cat1.getListeSSCategories().add(ssCat12);
		
		
		ssCat21 = new CategorieDepense();
		ssCat21.setActif(true);
		ssCat21.setCategorie(false);
		ssCat21.setId("SS CAT21");
		ssCat21.setLibelle("SS CATEGORIE 21");
		
		ssCat22 = new CategorieDepense();
		ssCat22.setActif(true);
		ssCat22.setCategorie(false);
		ssCat22.setId("SS CAT22");
		ssCat22.setLibelle("SS CATEGORIE 22");
		
		cat2 = new CategorieDepense();
		cat2.setActif(true);
		cat2.setCategorie(true);
		cat2.setId("CAT2");
		cat2.setLibelle("CATEGORIE 2");
		cat2.getListeIdsSSCategories().add("SS CAT21");
		cat2.getListeIdsSSCategories().add("SS CAT22");
		cat2.getListeSSCategories().add(ssCat21);
		cat2.getListeSSCategories().add(ssCat22);
		
		
	}
	
	private BasicTextEncryptor e = new BasicTextEncryptor();
	
	@Before
	public void initTransformer() throws DataNotFoundException{
		e.setPassword("test");
		transformer.setDataTransformerLigneDepense(dataTransformerLigneDepense);
		transformer.setParametrageService(parametrageMockService);
		when(parametrageMockService.chargeCategorieParId(anyString())).thenAnswer(new Answer<CategorieDepense>() {

			/* (non-Javadoc)
			 * @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
			 */
			@Override
			public CategorieDepense answer(InvocationOnMock invocation) throws Throwable {
				String param = (String)invocation.getArguments()[0];
				switch (param) {
				case "CAT1":
					return cat1;
				case "CAT2":
					return cat2;
				case "SS CAT11":
					return ssCat11;
				case "SS CAT12":
					return ssCat12;
				case "SS CAT21":
					return ssCat21;
				case "SS CAT22":
					return ssCat22;
				default:
					break;
				}
				return null;
			}
		});
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
		
		bo.setFinArgentAvance(123D);
		bo.setFinCompteReel(121D);
		bo.setNowArgentAvance(12D);
		bo.setNowCompteReel(11D);
		
		bo.getTotalParSSCategories().put(ssCat11, new Double[]{11D, 311D});
		bo.getTotalParSSCategories().put(ssCat12, new Double[]{12D, 312D});
		bo.getTotalParSSCategories().put(ssCat21, new Double[]{21D, 321D});
		bo.getTotalParSSCategories().put(ssCat22, new Double[]{22D, 322D});
		
		bo.getTotalParCategories().put(cat1, new Double[]{1111D, 11311D});
		bo.getTotalParCategories().put(cat2, new Double[]{1112D, 11312D});
		
		
		/**
		 * Transformation en DTO
		 */
		BudgetMensuelDTO dto = transformer.transformBOtoDTO(bo, e);
		assertNotNull(dto);
		
		assertNotEquals(bo.getFinArgentAvance(), dto.getFinArgentAvance());
		assertNotEquals(bo.getFinCompteReel(), dto.getFinCompteReel());
		assertNotEquals(bo.getNowArgentAvance(), dto.getNowArgentAvance());
		assertNotEquals(bo.getNowCompteReel(), dto.getNowCompteReel());
		
	
		/**
		 * Transformation en BO
		 */
		BudgetMensuel bo2 = transformer.transformDTOtoBO(dto, e);
		assertEquals(bo.getFinArgentAvance(), bo2.getFinArgentAvance(), 1);
		assertEquals(bo.getFinCompteReel(), bo2.getFinCompteReel(), 1);
		assertEquals(bo.getNowArgentAvance(), bo2.getNowArgentAvance(), 1);
		assertEquals(bo.getNowCompteReel(), bo2.getNowCompteReel(), 1);
		
		assertEquals(bo.getTotalParCategories().get(cat1)[0], bo2.getTotalParCategories().get(cat1)[0], 1);
		assertEquals(bo.getTotalParCategories().get(cat1)[1], bo2.getTotalParCategories().get(cat1)[1], 1);
		
		assertEquals(bo.getTotalParSSCategories().get(ssCat11)[0], bo2.getTotalParSSCategories().get(ssCat11)[0], 1);
		assertEquals(bo.getTotalParSSCategories().get(ssCat11)[1], bo2.getTotalParSSCategories().get(ssCat11)[1], 1);
	}
}
