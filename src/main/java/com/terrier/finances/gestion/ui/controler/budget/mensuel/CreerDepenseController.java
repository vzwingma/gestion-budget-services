package com.terrier.finances.gestion.ui.controler.budget.mensuel;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.autocomplete.AutocompleteExtension;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.CreerDepenseForm;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.controler.validators.TypeDepenseValidator;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.ActionValiderCreationDepenseClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionCategorieValueChangeListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionSousCategorieValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Validator.InvalidValueException;

/**
 * Controleur de créer des dépenses
 * @author vzwingma
 *
 */
public class CreerDepenseController extends AbstractUIController<CreerDepenseForm> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1546247300666000991L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CreerDepenseController.class);

	private boolean focus = false;

	/**
	 * Contructeur
	 * @param composant composant
	 */
	public CreerDepenseController(CreerDepenseForm composant) {
		super(composant);
	}


	/**
	 * Init du suivi
	 * @param tableSuiviDepense tableau de suivi
	 */
	public void initDynamicComponentsOnPage(){
		// Sélection d'une catégorie
		getComponent().getComboBoxCategorie().setImmediate(true);
		getComponent().getComboBoxCategorie().addValueChangeListener(new SelectionCategorieValueChangeListener(this));
		// Sélection d'une sous catégorie
		getComponent().getComboBoxSsCategorie().addValueChangeListener(new SelectionSousCategorieValueChangeListener(this));
		
		getComponent().getListSelectComptes().setVisible(false);
		getComponent().getLayoutCompte().setVisible(false);
		getComponent().getLabelCompte().setVisible(false);
		// Périodique
		getComponent().getCheckBoxPeriodique().setCaption(null);
		getComponent().getCheckBoxPeriodique().setDescription("Cocher pour une dépense mensuelle");
		
		// Bouton
		getComponent().getButtonValider().addClickListener(new ActionValiderCreationDepenseClickListener());
		getComponent().getButtonValider().setDescription("Valider l'opération et fermer l'écran de saisie");
		getComponent().getButtonValiderContinuer().addClickListener(new ActionValiderCreationDepenseClickListener());
		getComponent().getButtonValiderContinuer().setDescription("Valider l'opération et Créer une nouvelle opération");

		addValidators();
	}


	/**
	 * Ajout de la validation du formulaire
	 */
	private void addValidators(){
		getComponent().getComboBoxCategorie().setRequiredError("La catégorie est obligatoire");
		getComponent().getListSelectComptes().setRequiredError("Le compte de destination est obligatoire");
		getComponent().getComboBoxSsCategorie().setRequiredError("La sous catégorie est obligatoire");
		getComponent().getListSelectEtat().setRequiredError("L'état de la dépense est obligatoire");
		getComponent().getListSelectType().setRequiredError("Le type de dépense est obligatoire");
		// getComponent().getTextFieldDescription().setRequiredError("La description est obligatoire");

	//	getComponent().getTextFieldValeur().addValidator(new ValeurDepenseValidator("La valeur est incorrecte"));
	}


	/**
	 * Validation du formulaire
	 */
	public boolean validateForm(){
		try{
			getComponent().getComboBoxCategorie().validate();
			getComponent().getComboBoxSsCategorie().validate();
	//		getComponent().getTextFieldValeur().validate();
			if(getComponent().getListSelectComptes().isVisible()){
				getComponent().getListSelectComptes().validate();
			}
		}
		catch(InvalidValueException e){
			LOGGER.info("La dépense est incorrecte", e);
			Notification.show("La dépense est incorrecte : " + e.getMessage(), Notification.Type.WARNING_MESSAGE);
			return false;
		}

		// Vérification de la cohérence des données :
		TypeDepenseValidator typeValidator = new TypeDepenseValidator((CategorieDepense)getComponent().getComboBoxSsCategorie().getConvertedValue());
		try{
			typeValidator.validate((TypeDepenseEnum)getComponent().getListSelectType().getConvertedValue());
		}
		catch(InvalidValueException e){
			Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			return false;
		}
		return true;
	}


	/**
	 * Complétion des éléments du formulaire
	 * @see com.terrier.finances.gestion.ui.controler.AbstractUIController#miseAJourVueDonnees()
	 */

	@Override
	public void miseAJourVueDonnees() {

		/**
		 *  Refresh
		 */
		// Catégories
		Collection<CategorieDepense> categories;
		try {
			categories = getServiceParams().getCategories();
		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}
		getComponent().getComboBoxCategorie().removeAllItems();
		for (CategorieDepense categorieDepense : categories) {
			getComponent().getComboBoxCategorie().addItem(categorieDepense);	
		}
		// SS catégorie
		getComponent().getComboBoxSsCategorie().removeAllItems();
		getComponent().getComboBoxSsCategorie().setEnabled(false);

		/**
		 *  Query
		 */
		 // Apply extension and set suggestion generator
	    AutocompleteExtension<String> suggestDescription = new AutocompleteExtension<String>( getComponent().getTextFieldDescription());
	    suggestDescription.setSuggestionListSize(10);
	    suggestDescription.setSuggestionGenerator(getServiceDepense()::suggestDescription);

	    // Notify when suggestion is selected
	    suggestDescription.addSuggestionSelectListener(event -> {
	    	System.err.println(event.getSelectedItem());
	        event.getSelectedItem().ifPresent(Notification::show);
	    });
		

		
		// Comptes pour virement intercomptes
		getComponent().getListSelectComptes().setNullSelectionAllowed(true);
		getComponent().getListSelectComptes().removeAllItems();
		try{
			for (CompteBancaire compte : getServiceParams().getComptesUtilisateur(getUtilisateurCourant())) {
				// # 58 Pas le compte courant
				if(!compte.getId().equals(getBudgetMensuelCourant().getCompteBancaire().getId())
						&& 
						// #57 Compte actif
						compte.isActif()){
					getComponent().getListSelectComptes().addItem(compte.getId());
					getComponent().getListSelectComptes().setItemCaption(compte.getId(), compte.getLibelle());
				}
			}
		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}

		// Valeur
		getComponent().getTextFieldValeur().setValue("0");
		// Type dépense
		getComponent().getListSelectType().setNullSelectionAllowed(false);
		getComponent().getListSelectType().removeAllItems();
		for (TypeDepenseEnum type : TypeDepenseEnum.values()) {
			getComponent().getListSelectType().addItem(type);
			getComponent().getListSelectType().setItemCaption(type, type.getLibelle());
			if(type.equals(TypeDepenseEnum.DEPENSE)){
				getComponent().getListSelectType().select(type);
			}
		}
				
		// Etat
		getComponent().getListSelectEtat().setNullSelectionAllowed(false);
		getComponent().getListSelectEtat().removeAllItems();
		for(EtatLigneDepenseEnum etat : EtatLigneDepenseEnum.values()){
			getComponent().getListSelectEtat().addItem(etat);
			getComponent().getListSelectEtat().setItemCaption(etat, etat.getLibelle());
			// #50 : Gestion du style par préférence utilisateur
			String etatNlleDepense = getUtilisateurCourant().getPreference(UtilisateurPrefsEnum.PREFS_STATUT_NLLE_DEPENSE, String.class);
			if(etatNlleDepense != null){
				getComponent().getListSelectEtat().select(EtatLigneDepenseEnum.getEnum(etatNlleDepense));
			}
		}

		// Périodique	
		getComponent().getCheckBoxPeriodique().setValue(Boolean.FALSE);
	}
	


	/**
	 * Lorsque Focus sur l'autocomplete : Désactivation de la validation du formulaire par Entrée
	 */
	public void focusOnAutocompleteField(){
		if(!focus){
			LOGGER.debug("[IHM] Focus on AutoComplete");
			getComponent().getButtonValider().setClickShortcut(KeyCode.INSERT, ModifierKey.CTRL);
			focus = true;
		}
	}
	
	/**
	 * Lorsque Blur sur l'autocomplete : Réactivation de la validation du formulaire par Entrée
	 */
	public void blurOnAutocompleteField(){
		if(focus){
			LOGGER.debug("[IHM] Blur from AutoComplete");
			getComponent().getButtonValider().setClickShortcut(KeyCode.ENTER);
			focus = false;
		}
	}
}
