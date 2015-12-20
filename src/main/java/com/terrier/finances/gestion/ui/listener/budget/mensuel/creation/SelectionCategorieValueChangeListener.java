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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

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

	@Override
	public void valueChange(ValueChangeEvent event) {
		CategorieDepense categorie = (CategorieDepense)event.getProperty().getValue();
		controleur.getComponent().getComboBoxSsCategorie().removeAllItems();
		
		if(categorie != null){
			if(categorie.getListeSSCategories() != null){
				List<CategorieDepense> listeSSCategories =  new ArrayList<>(categorie.getListeSSCategories());
				Collections.sort(listeSSCategories);
				for (CategorieDepense ssCategorie : listeSSCategories) {
					if(ssCategorie.isActif()){
						controleur.getComponent().getComboBoxSsCategorie().addItem(ssCategorie);
					}
				};
				controleur.getComponent().getComboBoxSsCategorie().setEnabled(true);
			}
		}
		else{
			controleur.getComponent().getComboBoxSsCategorie().setEnabled(false);
		}
	}
}
