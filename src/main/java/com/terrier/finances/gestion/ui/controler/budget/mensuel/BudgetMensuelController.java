package com.terrier.finances.gestion.ui.controler.budget.mensuel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.model.exception.CompteClosedException;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.BudgetMensuelPage;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableResumeTotauxController;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TreeResumeCategoriesController;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionDeconnexionClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionEditerDepensesClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionLockBudgetClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionRefreshMonthBudgetClickListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.ActionValiderAnnulerEditionDepenseListener;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.creation.ActionCreerDepenseClickListener;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.event.UIEvents;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * Controleur du budget mensuel
 * Le controleur est poolé régulièrement par l'IHM pour vérifier s'il faut mettre à jour le modèle du à des modifs en BDD
 * @author vzwingma
 *
 */
public class BudgetMensuelController extends AbstractUIController<BudgetMensuelPage> implements ValueChangeListener, UIEvents.PollListener, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -235154625221927340L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetMensuelController.class);


	// Table de suivi
	private TableSuiviDepenseController tableSuiviDepenseControleur;
	private TableResumeTotauxController tableTotalResumeControleur;
	private TreeResumeCategoriesController treeResumeControleur;
	private ComboBox compte;

	// Calcul de mise à jour du compte courant
	private int oldMois;
	private int oldAnnee;
	private String oldIdCompte;
	private boolean refreshAllTable = false;
	/**
	 * Constructure du Controleur du composant
	 * @param composant
	 */
	public BudgetMensuelController(BudgetMensuelPage composant) {
		super(composant);
		// Ajout du pooling listener de l'UI sur ce controleur
		UI.getCurrent().addPollListener(this);
	}

	/**
	 * Pool de l'IHM pour vérifier la mise à jour vis à vis de la BDD
	 * @param event
	 */
	@Override
	public void poll(UIEvents.PollEvent event) {

		String idSession = getIdSession();
		if(UISessionManager.get().getNombreSessionsActives() > 1){
			BudgetMensuel budgetCourant = getBudgetMensuelCourant();
			if(idSession != null &&  budgetCourant != null){
				LOGGER.debug("[REFRESH][{}] Dernière mise à jour reçue pour le budget {} : {}", idSession, 
						budgetCourant.getId(), budgetCourant.getDateMiseAJour() != null ? budgetCourant.getDateMiseAJour().getTime() : "null");

				if(getServiceDepense().isBudgetUpToDate(budgetCourant.getId(), budgetCourant.getDateMiseAJour())){
					LOGGER.info("[REFRESH][{}] Le budget a été mis à jour en base de données.  Mise à jour de l'IHM", idSession);
					miseAJourVueDonnees();
				}
				else{
					LOGGER.debug("[REFRESH][{}] Le budget est à jour par rapport à la  base de données. ", idSession);
				}
			}
		}
		else{
			LOGGER.debug("{} session active. Pas de refresh automatique en cours", UISessionManager.get().getNombreSessionsActives());
		}
	}

	/**
	 * Init du suivi
	 * @param tableSuiviDepenseControleur tableau de suivi
	 */
	@Override
	public void initDynamicComponentsOnPage(){

		// Init des boutons
		getComponent().getButtonEditer().addClickListener(new ActionEditerDepensesClickListener());
		getComponent().getButtonEditer().setDescription("Editer le tableau des opérations");
		getComponent().getButtonCreate().addClickListener(new ActionCreerDepenseClickListener());
		getComponent().getButtonCreate().setDescription("Ajouter une nouvelle opération");
		getComponent().getComboBoxComptes().setNullSelectionAllowed(false);
		getComponent().getComboBoxComptes().setImmediate(true);

		getComponent().getButtonValider().setVisible(false);
		getComponent().getButtonValider().setEnabled(false);
		getComponent().getButtonValider().addClickListener(new ActionValiderAnnulerEditionDepenseListener());
		getComponent().getButtonValider().setDescription("Valider les modifications du tableau des dépenses");
		getComponent().getButtonValider().setClickShortcut(KeyCode.ENTER);
		getComponent().getButtonAnnuler().setVisible(false);
		getComponent().getButtonAnnuler().setEnabled(false);
		getComponent().getButtonAnnuler().addClickListener(new ActionValiderAnnulerEditionDepenseListener());
		getComponent().getButtonAnnuler().setDescription("Annuler les modifications du tableau des dépenses");


		// Init des controleurs
		this.tableSuiviDepenseControleur = getComponent().getTableSuiviDepense().getControleur();
		this.treeResumeControleur = getComponent().getTreeResume().getControleur();
		this.tableTotalResumeControleur = getComponent().getTableTotalResume().getControleur();

		// Init premiere fois
		Calendar dateBudget = Calendar.getInstance();
		dateBudget.set(Calendar.DAY_OF_MONTH, 1);
		if(getComponent().getMois().getValue() == null){
			getComponent().getMois().setValue(dateBudget.getTime());
			LOGGER.debug("[INIT] Init du mois géré : {}", dateBudget.getTime());
		}
		// Label last connexion
		Date dateDernierAcces = getUtilisateurCourant().getDernierAcces();
		if(dateDernierAcces != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm", Locale.FRENCH);
			sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
			String date = sdf.format(dateDernierAcces.getTime());
			this.getComponent().getLabelLastConnected().setValue("Dernière connexion : \n" + date);
		}

		// Maj des composants MOIS/COMPTES
		getComponent().getMois().setResolution(Resolution.MONTH);
		getComponent().getMois().setImmediate(true);
		getComponent().getMois().addValueChangeListener(this);

		this.compte = getComponent().getComboBoxComptes();
		this.compte.setImmediate(true);
		this.compte.setDescription("Choix du compte");
		this.compte.setNewItemsAllowed(false);
		this.compte.setNullSelectionAllowed(false);

		int ordreCompte = 100;
		CompteBancaire compteCourant = null;

		try{
			List<CompteBancaire> comptes = getServiceParams().getComptesUtilisateur(getUtilisateurCourant());
			// Ajout de la liste des comptes dans la combobox
			for (CompteBancaire compte : comptes) {
				this.compte.addItem(compte.getId());

				if(compte.getOrdre() <= ordreCompte){
					this.compte.select(compte.getId());
					compteCourant = compte;
					ordreCompte = compte.getOrdre();
				}
				if(getComponent().getIdCompteSelectionne() != null && compte.getId().equals(getComponent().getIdCompteSelectionne())){
					this.compte.select(compte.getId());
					compteCourant = compte;
				}
				// mise à jour du style
				this.compte.setItemCaption(compte.getId(), "  " + compte.getLibelle());
				this.compte.setItemIcon(compte.getId(), new ThemeResource(compte.getItemIcon()));
			}
			this.compte.setItemStyleGenerator(new ComptesComboboxItemStyle(comptes));

			initRangeDebutFinMois(compteCourant.getId());
			this.compte.setTextInputAllowed(false);
			this.compte.addValueChangeListener(this);


			// Bouton stat
			//getComponent().getButtonStatistique().addClickListener(new ChangePageListener(StatistiquesPage.class));
			// Bouton déconnexion
			getComponent().getButtonDeconnexion().setCaption("");
			getComponent().getButtonDeconnexion().addClickListener(new ActionDeconnexionClickListener());
			getComponent().getButtonDeconnexion().setDescription("Déconnexion de l'application");
			// Bouton refresh
			getComponent().getButtonRefreshMonth().setVisible(compteCourant.isActif());
			if(getUtilisateurCourant().isEnabled(UtilisateurDroitsEnum.DROIT_RAZ_BUDGET) && compteCourant.isActif() ){
				getComponent().getButtonRefreshMonth().addClickListener(new ActionRefreshMonthBudgetClickListener());
			}
			else{
				getComponent().getButtonRefreshMonth().setEnabled(false);
			}
			// Bouton lock
			getComponent().getButtonLock().setVisible(compteCourant.isActif());
			if(getUtilisateurCourant().isEnabled(UtilisateurDroitsEnum.DROIT_CLOTURE_BUDGET)){
				getComponent().getButtonLock().setCaption("");
				getComponent().getButtonLock().addClickListener(new ActionLockBudgetClickListener());
			}
			else{
				getComponent().getButtonLock().setEnabled(false);
			}

		}catch (DataNotFoundException e) {
			Notification.show("Impossible de charger le budget du compte " + (compteCourant != null ? compteCourant.getLibelle() : "" ) + " du " + dateBudget.get(Calendar.MONTH)+"/"+ dateBudget.get(Calendar.YEAR), Notification.Type.ERROR_MESSAGE);
		}


	}


	/**
	 * Déconnexion de l'utilisateur
	 */
	public void deconnexion(){
		UISessionManager.get().deconnexion();
	}


	/**
	 * Finalisation du budget
	 */
	public void lockBudget(boolean setBudgetActif){
		LOGGER.info("[IHM] {} du budget mensuel", setBudgetActif ? "Ouverture" : "Clôture");
		getServiceDepense().setBudgetActif(getBudgetMensuelCourant(), setBudgetActif);
		miseAJourVueDonnees();
	}

	/**
	 * Réinitialiser le budhet Mensuel Courant
	 */
	public void reinitialiserBudgetCourant() {
		LOGGER.info("Réinitialisation du budget mensuel courant");
		try {
			getServiceDepense().reinitialiserBudgetMensuel(
					getBudgetMensuelCourant(), 
					getUtilisateurCourant());
			// Ack pour forcer le "refreshAllTable"
			oldMois = -1;
			miseAJourVueDonnees();
		} catch (BudgetNotFoundException | DataNotFoundException | CompteClosedException e) {
			LOGGER.error("[BUDGET] Erreur lors de la réinitialisation du compte", e);
			Notification.show("Impossible de réinitialiser le mois courant "+
					( getBudgetMensuelCourant().getMois() + 1)+"/"+ getBudgetMensuelCourant().getAnnee()
					+" du compte "+ getBudgetMensuelCourant().getCompteBancaire().getLibelle(), 
					Notification.Type.ERROR_MESSAGE);
		}
	}




	/**
	 * Sette la table en mode édition
	 */
	public void setTableOnEditableMode(boolean editableMode){
		// Activation du tableau
		getComponent().getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.TYPE.getId(), !editableMode);
		getComponent().getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), !editableMode);
		// Inversion du champ Libelle
		getComponent().getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE.getId(), !editableMode);
		getComponent().getTableSuiviDepense().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId(), editableMode);
		getComponent().getTableSuiviDepense().setEditable(editableMode);
		getComponent().getTableSuiviDepense().setColumnWidth(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId(), editableMode ? TableSuiviDepenseController.TAILLE_COLONNE_DATE_EDITEE : TableSuiviDepenseController.TAILLE_COLONNE_DATE);


		getComponent().getButtonValider().setVisible(editableMode);
		getComponent().getButtonValider().setEnabled(editableMode);
		getComponent().getButtonAnnuler().setVisible(editableMode);
		getComponent().getButtonAnnuler().setEnabled(editableMode);
		getComponent().getButtonEditer().setVisible(!editableMode);
		getComponent().getButtonEditer().setEnabled(!editableMode);
		getComponent().getButtonCreate().setVisible(!editableMode);
		getComponent().getButtonCreate().setEnabled(!editableMode);		
	}


	/**
	 * Modif du compte ou bien de la date
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void valueChange(ValueChangeEvent event) {
		// Modification de la date
		boolean miseAJour = false;
		String idCompte = (String)this.compte.getConvertedValue();
		if(event.getProperty().getType().equals(Date.class)){
			Calendar d = Calendar.getInstance();
			d.setTime((Date)event.getProperty().getValue());
			d.set(Calendar.DAY_OF_MONTH, 1);
			setRangeFinMois(d, idCompte);
			if(d.get(Calendar.MONTH) != this.oldMois || d.get(Calendar.YEAR) != this.oldAnnee){
				miseAJourVueDonnees();			
			}
		}
		else{
			// Modification du compte
			miseAJour = true;
			initRangeDebutFinMois(idCompte);
		}
		// Si oui : refresh
		if(miseAJour){
			miseAJourVueDonnees();
		}
	}





	/**
	 * Mise à jour du range fin
	 * @param dateFin
	 */
	private void initRangeDebutFinMois(String idCompte){
		if(idCompte != null){
			// Bouton Mois précédent limité au mois du
			// Premier budget du compte de cet utilisateur
			try {
				Calendar[] datePremierDernierBudgets = getServiceDepense().getDatePremierDernierBudgets(idCompte);
				getComponent().getMois().setRangeStart(datePremierDernierBudgets[0].getTime());
				getComponent().getMois().setRangeEnd(datePremierDernierBudgets[1].getTime());
			} catch (DataNotFoundException e) {
				LOGGER.error("[IHM] Erreur lors du chargement du premier budget");
			}

		}
		LOGGER.debug("[IHM] > Affichage limité à > [{}/{}]", getComponent().getMois().getRangeStart(), getComponent().getMois().getRangeEnd());

	}
	/**
	 * Mise à jour du range fin
	 * @param dateFin
	 */
	private void setRangeFinMois(Calendar dateFin, String idCompte){
		if(dateFin != null){
			// Bouton Mois suivant limité au mois prochain si le compte n'est pas clos
			Calendar dateRangeBudget = Calendar.getInstance();
			dateRangeBudget.setTime(dateFin.getTime());
			if(getServiceDepense().isCompteActif(idCompte)){
				dateRangeBudget.add(Calendar.MONTH, 1);
			}
			if(dateRangeBudget.getTime().after(getComponent().getMois().getRangeEnd())){
				getComponent().getMois().setRangeEnd(dateRangeBudget.getTime());
			}
		}
		LOGGER.debug("[IHM] < Affichage limité à [{}/{}] <", getComponent().getMois().getRangeStart(), getComponent().getMois().getRangeEnd());

	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.AbstractUIController#chargeDonnees()
	 */
	private BudgetMensuel chargeDonnees() throws DataNotFoundException {
		LOGGER.debug("[BUDGET] Chargement du budget pour le tableau de suivi des dépenses");

		String idCompte = (String)this.compte.getConvertedValue();
		Date dateMois = (Date)getComponent().getMois().getConvertedValue();

		Calendar c = Calendar.getInstance();
		if(dateMois != null){
			c.setTime(dateMois);
		}

		LOGGER.debug("[BUDGET] Gestion du Compte : {} du mois {}",idCompte, c.getTime());

		try {
			// Budget
			BudgetMensuel budget = getServiceDepense().chargerBudgetMensuel(
					getUtilisateurCourant(),
					idCompte,
					c.get(Calendar.MONTH), 
					c.get(Calendar.YEAR));

			// Maj du budget
			getUISession().setBudgetMensuelCourant(budget);
			// Mise à jour du mois suivant le résultat de la requête
			// (gère les comptes cloturés, on récupère que le dernier accessible)
			if(budget.getMois() == oldMois && idCompte.equals(oldIdCompte)){
				refreshAllTable = false;
				LOGGER.info("[BUDGET] Pas de changement de mois ou de compte : Pas de refresh total des tableaux");
			}
			else{
				LOGGER.debug("[BUDGET] Changement de mois ou de compte : Refresh total des tableaux");
				refreshAllTable = true;
			}
			oldMois = budget.getMois();
			oldAnnee = budget.getAnnee();
			oldIdCompte = idCompte;
			return budget;
		} catch (BudgetNotFoundException e) {
			String messageerreur = new StringBuilder().
					append("Impossible de charger le budget du compte ").
					append(idCompte).append(" du ").
					append(c.get(Calendar.MONTH)).append("/").append(c.get(Calendar.YEAR)).toString();
			throw new DataNotFoundException(messageerreur);
		}
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {
		BudgetMensuel budgetCourant = null;
		try {
			budgetCourant = chargeDonnees();
		} catch (final DataNotFoundException e) {
			Calendar c = Calendar.getInstance();
			c.setTime(getComponent().getMois().getValue());
			c.set(Calendar.MONTH, this.oldMois);
			c.set(Calendar.YEAR, this.oldAnnee);
			LOGGER.warn("[BUDGET] Budget non trouvé. Réinjection de {}", c.getTime());
			getComponent().getMois().setValue(c.getTime());
			Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			return;
		}
		LOGGER.debug("[IHM] >> Mise à jour des vues >> {}", budgetCourant.isActif());		
		LOGGER.debug("[IHM] Affichage des données dans le tableau de suivi des dépenses");
		List<LigneDepense> listeDepenses = budgetCourant.getListeDepenses();		
		/**
		 * Affichage des lignes dans le tableau
		 **/

		tableSuiviDepenseControleur.miseAJourVueDonnees(this.refreshAllTable, budgetCourant.isActif(), listeDepenses);

		tableSuiviDepenseControleur.getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.AUTEUR.getId(), budgetCourant.isActif());
		tableSuiviDepenseControleur.getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.ACTIONS.getId(), !budgetCourant.isActif());
		tableSuiviDepenseControleur.getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE.getId(), budgetCourant.isActif());
		tableSuiviDepenseControleur.getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId(), !budgetCourant.isActif());

		// Maj du mois
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, budgetCourant.getMois());
		c.set(Calendar.YEAR, budgetCourant.getAnnee());
		getComponent().getMois().setValue(c.getTime());

		// Boutons actions sur Budget inactif :
		if(!budgetCourant.isActif()){
			getComponent().getButtonAnnuler().setVisible(false);
			getComponent().getButtonValider().setVisible(false);
			getComponent().getButtonEditer().setVisible(false);
			getComponent().getButtonCreate().setVisible(false);
			getComponent().getButtonRefreshMonth().setVisible(false);
			getComponent().getButtonLock().setVisible(budgetCourant.getCompteBancaire().isActif());
			getComponent().getButtonLock().setDescription("Réouvrir le budget mensuel");
			getComponent().getButtonLock().setStyleName("locked");
		}
		// Boutons actions sur Budget actif :
		else{
			getComponent().getButtonCreate().setVisible(budgetCourant.getCompteBancaire().isActif());
			getComponent().getButtonEditer().setVisible(budgetCourant.getCompteBancaire().isActif());
			getComponent().getButtonRefreshMonth().setVisible(budgetCourant.getCompteBancaire().isActif());
			getComponent().getButtonLock().setVisible(budgetCourant.getCompteBancaire().isActif());
			getComponent().getButtonLock().setDescription("Finaliser le budget mensuel");
			getComponent().getButtonLock().setStyleName("unlocked");
		}

		/** 
		 * Affichage par catégorie
		 */
		LOGGER.debug("[IHM] Total par categorie");
		treeResumeControleur.miseAJourVueDonnees(budgetCourant, getMaxDateListeDepenses(listeDepenses, budgetCourant.getMois(), budgetCourant.getAnnee()));
		/**
		 * Affichage du résumé
		 */
		tableTotalResumeControleur.miseAJourVueDonnees(budgetCourant, getMaxDateListeDepenses(listeDepenses, budgetCourant.getMois(), budgetCourant.getAnnee()));

		LOGGER.debug("[IHM] << Mise à jour des vues <<");
		this.refreshAllTable = false;
	}


	/**
	 * @param listeDepenses
	 * @return date max d'une liste de dépenses
	 */
	private Calendar getMaxDateListeDepenses(List<LigneDepense> listeDepenses, int moisBudgetCourant, int anneeBudgetCourant){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, anneeBudgetCourant);
		c.set(Calendar.MONTH, moisBudgetCourant);
		c.set(Calendar.DAY_OF_MONTH, 1);
		if(listeDepenses != null){
			for (LigneDepense ligneDepense : listeDepenses) {
				if(ligneDepense.getDateOperation() != null && c.getTime().before(ligneDepense.getDateOperation())){
					c.setTime(ligneDepense.getDateOperation());
				}
			}
		}
		return c;
	}
}

