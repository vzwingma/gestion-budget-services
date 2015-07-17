/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.ui.components.auth.Login;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.auth.LoginConnexionClickListener;
import com.vaadin.server.ThemeResource;

/**
 * Controleur du login
 * @author vzwingma
 *
 */
public class LoginController extends AbstractUIController<Login>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7301428518502835422L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ParametragesService.class);
	
	
	/**
	 * Démarrage du contoleur
	 * @param composant
	 */
	public LoginController(Login composant) {
		super(composant);
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#initDynamicComponentsOnPage()
	 */
	@Override
	public void initDynamicComponentsOnPage() {
		// Ajout controle
		getComponent().getButtonConnexion().addClickListener(new LoginConnexionClickListener(this.getComponent()));

	}


	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {
		
		LOGGER.info("Démarrage de l'application [{}][{}]", getServiceParams().getVersion(), getServiceParams().getBuildTime());
		
		getComponent().getTextLogin().setIcon(new ThemeResource("img/login.png"));
		getComponent().getPasswordField().setIcon(new ThemeResource("img/passwd.png"));

		getComponent().getTextLogin().setValue(getServiceParams().getDefaultLogin());
		getComponent().getPasswordField().setValue(getServiceParams().getDefaultPwd());

		
		getComponent().getLabelVersion().setValue("Version : " + getServiceParams().getVersion());
		getComponent().getLabelBuildTime().setValue("Build : " + getServiceParams().getBuildTime());
	}
}
