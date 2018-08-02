/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.TotalBudgetMensuel;
import com.terrier.finances.gestion.model.data.DataUtils;
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
	
	public static final DateTimeFormatter auDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
	public static final DateTimeFormatter finDateFormat = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

	
	/**
	 * @param composant
	 */
	public GridResumeTotauxController(GridResumeTotaux composant) {
		super(composant);
	}


	@Override
	public void miseAJourVueDonnees() { 
		// Rien, cf #miseAJourVueDonnees(BudgetMensuel budget)

	}
	

	/**
	 * Mise à jour des données suite au budget
	 * @param refreshAllTable refresh total en cas de changemetn de mois ou de compte
	 * @param budget budget à jour
	 * @param dateBudget date du budget
	 */
	public void miseAJourVueDonnees(BudgetMensuel budget){

		LocalDate dateDerniereOperation = DataUtils.getMaxDateListeOperations(budget.getListeDepenses());
		
		// Injection des données
		List<TotalBudgetMensuel> totauxBudget = new ArrayList<>();
		totauxBudget.add(new TotalBudgetMensuel("Argent avancé", budget.getNowArgentAvance(), budget.getFinArgentAvance()));
		totauxBudget.add(new TotalBudgetMensuel("Solde réel du compte", budget.getNowCompteReel(), budget.getFinCompteReel()));
		
		// Maj des colonnes
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle()+ dateDerniereOperation.format(auDateFormat));
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle()+ dateDerniereOperation.format(finDateFormat));
		getComponent().setItems(totauxBudget);
		getComponent().getDataProvider().refreshAll();
		getComponent().setDescription("Marge de sécurité : "+budget.getMargeSecurite()+" € <br> Marge à fin de mois : " + budget.getMargeSecuriteFinMois() + " €");
	}

}
