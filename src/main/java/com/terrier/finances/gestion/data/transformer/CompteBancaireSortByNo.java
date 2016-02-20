package com.terrier.finances.gestion.data.transformer;

import java.util.Comparator;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;

/**
 * Comparaison de comptes bancaires
 * @author vzwingma
 *
 */
public class CompteBancaireSortByNo implements Comparator<CompteBancaire>{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CompteBancaire compte1, CompteBancaire compte2) {
		// TODO Auto-generated method stub
		return Integer.compare(compte1.getOrdre(), compte2.getOrdre());
	}
	
	

}
