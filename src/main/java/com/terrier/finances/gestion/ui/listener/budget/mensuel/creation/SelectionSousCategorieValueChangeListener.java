/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;

/**
 * Changement d'une ss catégorie dans le formulaire de création
 * Affichage du transfert intercompte
 * @author vzwingma
 *
 */
public class SelectionSousCategorieValueChangeListener extends AbstractComponentListener implements ValueChangeListener{

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
	public void valueChange(ValueChangeEvent event) {
		CategorieDepense ssCategorie = (CategorieDepense)event.getProperty().getValue();
		
		/**
		 * Sélection d'un virement intercompte
		 */
		boolean interCompte = false;
		if(ssCategorie != null){
			interCompte = BusinessDepensesService.ID_SS_CAT_TRANSFERT_INTERCOMPTE.equals(ssCategorie.getId());
		}
		controleur.getComponent().getListSelectComptes().setImmediate(true);
		controleur.getComponent().getLabelCompte().setImmediate(true);
		controleur.getComponent().getListSelectComptes().setVisible(interCompte);
		controleur.getComponent().getLayoutCompte().setVisible(interCompte);
		controleur.getComponent().getLabelCompte().setVisible(interCompte);
		

		/**
		 * Préparation du type de dépense
		 */
		if(ssCategorie != null){
			TypeDepenseEnum typeAttendu = TypeDepenseEnum.DEPENSE;
			if(BusinessDepensesService.ID_SS_CAT_SALAIRE.equals(ssCategorie.getId()) || BusinessDepensesService.ID_SS_CAT_REMBOURSEMENT.equals(ssCategorie.getId())){
				typeAttendu = TypeDepenseEnum.CREDIT;
			}
			controleur.getComponent().getListSelectType().select(typeAttendu);
		}

	}
}
