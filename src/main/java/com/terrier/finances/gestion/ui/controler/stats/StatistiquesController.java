package com.terrier.finances.gestion.ui.controler.stats;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.UISessionManager;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.stats.StatistiquesPage;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.ChangePageListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionDeconnexionClickListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

/**
 * Controleur de la page des statistiques
 * @author vzwingma
 *
 */
public class StatistiquesController extends AbstractUIController<StatistiquesPage> implements ValueChangeListener, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6410285178655721867L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatistiquesController.class);
	
	private ComboBox compte;
	
	/**
	 * Constructure du Controleur du composant
	 * @param composant
	 */
	public StatistiquesController(StatistiquesPage composant) {
		super(composant);
	}


	@Override
	public void initDynamicComponentsOnPage() {

		// Démarrage
		getComponent().getComboBoxComptes().setNullSelectionAllowed(false);
		getComponent().getComboBoxComptes().setImmediate(true);
		
		getComponent().getButtonDeconnexion().setCaption("");
		getComponent().getButtonDeconnexion().addClickListener(new ActionDeconnexionClickListener());
		getComponent().getButtonDeconnexion().setDescription("Déconnexion de l'application");
		getComponent().getButtonBudget().addClickListener(new ChangePageListener(BudgetMensuelPage.class));
	
		this.compte = getComponent().getComboBoxComptes();
		this.compte.setImmediate(true);
		this.compte.setDescription("Choix du compte");
		this.compte.setNewItemsAllowed(false);
		this.compte.setNullSelectionAllowed(false);
		try{
			List<CompteBancaire> comptes = getServiceParams().getComptesUtilisateur(UISessionManager.getSession().getUtilisateurCourant());
			for (CompteBancaire compte : comptes) {
				this.compte.addItem(compte.getId());
				this.compte.setItemCaption(compte.getId(), compte.getLibelle());
				if(compte.isDefaut()){
					this.compte.select(compte.getId());
				}
				if(getComponent().getIdCompteSelectionne() != null && compte.getId().equals(getComponent().getIdCompteSelectionne())){
					this.compte.select(compte.getId());				
				}
				this.compte.setItemIcon(compte.getId(), new ThemeResource(compte.getItemIcon()));
			}
			this.compte.setTextInputAllowed(false);
			this.compte.addValueChangeListener(this);
		} catch (DataNotFoundException e) {
			Notification.show("Impossible de charger les comptes de "+ UISessionManager.getSession().getUtilisateurCourant(), Notification.Type.ERROR_MESSAGE);
		}
		
	}


	@Override
	public void miseAJourVueDonnees() {
		

	}


    
    
    
    
    
    
    
    
    
    
    
	@Override
	public void valueChange(ValueChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

