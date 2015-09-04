/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.components;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableSuiviDepense;
import com.terrier.finances.gestion.ui.components.budget.mensuel.converter.edition.mode.TableSuiviDepenseEditedFieldFactory;
import com.terrier.finances.gestion.ui.components.style.TableDepensesCellStyle;
import com.terrier.finances.gestion.ui.components.style.TableDepensesDescriptionGenerator;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.listener.budget.mensuel.PopupNoteVisibitilityListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;

/**
 * @author vzwingma
 *
 */
public class TableSuiviDepenseController extends AbstractUIController<TableSuiviDepense>{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5190668755144306669L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TableSuiviDepenseController.class);
	
	public static final int TAILLE_COLONNE_DATE = 95;
	public static final int TAILLE_COLONNE_CATEGORIE = 150;
	public static final int TAILLE_COLONNE_AUTEUR = 100;
	public static final int TAILLE_COLONNE_DATE_EDITEE = 150;
	public static final int TAILLE_COLONNE_ACTIONS = 110;
	public static final int TAILLE_COLONNE_TYPE_MENSUEL = 65;
	public static final int TAILLE_COLONNE_VALEUR = 100;

	
	/**
	 * Constructeur du controleur du composant
	 * @param composant
	 */
	public TableSuiviDepenseController(TableSuiviDepense composant) {
		super(composant);
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#initDynamicComponentsOnPage()
	 */
	@Override
	public void initDynamicComponentsOnPage() {
		/**
		 * Table de suivi des dépenses
		 */
		getComponent().setImmediate(true);
		getComponent().setWidth("100.0%");
		getComponent().setHeight("100.0%");
		getComponent().setColumnCollapsingAllowed(true);
		getComponent().setSortEnabled(true);
		// Ajout des propriétés : ATTENTION les ID doivent correspondrent aux attributs de l'objet mappé
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId(), Date.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId(), EntetesTableSuiviDepenseEnum.DATE_OPERATION.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId(), TAILLE_COLONNE_DATE);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.CATEGORIE.getId(), CategorieDepense.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.CATEGORIE.getId(), EntetesTableSuiviDepenseEnum.CATEGORIE.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.CATEGORIE.getId(), TAILLE_COLONNE_CATEGORIE);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId(), CategorieDepense.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId(), EntetesTableSuiviDepenseEnum.SSCATEGORIE.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId(), TAILLE_COLONNE_CATEGORIE);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.LIBELLE.getId(), PopupView.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.LIBELLE.getId(), EntetesTableSuiviDepenseEnum.LIBELLE.getLibelle());
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.TYPE.getId(), TypeDepenseEnum.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.TYPE.getId(), EntetesTableSuiviDepenseEnum.TYPE.getLibelle());
		getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.TYPE.getId(), true);
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.TYPE.getId(), TAILLE_COLONNE_TYPE_MENSUEL);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.VALEUR.getId(), Float.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.VALEUR.getId(), EntetesTableSuiviDepenseEnum.VALEUR.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.VALEUR.getId(), TAILLE_COLONNE_VALEUR);
		getComponent().setColumnAlignment(EntetesTableSuiviDepenseEnum.VALEUR.getId(), Align.RIGHT);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), Boolean.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), EntetesTableSuiviDepenseEnum.PERIODIQUE.getLibelle());
		getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), true);
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId(), TAILLE_COLONNE_TYPE_MENSUEL);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.ACTIONS.getId(), ActionsLigneBudget.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.ACTIONS.getId(), EntetesTableSuiviDepenseEnum.ACTIONS.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.ACTIONS.getId(), TAILLE_COLONNE_ACTIONS);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId(), Date.class, Calendar.getInstance().getTime());
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId(), EntetesTableSuiviDepenseEnum.DATE_MAJ.getLibelle());
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId(), TAILLE_COLONNE_DATE + 10);
		
		getComponent().addContainerProperty(EntetesTableSuiviDepenseEnum.AUTEUR.getId(), String.class, null);
		getComponent().setColumnHeader(EntetesTableSuiviDepenseEnum.AUTEUR.getId(), EntetesTableSuiviDepenseEnum.AUTEUR.getLibelle());
		getComponent().setColumnCollapsed(EntetesTableSuiviDepenseEnum.AUTEUR.getId(), true);
		getComponent().setColumnWidth(EntetesTableSuiviDepenseEnum.AUTEUR.getId(), TAILLE_COLONNE_AUTEUR);
		
		// Ajout du mode Dernier ligne sur la liste des dépenses
		TableSuiviDepensesActionMenuHandler handler = new TableSuiviDepensesActionMenuHandler();
		getComponent().addActionHandler(handler);
		getComponent().addItemClickListener(handler);
	}

	/**
	 * Mise à jour de la vue
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {

		// Table Factory pour le mode édition
		try {
			getComponent().setTableFieldFactory(new TableSuiviDepenseEditedFieldFactory(getServiceParams().getCategories()));
			// Style
			getComponent().setCellStyleGenerator(new TableDepensesCellStyle(this));
			// Tooltip
			getComponent().setItemDescriptionGenerator(new TableDepensesDescriptionGenerator(this));
		
		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}

	}
	

	/**
	 * Effacement des données
	 */
	public void resetVueDonnees(){
		LOGGER.info("resetVueDonnees");
		getComponent().setImmediate(false);
		LOGGER.info("> removeAllItems");
		getComponent().removeAllItems();
		LOGGER.info(">>" + getComponent().getItemIds().size());
		getComponent().markAsDirty();
		LOGGER.info("> markAsDirty");
		getComponent().markAsDirtyRecursive();
		LOGGER.info("> markAsDirtyRecursive");
		getComponent().refreshRowCache();
		LOGGER.info("> refreshRowCache");
		getComponent().commit();
		LOGGER.info("> commit");
		getComponent().setImmediate(true);
		
	}
	
	
	/**
	 * Mise à jour de la vue suite aux données
	 * @param refreshAllTable : flag s'il faut tout effacer avant l'affichage
	 * @param budgetIsActif budget actif ?
	 * @param listeDepenses liste des dépenses à utiliser
	 */
	@SuppressWarnings("unchecked")
	public void miseAJourVueDonnees(boolean refreshAllDonnees, boolean budgetIsActif, List<LigneDepense> listeDepenses){
		
		if(refreshAllDonnees){
			getComponent().removeAllItems();
			getComponent().refreshRowCache();
		}
		
		for (final LigneDepense ligneDepense : listeDepenses) {
			// Access items and properties through the component
			Item item1 = getComponent().getItem(ligneDepense.getId()); // Get item by explicit ID
			if(item1 == null){
				item1 = getComponent().addItem(ligneDepense.getId()); // Create item by explicit ID
			}
				
			Property<Date> property2 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId());
			property2.setValue(ligneDepense.getDateOperation());
			Property<CategorieDepense> property3 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.CATEGORIE.getId());
			property3.setValue(ligneDepense.getCategorie());
			Property<CategorieDepense> property4 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId());
			property4.setValue(ligneDepense.getSsCategorie());
			Property<PopupView> property5 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE.getId());
			
			final RichTextArea rta = new RichTextArea();
			rta.setImmediate(true);
			rta.setValue(ligneDepense.getNotes() != null ? ligneDepense.getNotes() : "");
			String noteStar = ligneDepense.getNotes() != null ? "  *" : "";
			final PopupView ppv = new PopupView(ligneDepense.getLibelle() + noteStar, rta);
			ppv.addPopupVisibilityListener(new PopupNoteVisibitilityListener(ligneDepense.getId(), getServiceDepense()));
			ppv.setHideOnMouseOut(false);
			property5.setValue(ppv);

			Property<TypeDepenseEnum> property6 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.TYPE.getId());
			property6.setValue(ligneDepense.getTypeDepense());			
			Property<Float> property7 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.VALEUR.getId());
			property7.setValue(ligneDepense.getValeur());
			Property<Boolean> property8 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId());
			property8.setValue(ligneDepense.isPeriodique());
			Property<Date> property9 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId());
			property9.setValue(ligneDepense.getDateMaj());
			Property<String> property10 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.AUTEUR.getId());
			property10.setValue(ligneDepense.getAuteur());
			
			// Pas d'action pour les réserves
			if(!BusinessDepensesService.ID_SS_CAT_RESERVE.equals(ligneDepense.getSsCategorie().getId())
					&& !BusinessDepensesService.ID_SS_CAT_PREVISION_SANTE.equals(ligneDepense.getSsCategorie().getId())
					&& budgetIsActif){
				Property<ActionsLigneBudget> property11 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.ACTIONS.getId());
				ActionsLigneBudget actions = new ActionsLigneBudget();
				actions.getControleur().setidDepense(ligneDepense.getId());
				actions.getControleur().miseAJourEtatLigne(ligneDepense.getEtat());
				property11.setValue(actions);
			}
		}
		getComponent().refreshRowCache();
	}
}
