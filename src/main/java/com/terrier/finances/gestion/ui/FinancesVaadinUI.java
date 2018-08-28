package com.terrier.finances.gestion.ui;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.components.auth.Login;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.FacadeServices;
import com.terrier.finances.gestion.ui.sessions.UserSessionsManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * IHM FINANCES
 * @author vzwingma
 *
 */
@Theme("mytheme")
@Title("Gestion de budget")
public class FinancesVaadinUI extends UI
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3105864589672927628L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FinancesVaadinUI.class);


	@WebServlet(value = {"/ihm/*", "/VAADIN/*"}, asyncSupported = false)
	@VaadinServletConfiguration(productionMode = false, ui = FinancesVaadinUI.class, widgetset = "com.terrier.finances.gestion.AppWidgetSet")
	public static class Servlet extends VaadinServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1941895602784719745L;
	}

	@Override
	protected void init(VaadinRequest request) {
		LOGGER.debug("[INIT] FinancesVaadinUI - IdSession : {}", this.getSession().getCsrfToken());
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSizeFull();
		setContent(layout);
		setSizeFull();
		UserSessionsManager.get().getSession().setMainLayout(layout);
		UI.setCurrent(this);

		// Refresh
		int pollInterval = Integer.parseInt(FacadeServices.get().getServiceParams().getUiRefreshPeriod());
		UI.getCurrent().setPollInterval(pollInterval);
		LOGGER.debug("[INIT] FinancesVaadinUI - PoolInterval de {} ms", pollInterval);

		if(!UserSessionsManager.get().getSession().isActive()){
			// Page de login au démarrage si non authentifié
			layout.addComponent(new Login());
		}
		else{
			// Sinon la page en cours
			layout.addComponent(new BudgetMensuelPage());
		}
	}
}
