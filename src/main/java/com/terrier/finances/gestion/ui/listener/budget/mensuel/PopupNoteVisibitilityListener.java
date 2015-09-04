package com.terrier.finances.gestion.ui.listener.budget.mensuel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RichTextArea;

/**
 * Listener de visibilité de la popup de notes sur une dépense
 * @author vzwingma
 *
 */
public class PopupNoteVisibitilityListener implements PopupVisibilityListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8274118224737404597L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PopupNoteVisibitilityListener.class);

	// Ligne de dépense
	private final String idLigneDepense;
	// Controleur
	private final BusinessDepensesService serviceDepenses;

	/**
	 * Popup visibility listener
	 * @param ligneDepense
	 * @param controleur
	 */
	public PopupNoteVisibitilityListener(final String idLigneDepense, final BusinessDepensesService serviceDepenses){
		this.idLigneDepense = idLigneDepense;
		this.serviceDepenses = serviceDepenses;
	}


	/* (non-Javadoc)
	 * @see com.vaadin.ui.PopupView.PopupVisibilityListener#popupVisibilityChange(com.vaadin.ui.PopupView.PopupVisibilityEvent)
	 */
	@Override
	public void popupVisibilityChange(PopupVisibilityEvent event) {
		if(!event.isPopupVisible()){
			// Mise à jour de la ligne de dépense
			final RichTextArea rta = (RichTextArea)event.getPopupView().getContent().getPopupComponent();
			LOGGER.debug("[IHM] Dépense [{}] Mise à jour de la note [{}]", idLigneDepense, rta.getValue());

			try{
				this.serviceDepenses.majNotesLignesDepenses(
						UISessionManager.getSession().getBudgetMensuelCourant(), 
						idLigneDepense, 
						rta.getValue(), 
						UISessionManager.getSession().getUtilisateurCourant().getLogin());

				String libellePPV = event.getPopupView().getContent().getMinimizedValueAsHTML();
				libellePPV = libellePPV != null ? libellePPV.replaceAll("\\*", "").trim() : "";
				if(rta.getValue() != null && rta.getValue().length() > 0){
					libellePPV += "  *";
				}
				final String libellePopupView = libellePPV;

				event.getPopupView().setContent(new PopupView.Content() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 3147461186004228578L;

					@Override
					public Component getPopupComponent() {
						return rta;
					}

					@Override
					public String getMinimizedValueAsHTML() {
						return libellePopupView;
					}
				});
			}
			catch(DataNotFoundException e){
				Notification.show("La dépense ["+idLigneDepense+"] est introuvable ou n'a pas été enregistrée", Type.ERROR_MESSAGE);
			}
		}
	}
}