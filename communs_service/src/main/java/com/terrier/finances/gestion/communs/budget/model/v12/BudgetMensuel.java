package com.terrier.finances.gestion.communs.budget.model.v12;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Budget du mois
 * @author vzwingma
 *
 */
@Document(collection = "budgets")
@Getter @Setter @NoArgsConstructor
public class BudgetMensuel extends AbstractAPIObjectModel implements Serializable {

	@Id
	@Schema(description = "Identifiant")
	public String id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4393433203514049021L;

	/**
	 * Mois du budget (au sens CALENDAR)
	 */
	@Schema(description = "Mois du budget")
	private Month mois;
	/**
	 * année du budget
	 */
	@Schema(description = "Année du budget")
	private int annee;
	/**
	 * Budget actif
	 */
	@Schema(description = "Etat d'activité")
	private boolean actif = false;
	
	@Transient
	private transient boolean newBudget = false;
	/**
	 * Date de mise à jour
	 */
	@Schema(description = "Date de mise à jour")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime dateMiseAJour;
	/**
	 * Compte bancaire
	 */
	@Schema(description = "Id du compte bancaire")
	private String idCompteBancaire;

	/**
	 * Liste des opérations
	 */
	@Schema(description = "Liste des opérations")
	private List<LigneOperation> listeOperations = new ArrayList<>();
	/**
	 * Résultats Totaux
	 */
	@Schema(description = "Soldes")
	private Soldes soldes = new Soldes();
	@Schema(description = "Totaux par catégorie")
	private Map<String, TotauxCategorie> totauxParCategories = new HashMap<>();
	@Schema(description = "Totaux par sous catégories")
	private Map<String, TotauxCategorie> totauxParSSCategories = new HashMap<>();

	
	/**
	 * @return the id
	 */
	public String getId() {
		if(id == null){
			setId();
		}
		return id;
	}

	/**
	 * Set id à partir des informations fonctionnelles
	 */
	public void setId() {
		this.id = BudgetDataUtils.getBudgetId(this.idCompteBancaire, this.mois, this.annee);
	}

	/**
	 * Totaux
	 */
	@Getter @Setter @NoArgsConstructor
	public class Soldes implements Serializable {
		private static final long serialVersionUID = 649769139203031253L;
		private Double soldeAtFinMoisPrecedent = 0D;
		private Double soldeAtMaintenant = 0D;
		private Double soldeAtFinMoisCourant = 0D;
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Soldes [soldeAtFinMoisPrecedent=").append(soldeAtFinMoisPrecedent)
					.append(", soldeAtMaintenant=").append(soldeAtMaintenant).append(", soldeAtFinMoisCourant=")
					.append(soldeAtFinMoisCourant).append("]");
			return builder.toString();
		}
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("BudgetMensuel [id=").append(id).append(", mois=").append(mois).append(", annee=").append(annee)
				.append(", actif=").append(actif).append(", dateMiseAJour=").append((dateMiseAJour != null ? BudgetDateTimeUtils.getLibelleDate(dateMiseAJour) : "null"))
				.append(", idCompteBancaire=").append(idCompteBancaire).append(", soldes=").append(soldes).append("]");
		return builder.toString();
	}
}
