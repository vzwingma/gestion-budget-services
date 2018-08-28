package com.terrier.finances.gestion.ui.controler.budget.mensuel.liste.operations;

import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.vaadin.ui.Button;

/**
 * Controleur des boutons d'actions d'une opération
 * @author vzwingma
 *
 */
public class ActionsLigneBudgetController extends AbstractUIController<ActionsLigneBudget> {


	private static final long serialVersionUID = 3969804553001678780L;

	private String idOperation;
	/**
	 * Contructeur
	 * @param composant
	 */
	public ActionsLigneBudgetController(ActionsLigneBudget composant) {
		super(composant);
	}


	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.ui.controler.AbstractUIController#miseAJourVueDonnees()
	 */
	@Override
	public void miseAJourVueDonnees() {
		// rien à faire
	}


	/**
	 * mise à jour de l'état de la ligne
	 * @param etat
	 */
	public void miseAJourEtatLigne(EtatLigneDepenseEnum etat){
		Button boutonInactif = getComponent().getButtonPrevue();
		if(etat != null){
			if(etat.equals(EtatLigneDepenseEnum.ANNULEE)) {
				boutonInactif = getComponent().getButtonAnnuler();
			}
			else if(etat.equals(EtatLigneDepenseEnum.REALISEE)) {
				boutonInactif = getComponent().getButtonReel();
			}
			else if(etat.equals(EtatLigneDepenseEnum.REPORTEE)) {
				boutonInactif = getComponent().getButtonReporter();
			}
			else if(etat.equals(EtatLigneDepenseEnum.PREVUE)) {
				boutonInactif = getComponent().getButtonPrevue();
			}
		}
		else{
			boutonInactif = getComponent().getButtonSupprimer();
		}
		boutonInactif.setVisible(false);
	}


	/**
	 * @return the idDepense
	 */
	public String getIdOperation() {
		return idOperation;
	}


	/**
	 * @param idOperation the idOperation to set
	 */
	public void setIdOperation(String idOperation) {
		this.idOperation = idOperation;
	}


}

