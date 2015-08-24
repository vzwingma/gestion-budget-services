package com.terrier.finances.gestion.model.data.budget;


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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BudgetMensuelXO [getMois()=").append(getMois())
				.append(", getAnnee()=").append(getAnnee())
				.append(", getDateMiseAJour()=").append(getDateMiseAJour() != null ? getDateMiseAJour() : "")
				.append(", getCompteBancaire()=").append(getCompteBancaire())
				.append(", isActif()=").append(isActif()).append("]");
		return builder.toString();
	}
	
	
}
