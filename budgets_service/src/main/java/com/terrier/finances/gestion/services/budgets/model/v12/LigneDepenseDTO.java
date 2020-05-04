package com.terrier.finances.gestion.services.budgets.model.v12;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;

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

	// Libellé
	private String libelle;

	// Catégorie
	private Categorie categorie;
	private Categorie ssCategorie;
	
	// Type de dépense
	private TypeOperationEnum typeOperation;
	// Etat de la ligne
	private EtatOperationEnum etat;
	// Valeur
	private Double valeur;

	// Périodique
	private boolean periodique; 
	// tag comme dernière opération
	private boolean tagDerniereOperation;
	// Autres infos
	private AddInfos autresInfos;

	@Getter @Setter @NoArgsConstructor
	public class Categorie {

		@Id
		private String id;
		/**
		 * Libelle
		 */
		private String libelle;
	}
	
	
	@Getter @Setter @NoArgsConstructor
	public class AddInfos{
		// Date operation
		private LocalDateTime dateOperation;
		// Date mise à jour
		private LocalDateTime dateMaj;
		// Auteur MAJ
		private String auteur;
		// Notes
		private String notes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LigneDepenseDTO [id=").append(id)
				.append(", categorie=").append(categorie)
				.append(", SSCategorie=").append(ssCategorie)
				.append(", libelle=").append(libelle).append(", typeDepense=").append(typeOperation)
				.append(", etat=").append(etat).append(", valeur=")
				.append(valeur).append(", periodique=")
				.append(periodique).append(", derniereOperation=")
				.append(tagDerniereOperation).append("]");
		return builder.toString();
	}
}
