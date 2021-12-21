package com.terrier.finances.gestion.communs.operations.model.api;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * Object représentant les libellés des opérations pour l'ensemble des budgets de l'année pour un compte
 * @author vzwingma
 *
 */
public class LibellesOperationsAPIObject extends AbstractAPIObjectModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1515823001772650589L;
	@Schema(description = "Id du compte")
	private String idCompte;
	@Schema(description = "Libelles des opérations courantes")
	private Set<String> libellesOperations;

	/**
	 * @return the idCompte
	 */
	public String getIdCompte() {
		return idCompte;
	}

	/**
	 * @param idCompte the idCompte to set
	 */
	public void setIdCompte(String idCompte) {
		this.idCompte = idCompte;
	}

	/**
	 * @return the libellesOperations
	 */
	public Set<String> getLibellesOperations() {
		return libellesOperations;
	}

	/**
	 * @param libellesOperations the libellesOperations to set
	 */
	public void setLibellesOperations(Set<String> libellesOperations) {
		this.libellesOperations = libellesOperations;
	}
	
	
}
