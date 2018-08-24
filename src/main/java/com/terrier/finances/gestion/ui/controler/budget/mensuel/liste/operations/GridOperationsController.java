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
		this.budgetControleur.getComponent().getButtonCreate().setVisible(!editableMode);
	}

	/**
	 * Mise à jour de la vue suite aux données
	 * @param refreshAllTable : flag s'il faut tout effacer avant l'affichage
	 * @param budgetIsActif budget actif ?
	 * @param listeDepenses liste des dépenses à utiliser
	 */
	public void miseAJourVueDonnees(boolean budgetIsActif, List<LigneDepense> listeDepenses){
		// Ajout des opérations
		getComponent().setItems(listeDepenses);

		// Mise à jour des colonnes suivant l'activité du budget
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.AUTEUR.name()).setHidden(budgetIsActif);
		getComponent().getColumn(EntetesTableSuiviDepenseEnum.ACTIONS.name()).setHidden(!budgetIsActif);
	}


	/**
	 * @param budgetControleur the budgetControleur to set
	 */
	public void setBudgetControleur(BudgetMensuelController budgetControleur) {
		this.budgetControleur = budgetControleur;
	}


	/**
	 * @return the budgetControleur
	 */
	public BudgetMensuelController getBudgetControleur() {
		return budgetControleur;
	}
	
	
}
