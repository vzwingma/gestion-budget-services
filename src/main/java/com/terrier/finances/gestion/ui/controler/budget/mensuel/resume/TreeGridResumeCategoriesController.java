/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.resume;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.ResumeTotalCategories;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.data.DataUtils;
import com.terrier.finances.gestion.model.enums.EntetesTreeResumeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TreeGridResumeCategories;
import com.terrier.finances.gestion.ui.controler.budget.mensuel.totaux.GridResumeTotauxController;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;

/**
 * Controleur du tableau des résumés
 * @author vzwingma
 *
 */
public class TreeGridResumeCategoriesController extends AbstractUIController<TreeGridResumeCategories>{



	//
	private static final long serialVersionUID = 5190668755144306669L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeGridResumeCategoriesController.class);

	/**
	 * @param composant
	 */
	public TreeGridResumeCategoriesController(TreeGridResumeCategories composant) {
		super(composant);
	}

	private boolean gridCollapsed = true;


	@Override
	public void miseAJourVueDonnees() { 
		// Rien cf. #miseAJourVueDonnees(BudgetMensuel budget)
	}

	/**
	 * Mise à jour des données suite au budget
	 * @param refreshAllTable refresh total en cas de changemetn de mois ou de compte
	 * @param budget budget à jour
	 * @param dateBudget date du budget
	 */
	public void miseAJourVueDonnees(BudgetMensuel budget){

		// Libellés
		LocalDate dateDerniereOperation = DataUtils.getMaxDateListeOperations(budget.getListeDepenses());
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_NOW.getLibelle()+ dateDerniereOperation.format(GridResumeTotauxController.auDateFormat));
		getComponent().getColumn(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getId()).setCaption(EntetesTreeResumeDepenseEnum.VALEUR_FIN.getLibelle()+ dateDerniereOperation.format(GridResumeTotauxController.finDateFormat));


		// Données des résumés
		List<ResumeTotalCategories> listeResumeTotaux = new ArrayList<>();

		// Tri des catégories
			for (CategorieDepense categorie : getServiceParams().getCategories()) {

				if(categorie != null && budget.getTotalParCategories().get(categorie) != null){

					ResumeTotalCategories totalCat = new ResumeTotalCategories(categorie.getLibelle(), budget.getTotalParCategories().get(categorie)[0], budget.getTotalParCategories().get(categorie)[1]);
					listeResumeTotaux.add(totalCat);
					for (CategorieDepense ssCategorie : categorie.getListeSSCategories()) {
						if(budget.getTotalParSSCategories().get(ssCategorie) == null){
							totalCat.getSousCategories().add(new ResumeTotalCategories(ssCategorie.getLibelle(), 0D,0D));
						}
						else{
							totalCat.getSousCategories().add(new ResumeTotalCategories(ssCategorie.getLibelle(), 
									budget.getTotalParSSCategories().get(ssCategorie)[0], 
									budget.getTotalParSSCategories().get(ssCategorie)[1]));
						}
					}
				}
				else{
					LOGGER.trace("Attention : Catégorie vide");
				}
			}
		listeResumeTotaux.sort((r1, r2) -> r1.getTypeTotal().compareTo(r2.getTypeTotal()));

		getComponent().setItems(listeResumeTotaux, ResumeTotalCategories::getSousCategories);
		this.gridCollapsed = true;
		collapseExpendTreeGrid();
	}
	
	/**
	 * Collapse/expand
	 */
	public void collapseExpendTreeGrid(){
		getComponent().getTreeData().getRootItems()
		.stream()
		.forEach(
				categorie -> {
					if(gridCollapsed){
						getComponent().getDataCommunicator().expand(categorie);
					}
					else{
						getComponent().getDataCommunicator().collapse(categorie);
					}
				});
		gridCollapsed = !gridCollapsed;
	}
}
