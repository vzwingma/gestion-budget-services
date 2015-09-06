package com.terrier.finances.gestion.model.data.budget;

import java.io.Serializable;


/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
public class IdLigneDepenseXO implements Serializable {




	/**
	 * 
	 */
	private static final long serialVersionUID = -5472608922664117493L;

	
	private String id;
	
	
	
	/**
	 * Id Dépense
	 * @param id retourne l'id de dépense
	 */
	public IdLigneDepenseXO(String id) {
		super();
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepenseXO [getId()=").append(getId())
				.append("]");
		return builder.toString();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	
	
}
