package com.terrier.finances.gestion.ui.listener.budget.mensuel.editor;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.GridOperationsController;
import com.vaadin.ui.Notification;
import com.vaadin.ui.components.grid.EditorCancelEvent;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorOpenEvent;
import com.vaadin.ui.components.grid.EditorOpenListener;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.components.grid.EditorSaveListener;

/**
 * Listener de l'éditor de la grille des opérations
 * @author vzwingma
 *
 */
public class GridOperationsEditorListener implements EditorCancelListener<LigneDepense>, EditorSaveListener<LigneDepense>, EditorOpenListener<LigneDepense> {

	//
	private static final long serialVersionUID = -4092876167681783200L;

	// Controleur
	private GridOperationsController controler;

	public GridOperationsEditorListener(GridOperationsController controller) {
		this.controler = controller;
	}


	@Override
	public void onEditorCancel(EditorCancelEvent<LigneDepense> event) {
		this.controler.updateViewGridOnEditableMode(false);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.components.grid.EditorOpenListener#onEditorOpen(com.vaadin.ui.components.grid.EditorOpenEvent)
	 */
	@Override
	public void onEditorOpen(EditorOpenEvent<LigneDepense> event) {
		this.controler.updateViewGridOnEditableMode(true);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.components.grid.EditorSaveListener#onEditorSave(com.vaadin.ui.components.grid.EditorSaveEvent)
	 */
	@Override
	public void onEditorSave(EditorSaveEvent<LigneDepense> event) {
		// Recalcul du budget
		BudgetMensuel budget = this.controler.getServiceOperations().calculEtSauvegardeBudget(this.controler.getBudgetMensuelCourant());
		this.controler.updateBudgetCourantInSession(budget);
		// MAJ des tableaux
		this.controler.getBudgetControleur().miseAJourVueDonnees();
		Notification.show("L'opération a bien été mise à jour", Notification.Type.TRAY_NOTIFICATION);
		this.controler.updateViewGridOnEditableMode(false);
	}
}

