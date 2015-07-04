/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;

/**
 * Autocompletion des libelles
 * @author vzwingma
 *
 */
public class AutocompleteDescriptionQueryListener implements AutocompleteQueryListener<String> {

	
	private BudgetMensuel budgetCourant;
	
	public AutocompleteDescriptionQueryListener(BudgetMensuel budget){
		this.budgetCourant = budget;
	}

	
	@Override
	public void handleUserQuery(AutocompleteField<String> autocompleteField,
			String query) {
		// Création de la liste correspondante à l'autocomplete

		for (String libelleDepense : budgetCourant.getSetLibellesDepensesForAutocomplete()) {
			if(libelleDepense != null && libelleDepense.toLowerCase().contains(query.toLowerCase())){
				autocompleteField.addSuggestion(libelleDepense, libelleDepense);	
			}
		}
	}
	
	
	

}
