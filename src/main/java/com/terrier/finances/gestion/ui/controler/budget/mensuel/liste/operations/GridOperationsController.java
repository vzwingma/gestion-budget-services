/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations;

import java.util.Date;
import java.util.List;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridOperations;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.ui.Grid.Column;

/**
 * Grille des opérations du budget
 * @author vzwingma
 *
 */
public class GridOperationsController extends AbstractUIController<GridOperations>{


	private static final long serialVersionUID = 5190668755144306669L;


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
	public GridOperationsController(GridOperations composant) {
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
		getComponent().setWidth("100.0%");
		getComponent().setHeight("100.0%");
		getComponent().setColumnReorderingAllowed(false);
		getComponent().setResponsive(true);
//		getComponent().setColumnCollapsingAllowed(true);

		Column<LigneDepense, Date> c = getComponent().addColumn(LigneDepense::getDateOperation);
		c.setId(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId()).setCaption(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getLibelle()).setWidth(TAILLE_COLONNE_DATE).setHidable(true).setResizable(false);
		
		Column<LigneDepense, CategorieDepense> c2 = getComponent().addColumn(LigneDepense::getCategorie);
		c2.setId(EntetesTableSuiviDepenseEnum.CATEGORIE.getId()).setCaption(EntetesTableSuiviDepenseEnum.CATEGORIE.getLibelle()).setWidth(TAILLE_COLONNE_CATEGORIE).setHidable(true).setResizable(false);
		
		Column<LigneDepense, CategorieDepense> c3 = getComponent().addColumn(LigneDepense::getSsCategorie);
		c3.setId(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId()).setCaption(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getLibelle()).setWidth(TAILLE_COLONNE_CATEGORIE).setHidable(true).setResizable(false);
		
		Column<LigneDepense, String> c4 = getComponent().addColumn(LigneDepense::getLibelle);
		c4.setId(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId()).setCaption(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getLibelle()).setHidable(true).setResizable(false);
		
		Column<LigneDepense, String> c5 = getComponent().addColumn(LigneDepense::getLibelle);
		c5.setId(EntetesTableSuiviDepenseEnum.LIBELLE.getId()).setCaption(EntetesTableSuiviDepenseEnum.LIBELLE.getLibelle()).setHidden(true).setHidable(true).setResizable(false);
		
		Column<LigneDepense, TypeDepenseEnum> c6 = getComponent().addColumn(LigneDepense::getTypeDepense);
		c6.setId(EntetesTableSuiviDepenseEnum.TYPE.getId()).setCaption(EntetesTableSuiviDepenseEnum.TYPE.getLibelle()).setWidth(TAILLE_COLONNE_TYPE_MENSUEL).setHidden(true).setHidable(true).setResizable(false);
		
		Column<LigneDepense, Float> c7 = getComponent().addColumn(LigneDepense::getValeur);
		c7.setId(EntetesTableSuiviDepenseEnum.VALEUR.getId()).setCaption(EntetesTableSuiviDepenseEnum.VALEUR.getLibelle()).setWidth(TAILLE_COLONNE_VALEUR).setHidable(true).setResizable(false);
		
//		c7.setColumnAlignment(EntetesTableSuiviDepenseEnum.VALEUR.getId(), Align.RIGHT);
		
		Column<LigneDepense, Boolean> c8 = getComponent().addColumn(LigneDepense::isPeriodique);
		c8.setId(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId()).setCaption(EntetesTableSuiviDepenseEnum.PERIODIQUE.getLibelle()).setWidth(TAILLE_COLONNE_TYPE_MENSUEL).setHidden(true).setHidable(true).setResizable(false);
		
		Column<LigneDepense, String> c9 = getComponent().addColumn(LigneDepense::getId);
		c9.setId(EntetesTableSuiviDepenseEnum.ACTIONS.getId()).setCaption(EntetesTableSuiviDepenseEnum.ACTIONS.getLibelle()).setWidth(TAILLE_COLONNE_ACTIONS).setHidable(true).setResizable(false);
		
		Column<LigneDepense, Date> c10 = getComponent().addColumn(LigneDepense::getDateMaj);
		c10.setId(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId()).setCaption(EntetesTableSuiviDepenseEnum.DATE_MAJ.getLibelle()).setWidth(TAILLE_COLONNE_DATE + 10).setHidable(true).setResizable(false);
		
		Column<LigneDepense, String> c11 = getComponent().addColumn(LigneDepense::getAuteur);
		c11.setId(EntetesTableSuiviDepenseEnum.AUTEUR.getId()).setCaption(EntetesTableSuiviDepenseEnum.AUTEUR.getLibelle()).setWidth(TAILLE_COLONNE_AUTEUR).setHidden(true).setHidable(true).setResizable(false);
		
		// Ajout du mode Dernier ligne sur la liste des dépenses
		TableSuiviDepensesActionMenuHandler handler = new TableSuiviDepensesActionMenuHandler();
//		getComponent().addActionHandler(handler);
		getComponent().addItemClickListener(handler);
	}

	/**
	 * Mise à jour de la vue
	 * @see com.terrier.finances.gestion.ui.controler.common.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {
		/*
		try {
	
			// Table Factory pour le mode édition
			getComponent().setTableFieldFactory(new TableSuiviDepenseEditedFieldFactory(getServiceParams().getCategories()));
			// Style
			getComponent().setCellStyleGenerator(new TableDepensesCellStyle(this));
			// Tooltip
			getComponent().setItemDescriptionGenerator(new TableDepensesDescriptionGenerator(this));

		} catch (DataNotFoundException e) {
			Notification.show("Erreur grave : Impossible de charger les données", Notification.Type.ERROR_MESSAGE);
			return;
		}
*/
	}



	/**
	 * Mise à jour de la vue suite aux données
	 * @param refreshAllTable : flag s'il faut tout effacer avant l'affichage
	 * @param budgetIsActif budget actif ?
	 * @param listeDepenses liste des dépenses à utiliser
	 */
	public void miseAJourVueDonnees(boolean refreshAllDonnees, boolean budgetIsActif, List<LigneDepense> listeDepenses){
/*
		if(refreshAllDonnees){
			getComponent().removeAllItems();
			getComponent().refreshRowCache();
		}
		*/
		// Ajout des opérations
		getComponent().setItems(listeDepenses);

/*
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

			// Type Popup view pour avoir les notes
			Property<PopupView> property5 = item1.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE_VIEW.getId());

			final RichTextArea rta = new RichTextArea();
			rta.setValue(ligneDepense.getNotes() != null ? ligneDepense.getNotes() : "");
			String noteStar = ligneDepense.getNotes() != null ? "  *" : "";
			final PopupView ppv = new PopupView(ligneDepense.getLibelle() + noteStar, rta);
			ppv.addPopupVisibilityListener(new PopupNoteVisibitilityListener(ligneDepense.getId(), this));
			ppv.setHideOnMouseOut(false);
			property5.setValue(ppv);

			// Type String
			Property<String> property5b = item1.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE.getId());
			if(budgetIsActif){
				property5b.setValue(ligneDepense.getLibelle());	
			}
			else{
				property5b.setValue(ligneDepense.getLibelle() + noteStar);
			}


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
		*/
	}


	/**
	 * Validation du formulaire
	 */
	public boolean validateEditableForm(){
/*
		List<String> messagesErreurs = new ArrayList<String>();
		String idLigneEditable = ((TableSuiviDepenseEditedFieldFactory)getComponent().getTableFieldFactory()).getIdLigneEditable();
		if(getComponent().isEditable() && idLigneEditable != null){
			for (Object itemID : getComponent().getItemIds()) {
				Item itemLigneDepense = getComponent().getItem(itemID);
				
				String description = itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.LIBELLE.getId()).getValue().toString();
				
				if(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.CATEGORIE.getId()).getValue() == null){
					messagesErreurs.add(description + " : La catégorie est obligatoire");
				}
				if(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId()).getValue() == null){
					messagesErreurs.add(description + " : La sous-catégorie est obligatoire");
				}
				if(description == null || description.isEmpty()){
					messagesErreurs.add(description + " : La description est obligatoire");
				}
				ValeurDepenseValidator validator = new ValeurDepenseValidator("La valeur est incorrecte");
				try{
					validator.validate(itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.VALEUR.getId()).getValue());
				}
				catch(InvalidValueException e){
					messagesErreurs.add(description + " : La valeur est obligatoire");
				}
				// Vérification de la cohérence des données :
				CategorieDepense ssCategorie = (CategorieDepense)itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId()).getValue();
				TypeDepenseEnum type = (TypeDepenseEnum)itemLigneDepense.getItemProperty(EntetesTableSuiviDepenseEnum.TYPE.getId()).getValue();

				TypeDepenseValidator typeValidator = new TypeDepenseValidator(ssCategorie);
				try{
					typeValidator.validate(type);
				}
				catch(InvalidValueException e){
					messagesErreurs.add(description + " : " + e.getMessage());
				}
			}
		}

		StringBuilder b = new StringBuilder();
		for (String string : messagesErreurs) {
			b.append(string).append("\n");
		}
		if(!messagesErreurs.isEmpty()){
			Notification.show(b.toString(), Notification.Type.WARNING_MESSAGE);
		}
		return messagesErreurs.isEmpty();
		*/
		return true;
	}
}
