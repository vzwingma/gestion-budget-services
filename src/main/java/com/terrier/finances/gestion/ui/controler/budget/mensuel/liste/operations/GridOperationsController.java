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

	private BudgetMensuelController budgetControleur;

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
		getComponent().setSizeFull();
		getComponent().setColumnReorderingAllowed(false);
		getComponent().setResponsive(true);
	}

	/**
	 * Sette la table en mode édition
	 */
	public void updateViewGridOnEditableMode(boolean editableMode){

//		// Activation du tableau
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.TYPE.name()).setHidden(!editableMode);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.PERIODIQUE.name()).setHidden(!editableMode);
		this.budgetControleur.getComponent().getButtonCreate().setVisible(!editableMode);
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


	/**
	 * @param budgetControleur the budgetControleur to set
	 */
	public void setBudgetControleur(BudgetMensuelController budgetControleur) {
		this.budgetControleur = budgetControleur;
	}
	
	
}
