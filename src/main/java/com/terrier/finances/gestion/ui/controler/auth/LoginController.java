/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.ui.components.auth.Login;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;

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
		
		initDynamicComponentsOnPage();
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#initDynamicComponentsOnPage()
	 */
	public void initDynamicComponentsOnPage() {
		// Ajout controle
		getComponent().getButtonConnexion().addClickListener(
			e -> authenticateUser(
				getComponent().getTextLogin().getValue(), 
				getComponent().getPasswordField().getValue()));
		getComponent().getTextLogin().focus();
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {

		LOGGER.info("Démarrage de l'application [{}][{}]", getServiceParams().getVersion(), getServiceParams().getBuildTime());

		getComponent().getTextLogin().setIcon(new ThemeResource("img/login.png"));
		getComponent().getPasswordField().setIcon(new ThemeResource("img/passwd.png"));
		getComponent().getLabelVersion().setValue("Version : " + getServiceParams().getVersion());
		getComponent().getLabelBuildTime().setValue("Build : " + getServiceParams().getBuildTime());
	}



	/**
	 * Méthode d'authenticiation de l'utilisateur
	 * @param login de l'utilisateur
	 * @param passwordEnClair en clair de l'utilisateur
	 */
	public void authenticateUser(String login, String passwordEnClair){
		Utilisateur utilisateur = getServiceAuthentification().authenticate(
				login, passwordEnClair);
		if(utilisateur != null){
			getUISession().registerUtilisateur(utilisateur);
			LOGGER.info("Accès autorisé pour {}", login);
			// MAJ
			getUISession().getMainLayout().removeAllComponents();
			getUISession().getMainLayout().addComponent(new BudgetMensuelPage());

		}
		else{
			LOGGER.error("****************** ECHEC AUTH ***********************");
			Notification.show("Les login et mot de passe sont incorrects", Notification.Type.ERROR_MESSAGE);
		}
	}
}
