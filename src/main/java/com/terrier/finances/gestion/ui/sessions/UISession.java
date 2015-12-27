package com.terrier.finances.gestion.ui.sessions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Gestionnaire des UI par Session utilisateur
 * @author vzwingma
 *
 */
public class UISession {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UISession.class);

	private String idSession;
	
	/**
	 * Session Manager
	 * @param idSession idSessions
	 */
	public UISession(String idSession){
		LOGGER.trace("[INIT][{}] Session UI ", idSession);
		this.idSession = idSession;
	}

	/**
	 * Utilisateur courant
	 */
	private Utilisateur utilisateurCourant = new Utilisateur();

	/**
	 * Budget courant
	 */
	private BudgetMensuel budgetMensuelCourant;

	@SuppressWarnings("rawtypes")
	private Map<Class, AbstractUIController<? extends AbstractComponent>> mapControleurs = new HashMap<Class, AbstractUIController<? extends AbstractComponent>>();
	// Page principale
	private Layout mainLayout;

	private Window popupModale;


	/**
	 * Déconnexion de l'utilisateur
	 */
	public void deconnexion(){
		// Suppression de l'utilisateur
		this.utilisateurCourant = null;
		// Suppression de l'IHM
		getMainLayout().removeAllComponents();
		// Suppression de tous les controleurs
		mapControleurs.clear();
		
		// Invalidate Sessions
		VaadinSession vSession = VaadinSession.getCurrent();
		vSession.close();
		WrappedSession httpSession = vSession.getSession();
		//Invalidate HttpSession
		httpSession.invalidate();
		//Redirect the user to the login/default Page
		Page currentPage = Page.getCurrent();
		if(currentPage != null){
			currentPage.setLocation(VaadinServlet.getCurrent().getServletConfig().getServletContext().getContextPath()+"/ihm");
		}
		else{
			LOGGER.error("Erreur : Impossible de trouver la page courante. Pb de framework Vaadin");
		}
	}


	/**
	 * Enregistrement des controleurs
	 * @param controleur controleur à enregistrer
	 */
	public <CTRL extends AbstractUIController<? extends AbstractComponent>> void registerUIControler(CTRL controleur) {
		if(mapControleurs.get(controleur.getClass()) == null){
			LOGGER.info("[{}] Enregistrement du controleur : {}", this.idSession, controleur);
			mapControleurs.put(controleur.getClass(), controleur);
		}

	}



	/**
	 * @return the mapControleurs
	 */
	@SuppressWarnings("unchecked")
	public <CTRL extends AbstractUIController<? extends CustomComponent>> CTRL getControleur(Class<CTRL> classNameControleur) {
		return (CTRL) mapControleurs.get(classNameControleur);
	}

	/**
	 * Enregistrement de l'utilisateur
	 * @param utilisateur USER
	 */
	public boolean registerUtilisateur(Utilisateur utilisateur){
		LOGGER.info("[{}] Enregistrement de l'utilisateur : {}", idSession, utilisateur);
		this.utilisateurCourant = utilisateur;
		return true;
	}
	
	
	/**
	 * @return the utilisateurCourant
	 */
	public Utilisateur getUtilisateurCourant() {
		// LOGGER.trace("[{}] Utilisateur courant > {}", this.idSession, this.utilisateurCourant);
		return utilisateurCourant;
	}
	
	
	
	/**
	 * @return the idSession
	 */
	public String getIdSession() {
		return idSession;
	}

	/**
	 * @return the budgetMensuelCourant
	 */
	public BudgetMensuel getBudgetMensuelCourant() {
		return budgetMensuelCourant;
	}

	/**
	 * @param budgetMensuelCourant the budgetMensuelCourant to set
	 */
	public void setBudgetMensuelCourant(BudgetMensuel budgetMensuelCourant) {
		this.budgetMensuelCourant = budgetMensuelCourant;
	}



	/**
	 * @return le mainLayout
	 */
	public Layout getMainLayout() {
		return mainLayout;
	}

	/**
	 * @param mainLayout to set
	 */
	public void setMainLayout(Layout mainLayout) {
		this.mainLayout = mainLayout;
	}

	/**
	 * @return the popupModale
	 */
	public Window getPopupModale() {
		return popupModale;
	}

	/**
	 * @param popupModale the popupModale to set
	 */
	public void setPopupModale(Window popupModale) {
		UI.getCurrent().addWindow(popupModale);
		this.popupModale = popupModale;
	}
}
