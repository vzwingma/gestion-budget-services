package com.terrier.finances.gestion.model.data.budget;


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
		builder.append("LigneDepenseXO [Id=").append(getId())
				.append(", Libelle=").append(getLibelle())
				.append(", TypeDepense=").append(getTypeDepense())
				.append(", Valeur=").append(getValeur())
				.append(", DateOperation=").append(getDateOperation())
				.append(", Auteur=").append(getAuteur())
				.append(", Etat=").append(getEtat())
				.append(", isPeriodique=").append(isPeriodique())
				.append(", Notes=").append(getNotes())
				.append(", IdSSCategorie=").append(getIdSSCategorie())
				.append(", IdCategorie=").append(getIdCategorie())
				.append(", isDerniereOperation=")
				.append(isDerniereOperation()).append("]");
		return builder.toString();
	}

	
	
}
