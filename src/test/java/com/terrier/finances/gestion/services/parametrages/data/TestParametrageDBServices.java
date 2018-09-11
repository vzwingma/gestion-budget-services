package com.terrier.finances.gestion.services.parametrages.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.model.CategorieOperationDTO;

public class TestParametrageDBServices {

	
	@Test
	public void testCategories(){
		
		
		ParametragesDatabaseService db = spy(new ParametragesDatabaseService());
		
		List<CategorieOperationDTO> categoriesFromDB = new ArrayList<>();
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
		categoriesFromDB.add(catAlimentation);
		
		
		MongoOperations mockMongo = mock(MongoOperations.class);
		when(mockMongo.findAll(CategorieOperationDTO.class)).thenReturn(categoriesFromDB);
		
		when(db.getMongoOperation()).thenReturn(mockMongo);
		
		List<CategorieOperation> cats = db.chargeCategories();
		assertNotNull(cats);
		assertEquals(1, cats.size());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", cats.iterator().next().getId());
		assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", cats.iterator().next().getListeIdsSSCategories().iterator().next());
	}
	

	@Test
	public void testGetCategorie(){
		
		
		ParametragesDatabaseService db = spy(new ParametragesDatabaseService());
		
		List<CategorieOperationDTO> categoriesFromDB = new ArrayList<>();
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
		categoriesFromDB.add(catAlimentation);
		
		
		MongoOperations mockMongo = mock(MongoOperations.class);
		when(mockMongo.findAll(CategorieOperationDTO.class)).thenReturn(categoriesFromDB);
		when(db.getMongoOperation()).thenReturn(mockMongo);
		
		CategorieOperation cat = db.chargeCategorieParId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
		assertNotNull(cat);
		CategorieOperation ssCat = db.chargeCategorieParId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		assertNotNull(ssCat);
	}
}
