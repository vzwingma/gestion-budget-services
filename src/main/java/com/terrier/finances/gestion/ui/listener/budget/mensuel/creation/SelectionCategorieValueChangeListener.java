/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.creer.operation.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;

/**
 * Changement d'une catégorie dans le formulaire de création
 * @author vzwingma
 *
 */
public class SelectionCategorieValueChangeListener extends AbstractComponentListener implements SingleSelectionListener<CategorieDepense>{

	// Controleur
	private CreerDepenseController controleur;


	public SelectionCategorieValueChangeListener(CreerDepenseController controleur){
		this.controleur = controleur;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7460353635366793837L;

	/**
	 * Sélection d'une catégorie
	 * @see com.vaadin.event.selection.SelectionListener#selectionChange(com.vaadin.event.selection.SelectionEvent)
	 */
	@Override
	public void selectionChange(SingleSelectionEvent<CategorieDepense> event) {
		
		controleur.getComponent().getComboBoxSsCategorie().clear();
		controleur.getComponent().getComboBoxSsCategorie().setSelectedItem(null);
		
		Optional<CategorieDepense> categories = event.getSelectedItem();
		if(categories.isPresent()){
			CategorieDepense categorie = categories.get();
			
			// Sélection d'une catégorie
			// Alimentation de la liste des sous catégories
			if(categorie != null && categorie.getListeSSCategories() != null){

				List<CategorieDepense> streamSSCategories = categorie.getListeSSCategories()
						.stream()
						.filter(CategorieDepense::isActif)
						.sorted()
						.collect(Collectors.toList());
				controleur.getComponent().getComboBoxSsCategorie().setItems(streamSSCategories);
				// #51 : S'il n'y a qu'un seul élément : sélection automatique de celui ci
				if(streamSSCategories.size() == 1){
					controleur.getComponent().getComboBoxSsCategorie().setSelectedItem(streamSSCategories.get(0));
				}
				else{
					controleur.getComponent().getComboBoxSsCategorie().setSelectedItem(null);
				}
				controleur.getComponent().getComboBoxSsCategorie().setEnabled(true);
			}
			else{
				controleur.getComponent().getComboBoxSsCategorie().setEnabled(false);
			}
		}
	}
}
