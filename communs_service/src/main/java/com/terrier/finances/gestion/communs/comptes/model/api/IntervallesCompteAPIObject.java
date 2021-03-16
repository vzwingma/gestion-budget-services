package com.terrier.finances.gestion.communs.comptes.model.api;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IntervallesCompteAPIObject extends AbstractAPIObjectModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2380780514003066552L;

	
	@ApiModelProperty(notes = "Date du premier budget du compte", required=true)
	private Long datePremierBudget;
	
	@ApiModelProperty(notes = "Date du dernier budget du compte", required=true)
	private Long dateDernierBudget;

	/**
	 * @return the datePremierBudget
	 */
	@JsonIgnore
	public LocalDate getLocalDatePremierBudget() {
		return BudgetDateTimeUtils.getLocalDateFromNbDay(datePremierBudget);
	}
	/**
	 * @return the dateDernierBudget
	 */
	@JsonIgnore
	public LocalDate getLocalDateDernierBudget() {
		return BudgetDateTimeUtils.getLocalDateFromNbDay(dateDernierBudget);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IntervallesCompteAPIObject [datePremierBudget=").append(datePremierBudget)
				.append(", dateDernierBudget=").append(dateDernierBudget).append("]");
		return builder.toString();
	}

	
}
