/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.io.Serializable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.budget.mensuel.CreerDepenseController;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;

/**
 * Autocompletion des libelles
 * @author vzwingma
 *
 */
public class AutocompleteDescriptionQueryListener implements AutocompleteQueryListener<String>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6274274186572281747L;

	// Liste des libelles pour l'autocomplete
	private Set<String> setLibellesDepensesForAutocomplete;
	
	/**
	 * Controleur associé
	 */
	private CreerDepenseController controleur;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteDescriptionQueryListener.class);
	
	
	
	/**
	 * Constructeur
	 * @param liste des libellées de l'autocomplete
	 * @param controleur controleur associé au formulaire 
	 */
	public AutocompleteDescriptionQueryListener(Set<String> setLibellesDepensesForAutocomplete, CreerDepenseController controleur){
		this.setLibellesDepensesForAutocomplete = setLibellesDepensesForAutocomplete;
		this.controleur = controleur;
	}

	
	@Override
	public void handleUserQuery(AutocompleteField<String> autocompleteField,
			String query) {
		
		LOGGER.debug("[IHM] Autocomplete query : {}", query);
		autocompleteField.setData(query);
		controleur.focusOnAutocompleteField();
		
		// Création de la liste correspondante à l'autocomplete
		for (String libelleDepense : this.setLibellesDepensesForAutocomplete) {
			if(libelleDepense != null && libelleDepense.toLowerCase().contains(query.toLowerCase())){
				autocompleteField.addSuggestion(libelleDepense, libelleDepense);	
			}
		}
	}
}
