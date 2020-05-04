package com.terrier.finances.gestion.services.budgets.model.v12;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Budget du mois
 * @author vzwingma
 *
 */
@Document(collection = "budgets")
@Getter @Setter @NoArgsConstructor
public class BudgetMensuelDTO implements Serializable {

	@Id
	public String id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4393433203514049021L;

	/**
	 * Mois du budget (au sens CALENDAR)
	 */
	private Month mois;
	/**
	 * année du budget
	 */
	private int annee;
	/**
	 * Budget actif
	 */
	private boolean actif = false;
	/**
	 * Date de mise à jour
	 */
	private LocalDateTime dateMiseAJour;
	/**
	 * Compte bancaire
	 */
	private String idCompteBancaire;

	/**
	 * Liste des dépenses
	 */
	private List<LigneDepenseDTO> listeDepenses = new ArrayList<>();
	/**
	 * Résultats Totaux
	 */
	private Totaux totaux;
	
	private Map<String, Totaux> totauxParCategories = new HashMap<>();
	private Map<String, Totaux> totauxParSSCategories = new HashMap<>();

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
	 * @param id the id to set
	 */
	public void setId() {
		this.id = BudgetDataUtils.getBudgetId(this.idCompteBancaire, this.mois, this.annee);
	}

	@Getter @Setter @NoArgsConstructor
	public class Totaux {
		/**
		 * Totaux
		 */
		private Double finMoisPrecedent;
		private Double maintenant;
		private Double finMoisCourant;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BudgetMensuelDTO [mois=" + mois + ", annee=" + annee
				+ ", bugetActif=" + actif + ", dateMiseAJour="
				+ (dateMiseAJour != null ? BudgetDateTimeUtils.getLibelleDate(dateMiseAJour) : "null") + ", compte=" + idCompteBancaire + "]";
	}
}
