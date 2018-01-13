/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.Set;
import java.util.stream.Stream;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.creer.operation.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;

/**
 * Changement d'une catégorie dans le formulaire de création
 * @author vzwingma
 *
 */
public class SelectionCategorieValueChangeListener extends AbstractComponentListener implements ValueChangeListener<Set<CategorieDepense>>{

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
	public void valueChange(ValueChangeEvent<Set<CategorieDepense>> event) {
		Set<CategorieDepense> categories = event.getValue();
		CategorieDepense categorie = categories.iterator().next();

		// Sélection d'une catégorie
		// Alimentation de la liste des sous catégories
		if(categorie != null && categorie.getListeSSCategories() != null){

			Stream<CategorieDepense> streamSSCategories = categorie.getListeSSCategories().stream().filter(cat -> cat.isActif()).sorted();
			controleur.getComponent().getComboBoxSsCategorie().setItems(streamSSCategories);
			// #51 : S'il n'y a qu'un seul élément : sélection automatique de celui ci
			if(streamSSCategories.count() == 1){
				controleur.getComponent().getComboBoxSsCategorie().select(streamSSCategories.findFirst().get());
			}
			controleur.getComponent().getComboBoxSsCategorie().setEnabled(true);
		}
		else{
			controleur.getComponent().getComboBoxSsCategorie().setEnabled(false);
		}
	}
}
