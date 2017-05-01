package com.terrier.finances.gestion.ui.components.budget.mensuel.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.abstrait.AbstractUITableComponent;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.components.TableSuiviDepenseController;
import com.vaadin.v7.data.Property;

/**
 * Tableau de suivi des dépenses
 * @author vzwingma
 *
 */
public class TableSuiviDepense extends AbstractUITableComponent<TableSuiviDepenseController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187184070043964584L;


	private final SimpleDateFormat DATE_FORMAT_MAJ = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
	private final SimpleDateFormat DATE_FORMAT_OPERATION = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
	private final TimeZone tzParis = TimeZone.getTimeZone("Europe/Paris");

	/**
	 * Logger
	 */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TableSuiviDepense.class);
	/**
	 * Constructure : démarrage du controleur
	 */
	public TableSuiviDepense(){
		// Start controleur
		startControleur();
		DATE_FORMAT_MAJ.setTimeZone(tzParis);
		DATE_FORMAT_OPERATION.setTimeZone(tzParis);
	}

	/**
	 * Affichage dans le tableau en mode consultation
	 * @see com.vaadin.ui.Table#formatPropertyValue(java.lang.Object, java.lang.Object, com.vaadin.data.Property)
	 */
	@Override
	protected String formatPropertyValue(Object rowId, Object colId,
			Property<?> property) {

		String colonneId = (String)colId;
		if(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId().equals(colonneId)){
			Date date = ((Date)property.getValue());

			return date == null ? "" : DATE_FORMAT_MAJ.format(date);
		}
		else if(EntetesTableSuiviDepenseEnum.DATE_OPERATION.getId().equals(colonneId)){
			Date date = ((Date)property.getValue());
			return date == null ? "" : DATE_FORMAT_OPERATION.format(date);
		}		
		else if(EntetesTableSuiviDepenseEnum.VALEUR.getId().equals(colonneId)){
			TypeDepenseEnum typeOperation = null;
			boolean depenseNulle = false;
			if(getBudgetMensuelCourant().getListeDepenses() != null){
				for (LigneDepense depense : getBudgetMensuelCourant().getListeDepenses()) {
					if(depense.getId().equals((String)rowId)){
						typeOperation = depense.getTypeDepense();
						if(depense.getValeur() == 0){
							depenseNulle = true;
						}
					}
				}
			}
			StringBuffer valeur = new StringBuffer();
			if(typeOperation == null){
				valeur.append("? ");
			}
			else if(typeOperation.equals(TypeDepenseEnum.DEPENSE) && !depenseNulle){
				valeur.append("- ");
			}
			else{
				valeur.append("+ ");
			}

			valeur.append(String.format("%.2f", (Float)property.getValue()));
			valeur.append(" €");
			return valeur.toString();
		}
		else if(EntetesTableSuiviDepenseEnum.CATEGORIE.getId().equals(colonneId) || EntetesTableSuiviDepenseEnum.SSCATEGORIE.getId().equals(colonneId)){
			CategorieDepense categorieId = ((CategorieDepense)property.getValue());
			return categorieId != null ? categorieId.getLibelle() :"?";
		}		
		else if(EntetesTableSuiviDepenseEnum.TYPE.getId().equals(colonneId)){
			TypeDepenseEnum type =  ((TypeDepenseEnum)property.getValue());
			return type != null ? type.getLibelle() : "?";
		}
		else if(EntetesTableSuiviDepenseEnum.PERIODIQUE.getId().equals(colonneId)){
			Boolean mensuel = ((Boolean)property.getValue());
			if(mensuel == null){
				mensuel = false;
			}
			return mensuel ? "Oui" : "Non";
		}		
		return super.formatPropertyValue(rowId, colId, property);
	}


	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.components.AbstractUITableComponent#getControleur()
	 */
	@Override
	public TableSuiviDepenseController createControleur() {
		return new TableSuiviDepenseController(this);
	}
}
