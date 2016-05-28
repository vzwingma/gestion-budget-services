package com.terrier.finances.gestion.business;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gwt.editor.client.Editor.Ignore;
import com.terrier.finances.gestion.data.DepensesDatabaseService;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-spring-config.xml")
public class TestMetier {

	@Autowired
	private DepensesDatabaseService service;

	@Autowired
	private TestBudgetConfig config;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMetier.class);
	
	@Test
	public void testDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm:ss", Locale.FRENCH);
		LOGGER.info(sdf.format(Calendar.getInstance().getTime()));
	}
	
	@Ignore
	public void test() throws Exception{
		assertNotNull(service);
		
		Query queryBudget = new Query();
		queryBudget.addCriteria(Criteria.where("id").is("ingdirectS"));
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
}
