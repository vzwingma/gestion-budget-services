/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.common;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.ui.sessions.UISession;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.Window;

/**
 * Méthodes génériques à tous les composants UI
 * @author vzwingma
 *
 */
public abstract class AbstractUIService  {


	/**
	 * Set popup modale
	 * @param popupModale enregistre la popup
	 */
	public void setPopupModale(Window popupModale){
		getUISession().setPopupModale(popupModale);
	}

	/**
	 * @return l'utilisateur courant
	 */
	public Utilisateur getUtilisateurCourant(){
		return getUISession().getUtilisateurCourant();
	}


	/**
	 * @return le budget mensuel courant
	 */
	public BudgetMensuel getBudgetMensuelCourant(){
		return getUISession().getBudgetMensuelCourant();
	}

	/**
	 * @return la session de l'UI
	 */
	public UISession getUISession(){
		return UISessionManager.get().getSession();
	}

	/**
	 * @return l'id de la session
	 */
	public String getIdSession(){
		return getUISession().getIdSession();
	}
}
