package com.terrier.finances.gestion.ui.controler.budget.mensuel.creer.operation;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.CreerDepenseForm;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.ActionValiderCreationDepenseClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionCategorieValueChangeListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.SelectionSousCategorieValueChangeListener;
import com.terrier.finances.gestion.ui.styles.comptes.ComptesItemCaptionStyle;
import com.vaadin.ui.ComboBox.NewItemHandler;
import com.vaadin.ui.Notification;

/**
 * Controleur de créer des dépenses
 * @author vzwingma
 *
 */
public class CreerDepenseController extends AbstractUIController<CreerDepenseForm> implements NewItemHandler {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1843521169417325067L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CreerDepenseController.class);


	/**
	 * Contructeur
	 * @param composant composant
	 */
	public CreerDepenseController(CreerDepenseForm composant) {
		super(composant);
	}


	/**
	 * Validation du formulaire
	 */
	public boolean validateForm(){
		boolean validation = true;
		validation &= getComponent().getComboBoxCategorie().getSelectedItem().isPresent();
		validation &= getComponent().getComboBoxSsCategorie().getSelectedItem().isPresent();
		validation &= getComponent().getTextFieldValeur().getOptionalValue().isPresent();
		if(getComponent().getComboboxComptes().isVisible()){
			validation &= getComponent().getComboboxComptes().getSelectedItem().isPresent();
		}
		TypeDepenseEnum typeAttendu = TypeDepenseEnum.DEPENSE;
		if(BusinessDepensesService.ID_SS_CAT_SALAIRE.equals(getComponent().getComboBoxSsCategorie().getSelectedItem().get().getId()) || BusinessDepensesService.ID_SS_CAT_REMBOURSEMENT.equals(getComponent().getComboBoxSsCategorie().getSelectedItem().get().getId())){
			typeAttendu = TypeDepenseEnum.CREDIT;
		}
		// Cohérence type
		validation &= typeAttendu.equals(getComponent().getComboboxType().getValue());

		String value = getComponent().getTextFieldValeur().getValue();
		if(value != null){
			try{
				String valeur = ((String)value).replaceAll(",", ".");
				Double d = Double.valueOf(valeur);
				validation &=(!Double.isInfinite(d) && !Double.isNaN(d));
			}
			catch(NumberFormatException e){ }
		}


		if(!validation){
			LOGGER.info("L'opération est incorrecte");
			Notification.show("L'opération est incorrecte", Notification.Type.WARNING_MESSAGE);
		}
		return validation;
	}


	/**
	 * Complétion des éléments du formulaire
	 * @see com.terrier.finances.gestion.ui.controler.AbstractUIController#miseAJourVueDonnees()
	 */

	@Override
	public void miseAJourVueDonnees() {

		// Sélection d'une catégorie
		// Catégories
		try {
			getComponent().getComboBoxCategorie().setItems(getServiceParams().getCategories());
		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}
		getComponent().getComboBoxCategorie().setEmptySelectionAllowed(false);
		getComponent().getComboBoxCategorie().setTextInputAllowed(false);
		getComponent().getComboBoxCategorie().setEnabled(true);
		getComponent().getComboBoxCategorie().addSelectionListener(new SelectionCategorieValueChangeListener(this));

		// Sélection d'une sous catégorie
		getComponent().getComboBoxSsCategorie().setEmptySelectionAllowed(false);
		getComponent().getComboBoxSsCategorie().setTextInputAllowed(false);
		getComponent().getComboBoxSsCategorie().setEnabled(false);
		getComponent().getComboBoxSsCategorie().addSelectionListener(new SelectionSousCategorieValueChangeListener(this));

		// Comptes pour virement intercomptes
		try{
			getComponent().getComboboxComptes().setItems(
					getServiceParams().getComptesUtilisateur(getUtilisateurCourant())
					.stream()
					.filter(c -> c.isActif())
					.filter(c -> !c.getId().equals(getBudgetMensuelCourant().getCompteBancaire().getId()))
					.collect(Collectors.toList()));
		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}
		getComponent().getComboboxComptes().setItemCaptionGenerator(new ComptesItemCaptionStyle());
		getComponent().getComboboxComptes().setTextInputAllowed(false);
		getComponent().getComboboxComptes().setVisible(false);
		getComponent().getLayoutCompte().setVisible(false);
		getComponent().getLabelCompte().setVisible(false);
		// Valeur
		getComponent().getTextFieldValeur().setValue("0");
		// Type dépense
		getComponent().getComboboxType().setItems(TypeDepenseEnum.values());
		getComponent().getComboboxType().setTextInputAllowed(false);
		getComponent().getComboboxType().setSelectedItem(TypeDepenseEnum.DEPENSE);

		// Etat
		getComponent().getListSelectEtat().setItems(EtatLigneDepenseEnum.values());
		getComponent().getListSelectEtat().setTextInputAllowed(false);
		// #50 : Gestion du style par préférence utilisateur
		String etatNlleDepense = getUtilisateurCourant().getPreference(UtilisateurPrefsEnum.PREFS_STATUT_NLLE_DEPENSE, String.class);
		if(etatNlleDepense != null){
			getComponent().getListSelectEtat().setSelectedItem(EtatLigneDepenseEnum.getEnum(etatNlleDepense));
		}
		// Périodique
		getComponent().getCheckBoxPeriodique().setCaption(null);
		getComponent().getCheckBoxPeriodique().setValue(Boolean.FALSE);
		getComponent().getCheckBoxPeriodique().setDescription("Cocher pour une dépense mensuelle");

		// Description
		getComponent().getTextFieldDescription().setItems(getBudgetMensuelCourant().getSetLibellesDepensesForAutocomplete());
		getComponent().getTextFieldDescription().setNewItemHandler(this);
		// Bouton
		getComponent().getButtonValider().addClickListener(new ActionValiderCreationDepenseClickListener());
		getComponent().getButtonValider().setDescription("Valider l'opération et fermer l'écran de saisie");
		getComponent().getButtonValiderContinuer().addClickListener(new ActionValiderCreationDepenseClickListener());
		getComponent().getButtonValiderContinuer().setDescription("Valider l'opération et Créer une nouvelle opération");	
	}



	@Override
	public void accept(String t) {
		LOGGER.debug("Ajout de la description : {}", t);
		getComponent().getTextFieldDescription().setSelectedItem(t);
	}
}
