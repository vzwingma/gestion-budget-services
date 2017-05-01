package com.terrier.finances.gestion.ui.controler.stats;

import java.io.Serializable;
import java.util.List;

import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.components.stats.StatistiquesPage;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.ChangePageListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionDeconnexionClickListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.v7.ui.ComboBox;
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
		int ordreCompte = 100;
		try{
			List<CompteBancaire> comptes = getServiceParams().getComptesUtilisateur(getUtilisateurCourant());
			for (CompteBancaire compte : comptes) {
				this.compte.addItem(compte.getId());
				this.compte.setItemCaption(compte.getId(), compte.getLibelle());
				if(compte.getOrdre() <= ordreCompte){
					this.compte.select(compte.getId());
					ordreCompte = compte.getOrdre();
				}
				if(getComponent().getIdCompteSelectionne() != null && compte.getId().equals(getComponent().getIdCompteSelectionne())){
					this.compte.select(compte.getId());				
				}
				this.compte.setItemIcon(compte.getId(), new ThemeResource(compte.getItemIcon()));
			}
			this.compte.setTextInputAllowed(false);
			this.compte.addValueChangeListener(this);
		} catch (DataNotFoundException e) {
			Notification.show("Impossible de charger les comptes de "+ getUtilisateurCourant(), Notification.Type.ERROR_MESSAGE);
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

