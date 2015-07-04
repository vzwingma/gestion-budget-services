/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.UISessionManager;
import com.terrier.finances.gestion.ui.components.auth.Login;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.auth.LoginController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

/**
 * Connexion
 * @author vzwingma
 *
 */
public class LoginConnexionClickListener extends AbstractComponentListener implements Button.ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9208265594447141871L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginConnexionClickListener.class);

	@Override
	public void buttonClick(ClickEvent event) {

		Login pageLogin = (Login)event.getButton().getParent().getParent().getParent().getParent();
		boolean auth = getControleur(LoginController.class).getServiceAuthentification().validate(
				pageLogin.getTextLoginValue(), 
				pageLogin.getPasswordField().getValue());
		if(auth){	
			LOGGER.info("Accès autorisé pour {}", pageLogin.getTextLoginValue());
			// MAJ
			UISessionManager.getSession().getMainLayout().removeAllComponents();
			UISessionManager.getSession().getMainLayout().addComponent(new BudgetMensuelPage());

		}
		else{
			LOGGER.error("****************** ECHEC AUTH ***********************");
			Notification.show("Les login et mot de passe sont incorrects", Notification.Type.ERROR_MESSAGE);
		}
	}

}
