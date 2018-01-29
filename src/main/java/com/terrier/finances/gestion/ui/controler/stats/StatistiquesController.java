package com.terrier.finances.gestion.ui.controler.stats;

import java.io.Serializable;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.stats.StatistiquesPage;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.ChangePageListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.boutons.ActionDeconnexionClickListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.ui.ComboBox;

/**
 * Controleur de la page des statistiques
 * @author vzwingma
 * @deprecated
 */
@Deprecated
public class StatistiquesController extends AbstractUIController<StatistiquesPage> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6410285178655721867L;

	
	private ComboBox<CompteBancaire> compte;
	
	/**
	 * Constructure du Controleur du composant
	 * @param composant
	 */
	public StatistiquesController(StatistiquesPage composant) {
		super(composant);
	}


	@Deprecated
	public void initDynamicComponentsOnPage() {

		// Démarrage
		getComponent().getComboBoxComptes().setEmptySelectionAllowed(false);
		
		getComponent().getButtonDeconnexion().setCaption("");
		getComponent().getButtonDeconnexion().addClickListener(new ActionDeconnexionClickListener());
		getComponent().getButtonDeconnexion().setDescription("Déconnexion de l'application");
		getComponent().getButtonBudget().addClickListener(new ChangePageListener(BudgetMensuelPage.class));
	
		this.compte = getComponent().getComboBoxComptes();
		this.compte.setDescription("Choix du compte");
		this.compte.setTextInputAllowed(false);
		this.compte.setEmptySelectionAllowed(false);
	}


	@Override
	public void miseAJourVueDonnees() {
		// Rien à faire pour le moment
	}
}

