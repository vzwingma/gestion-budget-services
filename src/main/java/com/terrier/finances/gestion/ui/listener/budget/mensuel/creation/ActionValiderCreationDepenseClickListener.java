/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.CreerDepenseForm;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.creer.operation.CreerDepenseController;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Validation de la création d'une dépense
 * @author vzwingma
 *
 */
public class ActionValiderCreationDepenseClickListener extends AbstractComponentListener implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823872638217135776L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionValiderCreationDepenseClickListener.class);



	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		CreerDepenseForm form = (CreerDepenseForm)event.getButton().getParent().getParent().getParent().getParent();

		LOGGER.debug("[IHM] Validation du formulaire de création");
		Optional<TypeDepenseEnum> typeSelected = form.getComboboxType().getSelectedItem();
		TypeDepenseEnum type = typeSelected.isPresent() ? typeSelected.get() : TypeDepenseEnum.DEPENSE;

		Optional<EtatLigneDepenseEnum> etatSelected = form.getListSelectEtat().getSelectedItem();
		EtatLigneDepenseEnum etat = etatSelected.isPresent() ? etatSelected.get() : EtatLigneDepenseEnum.PREVUE;

		Optional<CategorieDepense> categorieSelected = form.getComboBoxSsCategorie().getSelectedItem();
		Optional<String> descriptionSelected = form.getTextFieldDescription().getSelectedItem();


		LigneDepense newOperation = new LigneDepense(
				categorieSelected.isPresent() ? categorieSelected.get() : null, 
				descriptionSelected.isPresent() ? descriptionSelected.get() : null, 
				type,
				form.getTextFieldValeur().getValue(),
				etat,
				form.getCheckBoxPeriodique().getValue(),
				getBudgetMensuelCourant().isActif());
		LOGGER.debug("[IHM]  >  {}", newOperation);
		boolean resultat = getControleur(CreerDepenseController.class).validateAndCreate(newOperation, form.getComboboxComptes().getSelectedItem());

		if(resultat){
			/**
			 * MAJ des tableaux
			 */
			BudgetMensuelController controleur = getControleur(BudgetMensuelController.class);
			if(event.getButton().getCaption().contains("Fermer")){
				// Fin du formulaire
				getUserSession().getPopupModale().close();
				controleur.miseAJourVueDonnees();
			}
			else{
				// Reset du formulaire
				form.getControleur().miseAJourVueDonnees();
				controleur.miseAJourVueDonnees();		
			}
		}
	}
}

