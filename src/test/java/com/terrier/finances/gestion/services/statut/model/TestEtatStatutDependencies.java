/**
 * 
 */
package com.terrier.finances.gestion.services.statut.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.terrier.finances.gestion.communs.admin.model.DependencyName;
import com.terrier.finances.gestion.communs.admin.model.StatutDependencyAPIObject;
import com.terrier.finances.gestion.communs.admin.model.StatutStateEnum;

/**
 * @author vzwingma
 *
 */
public class TestEtatStatutDependencies {

	
	@Test
	public void testDependencies(){
		StatutDependencyAPIObject statutRoot = new StatutDependencyAPIObject(DependencyName.APPLICATION);
		
		assertNotNull(statutRoot);
		assertNotNull(statutRoot.getDate());
		assertNotNull(statutRoot.getTimestamp());
		assertNotNull(statutRoot.getNom());
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getStatusObject());
		
		// Mise à up
		statutRoot.updateStatusModule(DependencyName.APPLICATION, StatutStateEnum.OK);
		assertEquals(StatutStateEnum.OK, statutRoot.getStatusObject());
		
		// Ajout d'une dépendance inconnue
		statutRoot.addDependency(DependencyName.DATABASE, DependencyName.APPLICATION, "APP");
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getDependances().get(0).getStatusObject());
		// La dépendance globale redevient inconnue
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getStatusCompile());
		
		// Update de la BDD
		statutRoot.updateStatusModule(DependencyName.DATABASE, StatutStateEnum.OK);
		// La dépendance globale redevient ok
		assertEquals(StatutStateEnum.OK, statutRoot.getStatusCompile());
		
		// Ajout d'une dépendance à la BDD (n'est pas correct mais c'est pour le test)
		statutRoot.addDependency(DependencyName.REST_SERVICE, DependencyName.DATABASE, "REST");
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getDependances().get(0).getStatusCompile());
		assertEquals(StatutStateEnum.OK, statutRoot.getDependances().get(0).getStatusObject());
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getDependances().get(0).getDependances().get(0).getStatusCompile());
		// La dépendance globale redevient inconnue
		assertEquals(StatutStateEnum.INCONNU, statutRoot.getStatusCompile());
		
		
		// Update du rest
		statutRoot.updateStatusModule(DependencyName.REST_SERVICE, StatutStateEnum.FATAL);
		// La dépendance globale redevient ok
		assertEquals(StatutStateEnum.FATAL, statutRoot.getStatusCompile());
	}
}
