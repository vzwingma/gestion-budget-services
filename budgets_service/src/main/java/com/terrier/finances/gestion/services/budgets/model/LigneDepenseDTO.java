package com.terrier.finances.gestion.services.budgets.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
@Getter @Setter @NoArgsConstructor
public class LigneDepenseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2932267709864103657L;
	// Id
	private String id;
	// SS Catégorie
	private String idSSCategorie;
	// Libellé
	private String libelle;
	// Notes
	private String notes;
	// Type de dépense
	private String typeDepense;
	// Etat de la ligne
	private String etat;
	// Valeur
	private String valeur;
	// Date operation
	private Date dateOperation;
	// Date mise à jour
	private Date dateMaj;
	// Auteur MAJ
	private String auteur;
	// Périodique
	private boolean periodique; 
	// tag comme dernière opération
	private boolean derniereOperation;



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepenseDTO [id=").append(id)
				.append(", idSSCategorie=").append(idSSCategorie)
				.append(", libelle=").append(libelle).append(", notes=")
				.append(notes).append(", typeDepense=").append(typeDepense)
				.append(", etat=").append(etat).append(", valeur=")
				.append(valeur).append(", dateOperation=")
				.append(dateOperation).append(", dateMaj=").append(dateMaj)
				.append(", auteur=").append(auteur).append(", periodique=")
				.append(periodique).append(", derniereOperation=")
				.append(derniereOperation).append("]");
		return builder.toString();
	}
}
