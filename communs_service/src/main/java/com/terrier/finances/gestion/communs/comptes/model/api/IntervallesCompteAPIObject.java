package com.terrier.finances.gestion.communs.comptes.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Getter @Setter
public class IntervallesCompteAPIObject extends AbstractAPIObjectModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2380780514003066552L;


	/**
	 * @param datePremierBudget : Date du premier budget du compte
	 */
	@NonNull
	private Long datePremierBudget;

	/**
	 * @param dateDernierBudget : Date du dernier budget du compte
	 */
	@NonNull
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
