package com.terrier.finances.gestion.services.budgets.model;

import java.io.Serializable;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;

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
@Deprecated
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
	private int mois;
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
	private Date dateMiseAJour;
	/**
	 * Compte bancaire
	 */
	private CompteBancaire compteBancaire;
	/**
	 * Résultat du mois précédent
	 */
	private String resultatMoisPrecedent;


	/**
	 * Liste des dépenses
	 */
	private List<LigneDepenseDTO> listeDepenses = new ArrayList<>();

	/**
	 * Totaux
	 */
	private String nowArgentAvance;
	private String finArgentAvance;

	private Map<String, String[]> totalParCategories = new HashMap<>();
	private Map<String, String[]> totalParSSCategories = new HashMap<>();
	
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
		this.id = BudgetDataUtils.getBudgetId(this.compteBancaire.getId(), Month.of(this.mois+1), this.annee);
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BudgetMensuelDTO [mois=" + mois + ", annee=" + annee
				+ ", bugetActif=" + actif + ", dateMiseAJour="
				+ (dateMiseAJour != null ? dateMiseAJour.getTime() : "null") + ", compte=" + compteBancaire + "]";
	}
}
