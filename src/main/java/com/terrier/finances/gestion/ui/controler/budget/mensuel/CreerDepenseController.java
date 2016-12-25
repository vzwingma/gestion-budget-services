package com.terrier.finances.gestion.ui.controler.budget.mensuel;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.CreerDepenseForm;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.controler.validators.TypeDepenseValidator;
import com.terrier.finances.gestion.ui.controler.validators.ValeurDepenseValidator;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.ActionValiderCreationDepenseClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.AutocompleteDescriptionQueryListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.AutocompleteDescriptionSuggestionPickedListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.BlurDescriptionValueChangeListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionCategorieValueChangeListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionSousCategorieValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Notification;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;

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
		getComponent().getComboBoxCategorie().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		
		// Sélection d'une sous catégorie
		getComponent().getComboBoxSsCategorie().addValueChangeListener(new SelectionSousCategorieValueChangeListener(this));
		getComponent().getComboBoxSsCategorie().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		
		getComponent().getListSelectComptes().setVisible(false);
		getComponent().getListSelectComptes().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		getComponent().getLayoutCompte().setVisible(false);
		getComponent().getLabelCompte().setVisible(false);
		getComponent().getLabelCompte().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		// Périodique
		getComponent().getCheckBoxPeriodique().setCaption(null);
		getComponent().getCheckBoxPeriodique().setDescription("Cocher pour une dépense mensuelle");
		getComponent().getCheckBoxPeriodique().addValueChangeListener(new BlurDescriptionValueChangeListener(this));

		// Description
		getComponent().getTextFieldDescription().setTrimQuery(true);
		getComponent().getTextFieldDescription().setRequired(true);
		getComponent().getTextFieldDescription().setBuffered(false);
		getComponent().getTextFieldDescription().setInvalidAllowed(true);
		getComponent().getTextFieldDescription().setInvalidCommitted(true);
		getComponent().getTextFieldDescription().setDelay(0);
		
		// Valeur
		getComponent().getListSelectType().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		getComponent().getTextFieldValeur().addValueChangeListener(new BlurDescriptionValueChangeListener(this));
		
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
		getComponent().getTextFieldDescription().setRequiredError("La description est obligatoire");

		getComponent().getTextFieldValeur().addValidator(new ValeurDepenseValidator("La valeur est incorrecte"));
	}


	/**
	 * Validation du formulaire
	 */
	public boolean validateForm(){
		try{
			getComponent().getComboBoxCategorie().validate();
			getComponent().getComboBoxSsCategorie().validate();
			getComponent().getTextFieldValeur().validate();
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
		AutocompleteField<String> descriptionField = getComponent().getTextFieldDescription();
		descriptionField.setSuggestionPickedListener(new AutocompleteDescriptionSuggestionPickedListener(descriptionField, this));

		AutocompleteQueryListener<String> listener = new AutocompleteDescriptionQueryListener(
				getBudgetMensuelCourant().getSetLibellesDepensesForAutocomplete(), this) ;
		getComponent().getTextFieldDescription().setQueryListener(listener);
		// Description
		getComponent().getTextFieldDescription().setText("");
		getComponent().getTextFieldDescription().setData("");
		getComponent().getTextFieldDescription().clearChoices();
		getComponent().getTextFieldDescription().clear();

		
		
		// Compte
		getComponent().getListSelectComptes().setNullSelectionAllowed(true);
		getComponent().getListSelectComptes().removeAllItems();
		try{
			for (CompteBancaire compte : getServiceParams().getComptesUtilisateur(getUtilisateurCourant())) {
				getComponent().getListSelectComptes().addItem(compte.getId());
				getComponent().getListSelectComptes().setItemCaption(compte.getId(), compte.getLibelle());
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
