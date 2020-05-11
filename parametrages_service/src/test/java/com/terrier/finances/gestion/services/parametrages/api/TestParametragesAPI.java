package com.terrier.finances.gestion.services.parametrages.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.parametrages.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBParamConfig;


/**
 * Tests des API paramétrages
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBParamConfig.class})
public class TestParametragesAPI extends AbstractTestsAPI  {


	@Autowired
	private ParametragesDatabaseService mockDataDBParams;


	@Test
	public void testGetCategories() throws Exception {
		
		/** Authentification **/
		authenticateUser("userTest");
		
		when(mockDataDBParams.chargeCategories()).thenReturn(new ArrayList<>());
		
		getMockAPI().perform(get(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));


		List<CategorieOperation> categoriesFromDB = new ArrayList<>();
		CategorieOperation catAlimentation = new CategorieOperation();
		catAlimentation.setId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
		catAlimentation.setActif(true);
		catAlimentation.setCategorie(true);
		catAlimentation.setLibelle("Alimentation");


		CategorieOperation ssCatCourse = new CategorieOperation();
		ssCatCourse.setActif(true);
		ssCatCourse.setCategorie(false);
		ssCatCourse.setId("467496e4-9059-4b9b-8773-21f230c8c5c6");
		ssCatCourse.setLibelle("Courses");
		ssCatCourse.setListeSSCategories(null);
		catAlimentation.setListeSSCategories(new HashSet<>());
		catAlimentation.getListeSSCategories().add(ssCatCourse);
		categoriesFromDB.add(catAlimentation);

		when(mockDataDBParams.chargeCategories()).thenReturn(categoriesFromDB);
		
		String expectedResult = "[{\"id\":\"8f1614c9-503c-4e7d-8cb5-0c9a9218b84a\",\"libelle\":\"Alimentation\",\"actif\":true,\"listeSSCategories\":[{\"id\":\"467496e4-9059-4b9b-8773-21f230c8c5c6\",\"libelle\":\"Courses\",\"actif\":true,\"listeSSCategories\":null,\"categorie\":false}],\"categorie\":true}]";
		getMockAPI().perform( get(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL))
					.andExpect(status().isOk())
					.andExpect(content().string(expectedResult));

	}
}
