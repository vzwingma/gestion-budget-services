package com.terrier.finances.gestion.communs.budget.model.v12;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Totaux par catégories
 * @author vzwingma
 *
 */
@Getter @NoArgsConstructor
@Schema(description = "Totaux par catégorie")
public class TotauxCategorie implements Serializable {

	private static final long serialVersionUID = 1726925483789601358L;
	@Setter
	@Schema(description = "Libellé de la catégorie")
	private String libelleCategorie;
	@Schema(description = "Total à date")
	private Double totalAtMaintenant = 0D;
	@Schema(description = "Total à la fin du mois")
	private Double totalAtFinMoisCourant = 0D;
	
	
	public Double ajouterATotalAtMaintenant(Double montantAAjouter) {
		this.totalAtMaintenant += montantAAjouter;
		return this.totalAtMaintenant;
	}

	public Double ajouterATotalAtFinMoisCourant(Double montantAAjouter) {
		this.totalAtFinMoisCourant += montantAAjouter;
		return this.totalAtFinMoisCourant;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Totaux Categorie [libelle=").append(libelleCategorie).append(", total à Maintenant=")
				.append(totalAtMaintenant).append(", total A FinMoisCourant=").append(totalAtFinMoisCourant).append("]");
		return builder.toString();
	}
	
	
}
