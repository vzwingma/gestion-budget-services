/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.budget.mensuel.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.CreerDepenseForm;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.BudgetMensuelController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;

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

		form.getTextFieldValeur().setValue(form.getTextFieldValeur().getValue().replaceAll(",", "."));

		LOGGER.debug("[IHM] Validation du formulaire de création");
		// Validation
		if(form.getControleur().validateForm()){
			// Si oui création
			TypeDepenseEnum type = (TypeDepenseEnum)form.getListSelectType().getConvertedValue();
			if(type == null){
				type = TypeDepenseEnum.DEPENSE;
			}
			EtatLigneDepenseEnum etat = (EtatLigneDepenseEnum)form.getListSelectEtat().getConvertedValue();
			if(etat == null){
				etat = EtatLigneDepenseEnum.PREVUE;
			}

			LigneDepense ligneDepense = new LigneDepense(
					(CategorieDepense)form.getComboBoxSsCategorie().getValue(), 
					(String)form.getTextFieldDescription().getValue(), 
					type,
					Float.valueOf(form.getTextFieldValeur().getValue()),
					etat,
					form.getCheckBoxPeriodique().getValue());
			LOGGER.debug("[IHM]  >  {}", ligneDepense);
			String auteur = getUtilisateurCourant().getLibelle();
			BudgetMensuel budget = getBudgetMensuelCourant();
			try{
				if(BusinessDepensesService.ID_SS_CAT_TRANSFERT_INTERCOMPTE.equals(ligneDepense.getSsCategorie().getId())){
					LOGGER.info("[IHM] Ajout d'un nouveau transfert intercompte");
					getControleur(BudgetMensuelController.class).getServiceDepense().ajoutLigneTransfertIntercompte(budget.getId(), ligneDepense, ((String)form.getListSelectComptes().getConvertedValue()), getUtilisateurCourant());
					Notification.show("Le transfert inter-compte a bien été créée", Notification.Type.TRAY_NOTIFICATION);
				}
				else{
					LOGGER.info("[IHM] Ajout d'une nouvelle dépense");
					getControleur(BudgetMensuelController.class).getServiceDepense().ajoutLigneDepenseEtCalcul(budget.getId(), ligneDepense, auteur);
					Notification.show("La dépense a bien été créée", Notification.Type.TRAY_NOTIFICATION);
				}
			}
			catch(Exception e){
				Notification.show("Impossible de créer la dépense : " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}

			/**
			 * MAJ des tableaux
			 */
			BudgetMensuelController controleur = getControleur(BudgetMensuelController.class);
			
			if(event.getButton().getCaption().contains("Fermer")){
				// Fin du formulaire
				getUISession().getPopupModale().close();
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

