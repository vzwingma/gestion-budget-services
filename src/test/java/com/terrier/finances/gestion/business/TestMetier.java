package com.terrier.finances.gestion.business;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.data.DataUtils;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.budget.model.transformer.DataTransformerBudget;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-spring-config.xml")
public class TestMetier {

	@Autowired
	private BudgetDatabaseService service;

	@Autowired
	private TestBudgetConfig config;

	@Autowired
	DataTransformerBudget dataTransformerBudget = new DataTransformerBudget();
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMetier.class);

	@Test
	public void testDate(){
		SimpleDateFormat sdf = new SimpleDateFormat(DataUtils.DATE_FULL_TEXT_PATTERN, Locale.FRENCH);
		LOGGER.info(sdf.format(Calendar.getInstance().getTime()));
	}


	@Ignore
	public void createNewBudget() throws Exception{
		assertNotNull(service);

		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is("ingdirectV"));
		CompteBancaire compte = config.mongoTemplate().findOne(queryBudget, CompteBancaire.class);
		assertNotNull(compte);

		BudgetMensuelDTO budget = new BudgetMensuelDTO();
		budget.setActif(true);
		budget.setAnnee(2016);
		budget.setMois(0);
		budget.setCompteBancaire(compte);
		budget.setId(null);
		config.mongoTemplate().save(budget, "budget_2016");
	}


	@Ignore
	public void getCompte() throws Exception{
		assertNotNull(service);

		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is("ingdirectV"));
		CompteBancaire compte = config.mongoTemplate().findOne(queryBudget, CompteBancaire.class);
		assertNotNull(compte);
		assertNotNull(compte.isActif());
		assertTrue(compte.isActif());
	}

}