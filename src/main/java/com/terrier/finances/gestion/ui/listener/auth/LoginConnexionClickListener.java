/**
 * 
 */
package com.terrier.finances.gestion.ui.listener.auth;


import com.terrier.finances.gestion.ui.components.auth.Login;
import com.terrier.finances.gestion.ui.controler.auth.LoginController;
import com.terrier.finances.gestion.ui.controler.common.AbstractComponentListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

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
	

	/*
	 * Page de login
	 */
	private Login loginPage;
	
	public LoginConnexionClickListener(Login loginPage){
		this.loginPage = loginPage;
	}
	
	
	@Override
	public void buttonClick(ClickEvent event) {
		// Authentification
		getControleur(LoginController.class).authenticateUser(
				this.loginPage.getTextLogin().getValue(), 
				this.loginPage.getPasswordField().getValue());
	}
}
