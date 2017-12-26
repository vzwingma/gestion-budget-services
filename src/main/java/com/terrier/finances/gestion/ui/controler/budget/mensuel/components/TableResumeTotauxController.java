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
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.ui.components.grid.FooterRow;

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
//		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.CATEGORIE.getId(), String.class, null);
//		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Double.class, null);
//		getComponent().addContainerProperty(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Double.class, null);
//		getComponent().setColumnHeader(EntetesTreeResumeDepenseEnum.CATEGORIE.getId(), EntetesTreeResumeDepenseEnum.CATEGORIE.getLibelle());
	}

	@Override
	public void miseAJourVueDonnees() { 
		// Style
//		getComponent().setCellStyleGenerator(new TableTotalCellStyle());
	}
	
	/**
	 * Effacement des données
	 */
	public void resetVueDonnees(){
//		getComponent().removeAllItems();
	//		getComponent().refreshRowCache();
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
//		
//		getComponent().setColumns(
//				EntetesTreeResumeDepenseEnum.CATEGORIE.getId(),
//				EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), 
//				EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId());
//		
//		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).
//			setCaption(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle()+ auDateFormat.format(dateBudget.getTime()));
//		
//		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).
//			setCaption(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle() + finDateFormat.format(dateBudget.getTime()));
//		
////		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId(), Align.RIGHT);
////		getComponent().setColumnAlignment(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId(), Align.RIGHT);
//		
//		FooterRow fr1 = getComponent().addFooterRowAt(0);
//			fr1.getCell(EntetesTreeResumeDepenseEnum.CATEGORIE.getId()).setText("Argent avancé");
//			fr1.getCell(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).setText(""+budget.getNowArgentAvance());
//			fr1.getCell(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).setText(""+budget.getFinArgentAvance());
//
//		FooterRow fr2 = getComponent().addFooterRowAt(0);
//			fr2.getCell(EntetesTreeResumeDepenseEnum.CATEGORIE.getId()).setText("Solde réel du compte");
//			fr2.getCell(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).setText(""+budget.getNowCompteReel());
//			fr2.getCell(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).setText(""+budget.getFinCompteReel());
		
		getComponent().setDescription("Marge de sécurité : "+budget.getMargeSecurite()+" € <br> Marge à fin de mois : " + budget.getMargeSecuriteFinMois() + " €");
	}

}
