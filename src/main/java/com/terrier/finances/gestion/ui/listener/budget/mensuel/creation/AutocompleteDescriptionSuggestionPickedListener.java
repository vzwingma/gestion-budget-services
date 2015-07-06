/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.CreerDepenseController;
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
	
	private CreerDepenseController controleur;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteDescriptionSuggestionPickedListener.class);
	
	/**
	 * Constructeur
	 * @param descriptionField texte field
	 * @param controleur controleur associé au formulaire 
	 */
	public AutocompleteDescriptionSuggestionPickedListener(AutocompleteField<String> descriptionField, CreerDepenseController controleur){
		this.descriptionField = descriptionField;
		this.controleur = controleur;
	}
	
	@Override
	public void onSuggestionPicked(String suggestion) {
		
		LOGGER.debug("[IHM] Autocomplete picked : {}", suggestion);
		descriptionField.setText(suggestion);
		controleur.blurOnAutocompleteField();
	}
}
