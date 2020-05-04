package com.terrier.finances.gestion.services.parametrages.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.model.v12.CategorieOperationDTO;

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
		
		db.setMongoOperations(mockMongo);
		
		List<CategorieOperation> cats = db.chargeCategories();
		assertNotNull(cats);
		assertEquals(1, cats.size());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", cats.iterator().next().getId());
		assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", cats.iterator().next().getListeSSCategories().iterator().next().getId());
		
		cats.stream().forEach(c -> c.getListeSSCategories().clear());
		
		cats = db.chargeCategories();
		assertNotNull(cats);
		assertEquals(1, cats.size());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", cats.iterator().next().getId());
		assertEquals("467496e4-9059-4b9b-8773-21f230c8c5c6", cats.iterator().next().getListeSSCategories().iterator().next().getId());
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
		db.setMongoOperations(mockMongo);
		
		CategorieOperation cat = db.getCategorieParId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
		assertNotNull(cat);
		
		CategorieOperation ssCat = db.getCategorieParId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		assertNotNull(ssCat);
		assertNotNull(ssCat.getCategorieParente());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", ssCat.getCategorieParente().getId());
		
		
		cat.getListeSSCategories().clear();
		
		CategorieOperation ssCat2 = db.getCategorieParId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		assertNotNull(ssCat2);
		assertNotNull(ssCat2.getCategorieParente());
		assertEquals("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a", ssCat2.getCategorieParente().getId());
	}
	
	
	

	@Test
	public void testGetCategorieById(){
		
		
		ParametragesDatabaseService db = spy(new ParametragesDatabaseService());
		
		List<CategorieOperationDTO> categoriesFromDB = new ArrayList<>();
		
		for (int i = 0; i < 9; i++) {
			
			CategorieOperationDTO cat = new CategorieOperationDTO();
			cat.setId("ID" + i);
			cat.setActif(true);
			cat.setCategorie(true);
			cat.setLibelle("CAT" + i);
			
			for (int j = 0; j < 9; j++) {
				CategorieOperationDTO ssCat = new CategorieOperationDTO();
				ssCat.setActif(true);
				ssCat.setCategorie(false);
				ssCat.setId("ID" + i + j);
				ssCat.setLibelle("SSCAT" + j);
				ssCat.setListeSSCategories(null);
				cat.getListeSSCategories().add(ssCat);
				
			}
			categoriesFromDB.add(cat);
		}


		MongoOperations mockMongo = mock(MongoOperations.class);
		when(mockMongo.findAll(CategorieOperationDTO.class)).thenReturn(categoriesFromDB);
		db.setMongoOperations(mockMongo);
		
		CategorieOperation cat = db.getCategorieParId("ID8");
		assertNotNull(cat);
		
		CategorieOperation ssCat = db.getCategorieParId("ID88");
		assertNotNull(ssCat);
		assertNotNull(ssCat.getCategorieParente());
		assertEquals("ID8", ssCat.getCategorieParente().getId());
		
		
		cat.getListeSSCategories().clear();
		
		CategorieOperation ssCat2 = db.getCategorieParId("ID73");
		assertNotNull(ssCat2);
		assertNotNull(ssCat2.getCategorieParente());
		assertEquals("ID7", ssCat2.getCategorieParente().getId());
	}
}
