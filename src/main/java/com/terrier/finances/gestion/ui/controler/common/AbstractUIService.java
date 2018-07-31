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
public interface AbstractUIService  {


	/**
	 * Set popup modale
	 * @param popupModale enregistre la popup
	 */
	public default void setPopupModale(Window popupModale){
		getUISession().setPopupModale(popupModale);
	}

	/**
	 * @return l'utilisateur courant
	 */
	public default Utilisateur getUtilisateurCourant(){
		return getUISession().getUtilisateurCourant();
	}

	/**
	 * @return le budget mensuel courant
	 */
	public default void updateBudgetCourantInSession(BudgetMensuel budget){
		getUISession().setBudgetMensuelCourant(budget);
	}
	/**
	 * @return le budget mensuel courant
	 */
	public default BudgetMensuel getBudgetMensuelCourant(){
		return getUISession().getBudgetMensuelCourant();
	}

	/**
	 * @return la session de l'UI
	 */
	public default UISession getUISession(){
		return UISessionManager.get().getSession();
	}

	/**
	 * @return l'id de la session
	 */
	public default String getIdSession(){
		return getUISession().getIdSession();
	}
}
