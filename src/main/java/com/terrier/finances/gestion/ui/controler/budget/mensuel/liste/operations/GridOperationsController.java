/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations;

import java.util.List;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridOperations;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;

/**
 * Grille des opérations du budget
 * @author vzwingma
 *
 */
public class GridOperationsController extends AbstractUIController<GridOperations>{


	private static final long serialVersionUID = 5190668755144306669L;


	/**
	 * Constructeur du controleur du composant
	 * @param composant
	 */
	public GridOperationsController(GridOperations composant) {
		super(composant);
	}


	/**
	 * Mise à jour de la vue
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {
		/**
		 * Table de suivi des dépenses
		 */
		getComponent().setWidth("100.0%");
		getComponent().setHeight("100.0%");
		getComponent().setColumnReorderingAllowed(false);
		getComponent().setResponsive(true);


		//getComponent().setStyleGenerator(styleGenerator);
		
		// Ajout du mode Dernier ligne sur la liste des dépenses
		TableSuiviDepensesActionMenuHandler handler = new TableSuiviDepensesActionMenuHandler();
		//		getComponent().addActionHandler(handler);
		getComponent().addItemClickListener(handler);
		/*
		try {

			// Table Factory pour le mode édition
			getComponent().setTableFieldFactory(new TableSuiviDepenseEditedFieldFactory(getServiceParams().getCategories()));
			// Style
			getComponent().setCellStyleGenerator(new TableDepensesCellStyle(this));
			// Tooltip
			getComponent().setItemDescriptionGenerator(new TableDepensesDescriptionGenerator(this));

		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}
		 */
	}


	/**
	 * Sette la table en mode édition
	 */
	public void setTableOnEditableMode(boolean editableMode){
		
		// Activation du tableau
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.TYPE.name()).setHidden(!editableMode);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.PERIODIQUE.name()).setHidden(!editableMode);
		// Inversion du champ Libelle
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.LIBELLE.name()).setHidden(!editableMode);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.name()).setHidden(editableMode);
		getComponent().getEditor().setEnabled(editableMode);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.DATE_OPERATION.name()).setWidth(editableMode ? GridOperations.TAILLE_COLONNE_DATE_EDITEE : GridOperations.TAILLE_COLONNE_DATE);
	}

	/**
	 * Mise à jour de la vue suite aux données
	 * @param refreshAllTable : flag s'il faut tout effacer avant l'affichage
	 * @param budgetIsActif budget actif ?
	 * @param listeDepenses liste des dépenses à utiliser
	 */
	public void miseAJourVueDonnees(boolean refreshAllDonnees, boolean budgetIsActif, List<LigneDepense> listeDepenses){

		// Ajout des opérations
		getComponent().setItems(listeDepenses);


		// Mise à jour des colonnes suivant l'activité du budget
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.AUTEUR.name()).setHidden(budgetIsActif);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.ACTIONS.name()).setHidden(!budgetIsActif);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.LIBELLE.name()).setHidden(budgetIsActif);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.name()).setHidden(!budgetIsActif);


		/*
		for (final LigneDepense ligneDepense : listeDepenses) {
				// Type Popup view pour avoir les notes
			Property<PopupView> property5 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.name());

			final RichTextArea rta = new RichTextArea();
			rta.setValue(ligneDepense.getNotes() != null ? ligneDepense.getNotes() : "");
			String noteStar = ligneDepense.getNotes() != null ? "  *" : "";
			final PopupView ppv = new PopupView(ligneDepense.getLibelle() + noteStar, rta);
			ppv.addPopupVisibilityListener(new PopupNoteVisibitilityListener(ligneDepense.name(), this));
			ppv.setHideOnMouseOut(false);
			property5.setValue(ppv);

			// Type String
			Property<String> property5b = item1.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE.name());
			if(budgetIsActif){
				property5b.setValue(ligneDepense.getLibelle());	
			}
			else{
				property5b.setValue(ligneDepense.getLibelle() + noteStar);
			}


			// Pas d'action pour les réserves
			if(!BusinessDepensesService.ID_SS_CAT_RESERVE.equals(ligneDepense.getSsCategorie().name())
					&& !BusinessDepensesService.ID_SS_CAT_PREVISION_SANTE.equals(ligneDepense.getSsCategorie().name())
					&& budgetIsActif){
				Property<ActionsLigneBudget> property11 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.ACTIONS.name());
				ActionsLigneBudget actions = new ActionsLigneBudget();
				actions.getControleur().setidDepense(ligneDepense.name());
				actions.getControleur().miseAJourEtatLigne(ligneDepense.getEtat());
				property11.setValue(actions);
			}
		}
		getComponent().refreshRowCache();
		 */
	}


	/**
	 * Validation du formulaire
	 */
	public boolean validateEditableForm(){
		/*
		List<String> messagesErreurs = new ArrayList<String>();
		String idLigneEditable = ((TableSuiviDepenseEditedFieldFactory)getComponent().getTableFieldFactory()).getIdLigneEditable();
		if(getComponent().isEditable() && idLigneEditable != null){
			for (Object itemID : getComponent().getItemIds()) {
				Item itemLigneDepense = getComponent().getItem(itemID);

				String description = itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE.getId()).getValue().toString();

				if(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.CATEGORIE.getId()).getValue() == null){
					messagesErreurs.add(description + " : La catégorie est obligatoire");
				}
				if(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId()).getValue() == null){
					messagesErreurs.add(description + " : La sous-catégorie est obligatoire");
				}
				if(description == null || description.isEmpty()){
					messagesErreurs.add(description + " : La description est obligatoire");
				}
				ValeurDepenseValidator validator = new ValeurDepenseValidator("La valeur est incorrecte");
				try{
					validator.validate(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.VALEUR.getId()).getValue());
				}
				catch(InvalidValueException e){
					messagesErreurs.add(description + " : La valeur est obligatoire");
				}
				// Vérification de la cohérence des données :
				CategorieDepense ssCategorie = (CategorieDepense)itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId()).getValue();
				TypeDepenseEnum type = (TypeDepenseEnum)itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.TYPE.getId()).getValue();

				TypeDepenseValidator typeValidator = new TypeDepenseValidator(ssCategorie);
				try{
					typeValidator.validate(type);
				}
				catch(InvalidValueException e){
					messagesErreurs.add(description + " : " + e.getMessage());
				}
			}
		}

		StringBuilder b = new StringBuilder();
		for (String string : messagesErreurs) {
			b.append(string).append("\n");
		}
		if(!messagesErreurs.isEmpty()){
			Notification.show(b.toString(), Notification.Type.WARNING_MESSAGE);
		}
		return messagesErreurs.isEmpty();
		 */
		return true;
	}
}
