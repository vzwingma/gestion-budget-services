/**
 * 
 */
package com.terrier.finances.gestion.model.business.budget;

/**
 * @author vzwingma
 *
 */
public class TotalBudgetMensuel {

	
	private String typeTotal;
	
	private double totalADate;
	
	private double totalFinMois;

	
	
	
	
	/**
	 * @param typeTotal
	 * @param totalADate
	 * @param totalFinMois
	 */
	public TotalBudgetMensuel(String typeTotal, double totalADate, double totalFinMois) {
		super();
		this.typeTotal = typeTotal;
		this.totalADate = totalADate;
		this.totalFinMois = totalFinMois;
	}

	/**
	 * @return the typeTotal
	 */
	public String getTypeTotal() {
		return typeTotal;
	}

	/**
	 * @param typeTotal the typeTotal to set
	 */
	public void setTypeTotal(String typeTotal) {
		this.typeTotal = typeTotal;
	}

	/**
	 * @return the totalADate
	 */
	public double getTotalADate() {
		return totalADate;
	}

	/**
	 * @param totalADate the totalADate to set
	 */
	public void setTotalADate(double totalADate) {
		this.totalADate = totalADate;
	}

	/**
	 * @return the totalFinMois
	 */
	public double getTotalFinMois() {
		return totalFinMois;
	}

	/**
	 * @param totalFinMois the totalFinMois to set
	 */
	public void setTotalFinMois(double totalFinMois) {
		this.totalFinMois = totalFinMois;
	}
	
	
	
}
