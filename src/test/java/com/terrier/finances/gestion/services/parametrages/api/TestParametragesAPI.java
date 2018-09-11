package com.terrier.finances.gestion.services.parametrages.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;


/**
 * Tests des API paramétrages
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestParametragesAPI extends AbstractTestsAPI  {

	

	@Test
	public void testGetCategories() throws Exception {
		// Fail
		getMockAPI().perform(
				post(BudgetApiUrlEnum.PARAMS_CATEGORIES_FULL))
		.andExpect(status().is4xxClientError());

		
	}
}
