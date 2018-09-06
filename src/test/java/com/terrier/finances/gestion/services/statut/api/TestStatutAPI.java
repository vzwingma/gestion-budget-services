package com.terrier.finances.gestion.services.statut.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.services.statut.business.StatusApplicationService;
import com.terrier.finances.gestion.test.config.AbstractTestsAPI;
import com.terrier.finances.gestion.test.config.TestMockDBServicesConfig;
import com.terrier.finances.gestion.test.config.TestRealAuthServices;

/**
 * Test de l'API Statut
 * @author vzwingma
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMockDBServicesConfig.class, TestRealAuthServices.class})
public class TestStatutAPI extends AbstractTestsAPI {

	@Autowired
	private StatusApplicationService statusApplicationService;
	
	@Before
	public void initControleur(){
		statusApplicationService.initApplication();
	}

	@Test
	public void testStatut() throws Exception {
		// Statut OK
		getMockAPI().perform(
				get(BudgetApiUrlEnum.ROOT_BASE + BudgetApiUrlEnum.STATUT_BASE)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andDo(MockMvcResultHandlers.log())
		.andExpect(content().string(containsString("\"nom\":\"APPLICATION\"")));
	}	
}
