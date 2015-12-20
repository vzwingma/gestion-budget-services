package com.terrier.finances.gestion.model.data.budget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Budget du mois XML Object
 * @author vzwingma
 *
 */
public class BudgetMensuelXO extends BudgetMensuelDTO {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1741081461882417894L;

	
	
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO#getListeDepenses()
	 * Cette méthode est interdite via le service REST : retourne toujours null
	 * Appeler le service rest/v2/budget/{idBudget}/depenses pour obtenir la liste des dépenses
	 */
	@Override
	@JsonIgnore
	@Deprecated
	public List<LigneDepenseDTO> getListeDepenses() {
		return null;
	}




	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO#setListeDepenses(java.util.List)
	 * Cette méthode est interdite via le service REST : ne modifie rien
	 * Appeler le service rest/v2/budget/{idBudget}/depense/{idDepense} pour mettre à jour la liste des dépenses
	 */
	@Override
	@JsonIgnore
	@Deprecated
	public void setListeDepenses(List<LigneDepenseDTO> listeDepenses) {
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BudgetMensuelXO [Mois=").append(getMois())
				.append(", Annee=").append(getAnnee())
				.append(", DateMiseAJour=").append(getDateMiseAJour() != null ? getDateMiseAJour() : "")
				.append(", CompteBancaire=").append(getCompteBancaire())
				.append(", actif=").append(isActif()).append("]");
		return builder.toString();
	}
	
	
}
