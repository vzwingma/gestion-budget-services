/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.GridResumeTotaux;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;

/**
 * Controleur du tableau des résumés
 * @author vzwingma
 *
 */
public class GridResumeTotauxController extends AbstractUIController<GridResumeTotaux>{


	private static final long serialVersionUID = 5190668755144306669L;
	
	protected final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
	protected final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy", Locale.FRENCH);

	
	/**
	 * @param composant
	 */
	public GridResumeTotauxController(GridResumeTotaux composant) {
		super(composant);
	}


	@Override
	public void miseAJourVueDonnees() { 
		// Rien, cf #miseAJourVueDonnees(BudgetMensuel budget, Calendar dateBudget)

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
