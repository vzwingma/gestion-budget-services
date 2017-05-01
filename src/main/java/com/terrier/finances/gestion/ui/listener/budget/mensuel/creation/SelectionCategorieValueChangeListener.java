/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;

/**
 * Changement d'une catégorie dans le formulaire de création
 * @author vzwingma
 *
 */
public class SelectionCategorieValueChangeListener extends AbstractComponentListener implements ValueChangeListener{

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
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void valueChange(ValueChangeEvent event) {
		CategorieDepense categorie = (CategorieDepense)event.getProperty().getValue();
		controleur.getComponent().getComboBoxSsCategorie().removeAllItems();
		// Sélection d'une catégorie
		if(categorie != null){
			// Alimentation de la liste des sous catégories
			if(categorie.getListeSSCategories() != null){
				List<CategorieDepense> listeSSCategories =  new ArrayList<>(categorie.getListeSSCategories());
				Collections.sort(listeSSCategories);
				for (CategorieDepense ssCategorie : listeSSCategories) {
					if(ssCategorie.isActif()){
						controleur.getComponent().getComboBoxSsCategorie().addItem(ssCategorie);
					}
				};
				// #51 : S'il n'y a qu'un seul élément : sélection automatique de celui ci
				if(controleur.getComponent().getComboBoxSsCategorie().getItemIds().size() == 1){
					controleur.getComponent().getComboBoxSsCategorie().select(controleur.getComponent().getComboBoxSsCategorie().getItemIds().iterator().next());
				}
				
				controleur.getComponent().getComboBoxSsCategorie().setEnabled(true);
			}
		}
		else{
			controleur.getComponent().getComboBoxSsCategorie().setEnabled(false);
		}
	}
}
