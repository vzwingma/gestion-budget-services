/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * Action lors de la validation de l'autosuggestion
 * @author vzwingma
 *
 */
public class AutocompleteDescriptionSuggestionPickedListener implements
		AutocompleteSuggestionPickedListener<String> {

	// Textfield
	private AutocompleteField<String> descriptionField;
	
	
	/**
	 * Constructeur
	 * @param descriptionField texte field
	 */
	public AutocompleteDescriptionSuggestionPickedListener(AutocompleteField<String> descriptionField){
		this.descriptionField = descriptionField;
	}
	
	@Override
	public void onSuggestionPicked(String suggestion) {
		descriptionField.setValue(suggestion);
	}
}
