/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TableResumeTotaux;
import com.terrier.finances.gestion.ui.components.style.TableTotalCellStyle;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * Controleur du tableau des résumés
 * @author vzwingma
 *
 */
public class TableResumeTotauxController extends AbstractUIController<TableResumeTotaux>{


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
		
		getComponent().setSelectionMode(SelectionMode.NONE);
		/**
		 * Total resume
		 */
		Column<TotalBudgetMensuel, String> c = getComponent().addColumn(TotalBudgetMensuel::getTypeTotal).setCaption(EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle());
		c.setId(EntetesTreeResumeDepenseEnum.CATEGORIE.getId());
		c.setSortable(false);
		c.setResizable(false);
		c.setHidable(false);
		//c.setStyleGenerator(new TableTotalCellStyle(EntetesTreeResumeDepenseEnum.CATEGORIE));

		Column<TotalBudgetMensuel, Double> c2 = getComponent().addColumn(TotalBudgetMensuel::getTotalADate);
		c2.setId(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId());
		c2.setSortable(false);
		c2.setResizable(false);
		c2.setHidable(false);
		TableTotalCellStyle c2style = new TableTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_NOW);
		c2.setStyleGenerator(c2style);
		c2.setRenderer(c2style);
		
		Column<TotalBudgetMensuel, Double> c3 = getComponent().addColumn(TotalBudgetMensuel::getTotalFinMois);
		c3.setId(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId());
		c3.setSortable(false);
		c3.setResizable(false);
		c3.setHidable(false);
		TableTotalCellStyle c3style = new TableTotalCellStyle(EntetesTreeResumeDepenseEnum.VALEUR_FIN);
		c3.setStyleGenerator(c3style);
		c3.setRenderer(c3style);

	}

	@Override
	public void miseAJourVueDonnees() { 
		// Style
		getComponent().setStyleGenerator(new TableTotalCellStyle(EntetesTreeResumeDepenseEnum.CATEGORIE));
////	getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Align.RIGHT);
////	getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Align.RIGHT);

	}
	

	/**
	 * Mise à jour des données suite au budget
	 * @param refreshAllTable refresh total en cas de changemetn de mois ou de compte
	 * @param budget budget à jour
	 * @param dateBudget date du budget
	 */
	public void miseAJourVueDonnees(BudgetMensuel budget, Calendar dateBudget){
		
		if(dateBudget == null){
			dateBudget = Calendar.getInstance();
		}
		
		// Injection des données
		List<TotalBudgetMensuel> totauxBudget = new ArrayList<TotalBudgetMensuel>();
		totauxBudget.add(new TotalBudgetMensuel("Argent avancé", budget.getNowArgentAvance(), budget.getFinArgentAvance()));
		totauxBudget.add(new TotalBudgetMensuel("Solde réel du compte", budget.getNowCompteReel(), budget.getFinCompteReel()));
		
		// Maj des colonnes
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle()+ auDateFormat.format(dateBudget.getTime()));
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle()+ finDateFormat.format(dateBudget.getTime()));
		getComponent().setItems(totauxBudget);

		getComponent().setDescription("Marge de sécurité : "+budget.getMargeSecurite()+" € <br> Marge à fin de mois : " + budget.getMargeSecuriteFinMois() + " €");
	}

}
