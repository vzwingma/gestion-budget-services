/**
 * 
 */
package com.terrier.finances.gestion.services.parametrages.model.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.model.CategorieOperationDTO;

/**
 * @author vzwingma
 *
 */
public class TestTransformerCategorieOperations {

	
	@Test
	public void testTransform(){
		DataTransformerCategorieOperations transformer = new DataTransformerCategorieOperations();
		
		CategorieOperationDTO catAlimentation = new CategorieOperationDTO();
		catAlimentation.setId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
		catAlimentation.setActif(true);
		catAlimentation.setCategorie(true);
		catAlimentation.setLibelle("Alimentation");


		CategorieOperationDTO ssCatCourse = new CategorieOperationDTO();
		ssCatCourse.setActif(true);
		ssCatCourse.setCategorie(false);
		ssCatCourse.setId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		ssCatCourse.setLibelle("Courses");
		ssCatCourse.setListeSSCategories(null);
		catAlimentation.getListeSSCategories().add(ssCatCourse);
		
		
		CategorieOperation bo = transformer.transformDTOtoBO(catAlimentation);
		assertNotNull(bo);
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", bo.getId());
		assertNull(bo.getCategorieParente());
		assertTrue(bo.isCategorie());
		assertEquals(1, bo.getListeSSCategories().size());
		assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", bo.getListeSSCategories().iterator().next().getId());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", bo.getListeSSCategories().iterator().next().getCategorieParente().getId());
		
		
		CategorieOperationDTO dto = transformer.transformBOtoDTO(bo);
		assertNotNull(dto);
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", dto.getId());
		assertNotNull(dto.getLibelle());
		assertEquals(1, dto.getListeSSCategories().size());
		assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", dto.getListeSSCategories().iterator().next().getId());
		assertNull(dto.getListeSSCategories().iterator().next().getListeSSCategories());
	}
}
