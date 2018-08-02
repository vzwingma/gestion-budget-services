/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.Optional;

import com.terrier.finances.gestion.business.OperationsService;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.creer.operation.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;

/**
 * Changement d'une ss catégorie dans le formulaire de création
 * Affichage du transfert intercompte
 * @author vzwingma
 *
 */
public class SelectionSousCategorieValueChangeListener extends AbstractComponentListener implements SingleSelectionListener<CategorieDepense>{

	// Controleur
	private CreerDepenseController controleur;
	
	public SelectionSousCategorieValueChangeListener(CreerDepenseController controleur){
		this.controleur = controleur;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7460353635366793837L;

	/**
	 * Si transfert intercompte affichage du choix du compte
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void selectionChange(SingleSelectionEvent<CategorieDepense> event) {
		Optional<CategorieDepense> catSelected = event.getFirstSelectedItem();
		if(catSelected.isPresent()){
			CategorieDepense ssCategorie = catSelected.get();	
			/**
			 * Sélection d'un virement intercompte
			 */
			boolean interCompte = false;
			if(ssCategorie != null){
				interCompte = OperationsService.ID_SS_CAT_TRANSFERT_INTERCOMPTE.equals(ssCategorie.getId());
			}
			controleur.getComponent().getComboboxComptes().setVisible(interCompte);
			controleur.getComponent().getLayoutCompte().setVisible(interCompte);
			controleur.getComponent().getLabelCompte().setVisible(interCompte);

			/**
			 * Préparation du type de dépense
			 */
			if(ssCategorie != null){
				TypeDepenseEnum typeAttendu = TypeDepenseEnum.DEPENSE;
				if(OperationsService.ID_SS_CAT_SALAIRE.equals(ssCategorie.getId()) || OperationsService.ID_SS_CAT_REMBOURSEMENT.equals(ssCategorie.getId())){
					typeAttendu = TypeDepenseEnum.CREDIT;
				}
				controleur.getComponent().getComboboxType().setSelectedItem(typeAttendu);
			}
		}
	}
}
