/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;

/**
 * Autocompletion des libelles
 * @author vzwingma
 *
 */
public class AutocompleteDescriptionQueryListener implements AutocompleteQueryListener<String> {

	// Liste des libelles pour l'autocomplete
	private Set<String> setLibellesDepensesForAutocomplete;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteDescriptionQueryListener.class);
	
	
	
	/**
	 * Constructeur
	 * @param budget budget courant contenant la liste 
	 */
	public AutocompleteDescriptionQueryListener(Set<String> setLibellesDepensesForAutocomplete){
		this.setLibellesDepensesForAutocomplete = setLibellesDepensesForAutocomplete;
	}

	
	@Override
	public void handleUserQuery(AutocompleteField<String> autocompleteField,
			String query) {
		
		LOGGER.debug("[IHM] Autocomplete query : {}", query);
		
		// Création de la liste correspondante à l'autocomplete
		for (String libelleDepense : this.setLibellesDepensesForAutocomplete) {
			if(libelleDepense != null && libelleDepense.toLowerCase().contains(query.toLowerCase())){
				autocompleteField.addSuggestion(libelleDepense, libelleDepense);	
			}
		}
		// Injection de la valeur de la query sur la valeur du champs des fois que la saisie ne corresponde pas à un autocomplete
		autocompleteField.setValue(query);
	}
}
