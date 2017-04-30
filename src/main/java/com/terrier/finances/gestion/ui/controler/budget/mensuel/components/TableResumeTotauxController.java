/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableResumeTotaux;
import com.terrier.finances.gestion.ui.components.style.TableTotalCellStyle;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table.Align;

/**
 * Controleur du tableau des résumés
 * @author vzwingma
 *
 */
public class TableResumeTotauxController extends AbstractUIController<TableResumeTotaux>{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5190668755144306669L;
	
	protected final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
	protected final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy", Locale.FRENCH);

	
	/**
	 * @param composant
	 */
	public TableResumeTotauxController(TableResumeTotaux composant) {
		super(composant);
	}

	@Override
	public void initDynamicComponentsOnPage() {
		/**
		 * Total resume
		 */
		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.CATEGORIE.getId(), String.class, null);
		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Double.class, null);
		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Double.class, null);
		getComponent().setColumnHeader(EntetesTreeResumeDepenseEnum.CATEGORIE.getId(), EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle());
		getComponent().setImmediate(true);

	}

	@Override
	public void miseAJourVueDonnees() { 
		// Style
		getComponent().setCellStyleGenerator(new TableTotalCellStyle());
	}
	
	/**
	 * Effacement des données
	 */
	public void resetVueDonnees(){
		getComponent().removeAllItems();
		getComponent().refreshRowCache();
	}
	
	/**
	 * Mise à jour des données suite au budget
	 * @param refreshAllTable refresh total en cas de changemetn de mois ou de compte
	 * @param budget budget à jour
	 * @param dateBudget date du budget
	 */
	@SuppressWarnings("unchecked")
	public void miseAJourVueDonnees(BudgetMensuel budget, Calendar dateBudget){
		
		if(dateBudget == null){
			dateBudget = Calendar.getInstance();
		}
		
		getComponent().setColumnHeader(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), 
				EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle()+ auDateFormat.format(dateBudget.getTime()));
		getComponent().setColumnHeader(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), 
				EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle() + finDateFormat.format(dateBudget.getTime()));
		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Align.RIGHT);
		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Align.RIGHT);
		
		
		getComponent().addItem("avance"); // Create item by explicit ID
		Item item1 = getComponent().getItem("avance");
		Property<String> property1 = item1.getItemProperty(EntetesTreeResumeDepenseEnum.CATEGORIE.getId());
		if(item1.getItemPropertyIds() != null && item1.getItemPropertyIds().size() >= 0 && property1.getType() != null){		
			property1.setValue("Argent avancé");
			Property<Double> property2 = item1.getItemProperty(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId());
			property2.setValue(budget.getNowArgentAvance());
			Property<Double> property3 = item1.getItemProperty(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId());
			property3.setValue(budget.getFinArgentAvance());

			getComponent().addItem("reel"); // Create item by explicit ID
			Item item2 = getComponent().getItem("reel");
			Property<String> property4 = item2.getItemProperty(EntetesTreeResumeDepenseEnum.CATEGORIE.getId());
			property4.setValue("Solde réel du compte");
			Property<Double> property5 = item2.getItemProperty(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId());
			property5.setValue(budget.getNowCompteReel());
			Property<Double> property6 = item2.getItemProperty(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId());
			property6.setValue(budget.getFinCompteReel());
		}
		
		getComponent().setDescription("Marge de sécurité : "+budget.getMargeSecurite()+" € <br> Marge à fin de mois : " + budget.getMargeSecuriteFinMois() + " €");
	}

}
