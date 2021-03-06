package com.terrier.finances.gestion.services.parametrages.test.api;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.parametrages.business.port.IParametrageRequest;
import com.terrier.finances.gestion.services.parametrages.test.data.TestDataCategorieOperation;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Tests des API paramétrages
 * @author vzwingma
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes={MockServiceParametrage.class})
class TestParametragesAPI extends AbstractTestsAPI  {


	@Autowired
	private IParametrageRequest mockServiceParametrage;

	@Test
	void testGetCategories() throws Exception {

		// Authentification
		authenticateUser("userTest");
		
		when(mockServiceParametrage.getCategories()).thenReturn(new ArrayList<>());
		
		getMockAPI().perform(get(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));

		when(mockServiceParametrage.getCategories()).thenReturn(TestDataCategorieOperation.getListeTestCategories());
		
		String expectedResult = "[{\"id\":\"8f1614c9-503c-4e7d-8cb5-0c9a9218b84a\",\"libelle\":\"Alimentation\",\"actif\":true,\"listeSSCategories\":[{\"id\":\"467496e4-9059-4b9b-8773-21f230c8c5c6\",\"libelle\":\"Courses\",\"actif\":true,\"listeSSCategories\":null,\"categorie\":false}],\"categorie\":true}]";
		getMockAPI().perform( get(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL))
					.andExpect(status().isOk())
					.andExpect(content().string(expectedResult));

	}
}
