/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteDescriptionSuggestionPickedListener.class);
	
	/**
	 * Constructeur
	 * @param descriptionField texte field
	 */
	public AutocompleteDescriptionSuggestionPickedListener(AutocompleteField<String> descriptionField){
		this.descriptionField = descriptionField;
	}
	
	@Override
	public void onSuggestionPicked(String suggestion) {
		
		LOGGER.debug("[IHM] Autocomplete picked : {}", suggestion);
		descriptionField.setValue(suggestion);
	}
}
