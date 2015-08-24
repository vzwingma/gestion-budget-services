package com.terrier.finances.gestion.model.data.budget;

import java.util.Date;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
public class LigneDepenseXO extends LigneDepenseDTO {



	/**
	 * 
	 */
	private static final long serialVersionUID = -5352353628269452269L;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepenseXO [getId()=").append(getId())
				.append(", getLibelle()=").append(getLibelle())
				.append(", getTypeDepense()=").append(getTypeDepense())
				.append(", getValeur()=").append(getValeur())
				.append(", getDateOperation()=").append(getDateOperation())
				.append(", getAuteur()=").append(getAuteur())
				.append(", getEtat()=").append(getEtat())
				.append(", isPeriodique()=").append(isPeriodique())
				.append(", getNotes()=").append(getNotes())
				.append(", getIdSSCategorie()=").append(getIdSSCategorie())
				.append(", getIdCategorie()=").append(getIdCategorie())
				.append(", isDerniereOperation()=")
				.append(isDerniereOperation()).append("]");
		return builder.toString();
	}

	
	
}
