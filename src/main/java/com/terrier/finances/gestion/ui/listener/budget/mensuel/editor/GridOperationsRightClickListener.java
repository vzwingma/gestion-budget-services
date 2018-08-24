package com.terrier.finances.gestion.ui.listener.budget.mensuel.editor;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.GridOperationsController;
import com.vaadin.contextmenu.GridContextMenu.GridContextMenuOpenListener;
import com.vaadin.ui.Notification;

/**
 * 
 * Action sur une opération du tableau pour la déclarer dernière opération
 * @author vzwingma
 *
 */
public class GridOperationsRightClickListener implements GridContextMenuOpenListener<LigneDepense>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6541406098405268390L;

	// Controleur
	private GridOperationsController controleur;
	
	
	/**
	 * Constructeur avec le controleur associé
	 * @param controleur
	 */
	public GridOperationsRightClickListener(GridOperationsController controleur){
		this.controleur = controleur;
	}


	@Override
	public void onContextMenuOpen(
			com.vaadin.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<LigneDepense> event) {
		if(this.controleur != null){
			this.controleur.getBudgetControleur().setLigneDepenseAsDerniereOperation(event.getItem());
			Notification.show("L'opération est tagguée comme la dernière opération exécutée", Notification.Type.TRAY_NOTIFICATION);
		}
		else{
			Notification.show("Erreur lors du marquage de l'opération", Notification.Type.WARNING_MESSAGE);
		}
	}

}
