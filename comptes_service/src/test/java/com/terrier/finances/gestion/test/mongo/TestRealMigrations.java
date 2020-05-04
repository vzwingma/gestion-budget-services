/**
 * 
 */
package com.terrier.finances.gestion.test.mongo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;

/**
 * @author vzwingma
 *
 */
@Configuration
@ExtendWith({SpringExtension.class })
@ContextConfiguration(classes={TestRealMigrations.class})
public class TestRealMigrations {

	
	String REC_db_v1 = "budget-app-dev";
	String REC_db_v12 = "v12-app-dev";
	String REC_host = "clusterdev-oqiqv.mongodb.net";
	String REC_username = "budgetdev";
	String REC_password = "23ed8acc18fe33d5fbfa1c73bac352a8";

	String PROD_db_v1 = "budget-app";
	String PROD_db_v12 = "v12-app";
	String PROD_host = "clusterbudget-xm3fg.mongodb.net";
	String PROD_username = "budget";
	String PROD_password = "MY0AmDwSGQxuBgxuMY0AmDwSGQxuBgxu";


	@Bean
	public MongoDbFactory mongoDBv1Factory() {

		//create mongo template
		String mongoURI = new StringBuilder("mongodb+srv://")
				.append(REC_username).append(":").append(REC_password)
				.append("@").append(REC_host).append("/").append(REC_db_v1)
				.append("?retryWrites=true&w=majority")
				.toString();
		return new SimpleMongoClientDbFactory(mongoURI);
	}

	@Bean
	public MongoOperations mongoV1Operations() {
		return new MongoTemplate(mongoDBv1Factory());
	}

	@Bean
	public MongoDbFactory mongoDBv12Factory() {

		//create mongo template
		String mongoURI = new StringBuilder("mongodb+srv://")
				.append(REC_username).append(":").append(REC_password)
				.append("@").append(REC_host).append("/").append(REC_db_v12)
				.append("?retryWrites=true&w=majority")
				.toString();
		return new SimpleMongoClientDbFactory(mongoURI);
	}
	
	@Bean
	public MongoOperations mongoV12Operations() {
		return new MongoTemplate(mongoDBv12Factory());
	}
	
	
	@Test
	public void migrationComptes() {
		List<CompteBancaire> comptev1 = mongoV1Operations().findAll(CompteBancaire.class);
		comptev1.stream().forEach(cv1 -> {
			com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire cv12 = new com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire();
			cv12.setId(cv1.getId());
			cv12.setActif(cv1.isActif());
			cv12.setItemIcon(cv1.getItemIcon());
			cv12.setLibelle(cv1.getLibelle());
			cv12.setOrdre(cv1.getOrdre());
			
			cv1.getListeProprietaires().stream().forEach(uv1 -> {
				com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire.Utilisateur ucv12 = cv12.new Utilisateur();
				ucv12.setId(uv1.getId());
				ucv12.setLogin(uv1.getLogin());
				ucv12.setLibelle(uv1.getLogin());
				cv12.setProprietaire(ucv12);
			});
			
			mongoV12Operations().save(cv12);
		});
		
	}
}
