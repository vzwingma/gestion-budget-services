package com.terrier.finances.gestion.business;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.data.DepensesDatabaseService;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.enums.EntetesTableSuiviDepenseEnum;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;

/**
 * Service Métier : Dépenses
 * @author vzwingma
 *
 */
@Service
public class BusinessDepensesService {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessDepensesService.class);

	/**
	 * Lien vers les données
	 */
	@Autowired
	private DepensesDatabaseService dataDepenses;

	/**
	 * Paramétrages
	 */
	@Autowired
	private ParametragesService serviceParams;


	public static final String ID_SS_CAT_TRANSFERT_INTERCOMPTE = "ed3f6100-5dbd-4b68-860e-0c97ae1bbc63";

	public static final String ID_SS_CAT_SALAIRE = "d005de34-f768-4e96-8ccd-70399792c48f";

	public static final String ID_SS_CAT_REMBOURSEMENT = "885e0d9a-6f3c-4002-b521-30169baf7123";

	public static final String ID_SS_CAT_RESERVE = "26a4b966-ffdc-4cb7-8611-a5ba4b518ef5";
	public static final String ID_SS_CAT_PREVISION_SANTE = "eeb2f9a5-49b4-4c44-86bf-3bd626412d8e";
	/**
	 * Chargement du budget du mois courant
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public BudgetMensuel chargerBudgetMensuel(Utilisateur utilisateur, String compte, int mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget {} de {}/{}", compte, mois, annee);
		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compte, mois, annee);
		}
		catch(BudgetNotFoundException e){
			budgetMensuel = initNewBudget(compte, utilisateur, mois, annee);
		}
		// Maj du budget ssi budhet actif
		if(budgetMensuel != null && budgetMensuel.isActif()){
			// Recalcul du résultat du mois précédent
			try{
				int moisPrecedent = 0;
				int anneePrecedente = annee;
				if(mois == Calendar.JANUARY){
					moisPrecedent = Calendar.DECEMBER;
					anneePrecedente = annee -1;
				}
				else{
					moisPrecedent = mois -1;
				}
				BudgetMensuel budgetPrecedent = this.dataDepenses.chargeBudgetMensuel(compte, moisPrecedent, anneePrecedente);
				if(budgetPrecedent.isActif()){
					calculBudget(budgetPrecedent);
				}
				budgetMensuel.setResultatMoisPrecedent(budgetPrecedent.getFinArgentAvance());
			}
			catch(BudgetNotFoundException e){ }
			// Résultat mensuel mis à jour
			calculBudgetEtSauvegarde(budgetMensuel);
		}

		return budgetMensuel;
	}

	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param compte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public Calendar[] getDatePremierDernierBudgets(String compte) throws DataNotFoundException{
		return this.dataDepenses.getDatePremierDernierBudgets(compte);
	}

	/**
	 * Charge la date de mise à jour du budget
	 * @param idBudget identifiant du budget
	 * @return la date de mise à jour du  budget
	 */
	public boolean isBudgetUpToDate(String idBudget, Calendar dateToCompare) {

		Date dateEnBDD = this.dataDepenses.getDateMiseAJourBudget(idBudget);
		if(dateEnBDD != null){
			return dateToCompare.getTime().before(dateEnBDD);
		}
		else{
			LOGGER.error("[REFRESH] Impossible de trouver la date de mise à jour du budget. Annulation du traitement de rafraichissement");
			return false;
		}
	}


	/**
	 * Chargement du budget du mois courant
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	/**
	 * @param utilisateur utilisateur
	 * @param compte compte
	 * @return liste des budget
	 * @throws DataNotFoundException erreur
	 */
	public List<BudgetMensuelDTO> chargerBudgetsMensuelsConsultation(Utilisateur utilisateur, String compte) throws DataNotFoundException{
		LOGGER.debug("Chargement des budgets {} de {}", compte, utilisateur);
		return this.dataDepenses.chargeBudgetsMensuelsDTO(compte);
	}




	/**
	 * Chargement de l'état du budget du mois courant en consultation
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public boolean isBudgetMensuelActif(String compte, int mois, int annee){
		LOGGER.debug("Chargement du budget {} de {}/{}", compte, mois, annee);
		BudgetMensuelDTO budgetMensuel;
		try {
			budgetMensuel = this.dataDepenses.chargeBudgetMensuelDTO(compte, mois, annee);
			return budgetMensuel != null ? budgetMensuel.isActif() : false;
		} catch (BudgetNotFoundException e) {
			return false;
		}

	}

	/**
	 * Chargement des lignes de dépenses 
	 * @param idBudget idBudget
	 * @return lignes de dépenses du budget
	 * @throws DataNotFoundException erreur
	 */
	public List<LigneDepense> chargerLignesDepenses(String idBudget) throws BudgetNotFoundException{
		LOGGER.debug("Chargement des dépenses de {}", idBudget);
		BudgetMensuel budgetMensuel = this.dataDepenses.chargeBudgetMensuelById(idBudget);
		return budgetMensuel.getListeDepenses();
	}

	/**
	 * Init new budget
	 * @param compte compte
	 * @param mois mois
	 * @param annee année
	 * @return budget nouvellement créé
	 * @throws BudgetNotFoundException erreur budhet
	 * @throws DataNotFoundException erreur données
	 */
	private BudgetMensuel initNewBudget(String compte, Utilisateur utilisateur, int mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.info("[INIT] Initialisation du budget {} de {}/{}", compte, mois, annee);
		BudgetMensuel budget = new BudgetMensuel();
		budget.setActif(true);
		budget.setAnnee(annee);
		budget.setMois(mois);

		budget.setCompteBancaire(serviceParams.getCompteById(compte, utilisateur));
		budget.setDateMiseAJour(Calendar.getInstance());
		// Init si dans le futur par rapport au démarrage
		Calendar datePremierBudget = getDatePremierDernierBudgets(compte)[0];
		datePremierBudget.set(Calendar.DAY_OF_MONTH, 1);

		Calendar dateCourante = Calendar.getInstance();
		dateCourante.set(Calendar.MONTH, mois);
		dateCourante.set(Calendar.YEAR, annee);

		if(dateCourante.after(datePremierBudget)){
			// MAJ Calculs à partir du mois précédent
			// Mois précédent
			int moisPrecedent = 0;
			int anneePrecedente = annee;
			if(mois == Calendar.JANUARY){
				moisPrecedent = Calendar.DECEMBER;
				anneePrecedente = annee -1;
			}
			else{
				moisPrecedent = mois -1;
			}
			// Recherche du budget précédent 
			// Si impossible : BudgetNotFoundException
			BudgetMensuel budgetPrecedent = chargerBudgetMensuel(utilisateur, compte, moisPrecedent, anneePrecedente);
			initBudgetFromBudgetPrecedent(budget, budgetPrecedent);
		}
		else{
			LOGGER.error("[INIT] le buget {}/{} n'a jamais existé", mois, annee);
			throw new BudgetNotFoundException();
		}

		LOGGER.info("[INIT] Sauvegarde du Budget {}", budget);
		boolean save = this.dataDepenses.sauvegardeBudgetMensuel(budget);
		if(save){
			return budget;
		}
		else{
			return null;
		}
	}


	/**
	 * Réinitialiser un budget mensuel
	 * @param budgetMensuel budget mensuel
	 * @throws DataNotFoundException  erreur sur les données
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public void reinitialiserBudgetMensuel(BudgetMensuel budgetMensuel, Utilisateur utilisateur) throws BudgetNotFoundException, DataNotFoundException{
		initNewBudget(budgetMensuel.getCompteBancaire().getId(), utilisateur, budgetMensuel.getMois(), budgetMensuel.getAnnee());
	}
	/**
	 * Initialisation du budget à partir du budget du mois précédent
	 * @param budget
	 * @param budgetPrecedent
	 */
	private void initBudgetFromBudgetPrecedent(BudgetMensuel budget, BudgetMensuel budgetPrecedent){
		// Calcul
		calculBudget(budgetPrecedent);
		budget.setCompteBancaire(budgetPrecedent.getCompteBancaire());
		budget.setMargeSecurite(budgetPrecedent.getMargeSecurite());
		budget.setResultatMoisPrecedent(budgetPrecedent.getFinArgentAvance());
		budget.setDateMiseAJour(Calendar.getInstance());
		for (LigneDepense depenseMoisPrecedent : budgetPrecedent.getListeDepenses()) {
			if(depenseMoisPrecedent.isPeriodique() || depenseMoisPrecedent.getEtat().equals(EtatLigneDepenseEnum.REPORTEE)){
				budget.getListeDepenses().add(depenseMoisPrecedent.cloneDepenseToMoisSuivant());	
				budget.getSetLibellesDepensesForAutocomplete().add(depenseMoisPrecedent.getLibelle());
			}

		}
	}


	/**
	 * Ajout d'une ligne transfert intercompte
	 * @param ligneDepense ligne de dépense de transfert
	 * @param compteCrediteur compte créditeur
	 * @param auteur auteur de l'action
	 * @throws BudgetNotFoundException erreur budget introuvable
	 * @throws DataNotFoundException erreur données
	 */
	public void ajoutLigneTransfertIntercompte(String idBudget, LigneDepense ligneDepense, String compteCrediteur, Utilisateur utilisateur) throws BudgetNotFoundException, DataNotFoundException{

		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget);
		LOGGER.info("Ajout d'un transfert intercompte de {} vers {} > {} ", budget.getCompteBancaire().getLibelle(), compteCrediteur, ligneDepense);
		/**
		 *  Si transfert intercompte : Création d'une ligne dans le compte distant
		 */
		BudgetMensuel budgetTransfert = chargerBudgetMensuel(utilisateur, compteCrediteur, budget.getMois(), budget.getAnnee());
		LigneDepense ligneTransfert = new LigneDepense(ligneDepense.getSsCategorie(), "[de "+budget.getCompteBancaire().getLibelle()+"] " + ligneDepense.getLibelle(), TypeDepenseEnum.CREDIT, ligneDepense.getValeur(), EtatLigneDepenseEnum.PREVUE, ligneDepense.isPeriodique());
		ajoutLigneDepense(budgetTransfert, ligneTransfert, utilisateur.getLibelle());
		calculBudgetEtSauvegarde(budgetTransfert);
		/**
		 *  Ajout de la ligne dans le budget courant
		 */
		ligneDepense.setLibelle("[vers "+budgetTransfert.getCompteBancaire().getLibelle()+"] " + ligneDepense.getLibelle());
		ajoutLigneDepenseEtCalcul(idBudget, ligneDepense, utilisateur.getLibelle());
	}



	/**
	 * Ajout d'une ligne de dépense
	 * @param ligneDepense ligne de dépense
	 * @param auteur auteur de l'action
	 */
	private void ajoutLigneDepense(BudgetMensuel budget, LigneDepense ligneDepense, String auteur){
		LOGGER.info("Ajout d'une ligne de dépense : {}", ligneDepense);
		ligneDepense.setAuteur(auteur);
		ligneDepense.setDateMaj(Calendar.getInstance().getTime());
		budget.getListeDepenses().add(ligneDepense);
		budget.setDateMiseAJour(Calendar.getInstance());
	}


	/**
	 * Ajout d'une ligne de dépense
	 * @param ligneDepense ligne de dépense
	 * @param auteur auteur de l'action 
	 * @throws BudgetNotFoundException 
	 */
	public BudgetMensuel ajoutLigneDepenseEtCalcul(String idBudget, LigneDepense ligneDepense, String auteur) throws BudgetNotFoundException{
		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget);
		ajoutLigneDepense(budget, ligneDepense, auteur);
		// Résultat mensuel
		calculBudgetEtSauvegarde(budget);
		return budget;
	}

	/**
	 * @param ligneId
	 * @return {@link LigneDepense} correspondance
	 */
	private LigneDepense getLigneDepense(BudgetMensuel budget, String ligneId){
		// Recherche de la ligne
		LigneDepense ligneDepense = null;
		for (LigneDepense ligne : budget.getListeDepenses()) {
			if(ligne.getId().equals(ligneId)){
				ligneDepense = ligne;
				break;
			}
		}
		return ligneDepense;
	}

	/**
	 * Mise à jour des lignes de dépenses
	 * 
	 * Attention : Pas d'appel à CalculBudget() car c'est fait seulement à la fin de toute la liste
	 * 
	 * @param ligneId  id de la ligne de dépense
	 * @param propertyId id de la propriété
	 * @param propClass classe de la propriété
	 * @param value nouvelle valeur
	 * @throws DataNotFoundException données introuvable
	 */
	public void majLigneDepense(BudgetMensuel budgetEnCours, String ligneId, String propertyId, @SuppressWarnings("rawtypes") Class propClass, Object value, Utilisateur auteur) throws DataNotFoundException{
		
		// Recherche de la ligne
		LigneDepense ligneDepense = getLigneDepense(budgetEnCours, ligneId);
		boolean ligneUpdated = false;
		// Maj du modele (sauf pour Etat=null car cela signifie suppression de la ligne)
		if(ligneDepense != null){
			if(propertyId.equals("Etat") && value == null){
				ligneUpdated = budgetEnCours.getListeDepenses().remove(ligneDepense);
			}
			// Maj du modele (sauf pour CATEGORIE, DATE MAJ et AUTEUR)
			else if(!propertyId.equals(EntetesTableSuiviDepenseEnum.CATEGORIE.getId())
					&& !propertyId.equals(EntetesTableSuiviDepenseEnum.AUTEUR.getId())
					&& !propertyId.equals(EntetesTableSuiviDepenseEnum.DATE_MAJ.getId())){
				ligneUpdated = ligneDepense.updateProperty(ligneId, propertyId, propClass, value);
			}
			// LibelleView n'est que pour l'IHM au même titre que les actions. donc pas de mise à jour des dates fonctionnelles
			if(ligneUpdated && !"LibelleView".equals(propertyId)){
				ligneDepense.setDateMaj(Calendar.getInstance().getTime());
				ligneDepense.setAuteur(auteur != null ? auteur.getLibelle() : "");
				// Mise à jour du budget
				budgetEnCours.setDateMiseAJour(Calendar.getInstance());
			}
		}
		else{
			throw new DataNotFoundException("La ligne de dépense est introuvable");
		}
	}	


	/**
	 * Mise à jour d'une ligne de dépense 
	 * @param idBudget identifiant de budget
	 * @param ligneDepense ligne de dépense
	 * @param auteur auteur
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 */
	public BudgetMensuel majLigneDepense(String idBudget, LigneDepense ligneDepense, String auteur) throws DataNotFoundException, BudgetNotFoundException{

		boolean ligneupdated = false;
		if(ligneDepense != null){
			BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget);
			if(ligneDepense.getEtat() == null){
				for (Iterator<LigneDepense> iterator = budget.getListeDepenses().iterator(); iterator
						.hasNext();) {
					LigneDepense type = (LigneDepense) iterator.next();
					if(type.getId().equals(ligneDepense.getId())){
						iterator.remove();
						ligneupdated = true;
					}
				}
			}
			else{
				ligneDepense.setDateMaj(Calendar.getInstance().getTime());
				ligneDepense.setAuteur(auteur);
				// Mise à jour de la ligne de dépense
				for (int i = 0; i < budget.getListeDepenses().size(); i++) {
					if(budget.getListeDepenses().get(i).getId().equals(ligneDepense.getId())){
						budget.getListeDepenses().set(i, ligneDepense);
						ligneupdated = true;
						break;
					}
				}
			}
			if(ligneupdated){
				// Mise à jour du budget
				budget.setDateMiseAJour(Calendar.getInstance());
				// Budget
				calculBudgetEtSauvegarde(budget);
				return budget;
			}
			else{
				return null;
			}
		}
		else{
			throw new DataNotFoundException("La ligne de dépense est introuvable");
		}
	}	



	/**
	 * Enregistrement de la note pour une ligne
	 * @param budget budget courant
	 * @param ligneId id de la ligne à mettre à jour
	 * @param note note à enregistrer
	 * @param auteur auteur auteur de la note
	 * @throws DataNotFoundException données introuvable
	 * @throws BudgetNotFoundException erreur budget non trouvé
	 */
	public void majNotesLignesDepenses(String idBudget, String ligneId, String note, Utilisateur auteur) throws DataNotFoundException, BudgetNotFoundException{
		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget);
		majLigneDepense(budget, ligneId, "Notes", String.class, note, auteur);
		// Mise à jour du budget
		dataDepenses.sauvegardeBudgetMensuel(budget);
		UISessionManager.getSession().setBudgetMensuelCourant(budget);
	}


	/**
	 * Mise à jour de la ligne de dépense du budget
	 * @param ligneId ligne à modifier
	 * @param etat état de la ligne
	 * @param auteur auteur de l'action
	 * @throws DataNotFoundException erreur ligne non trouvé
	 * @throws BudgetNotFoundException not found
	 */
	public void majEtatLigneDepense(BudgetMensuel budget, String ligneId, EtatLigneDepenseEnum etat, String auteur) throws DataNotFoundException, BudgetNotFoundException{
		LigneDepense ligneDepense = getLigneDepense(budget, ligneId);
		// Mise à jour de l'état
		if(ligneDepense != null){
			ligneDepense.setEtat(etat);
			if(EtatLigneDepenseEnum.REALISEE.equals(etat)){
				ligneDepense.setDateOperation(Calendar.getInstance().getTime());
			}
			else{
				ligneDepense.setDateOperation(null);
			}
			// Mise à jour de la ligne
			ligneDepense.setDateMaj(Calendar.getInstance().getTime());
			ligneDepense.setAuteur(auteur);
			majLigneDepense(budget.getId(), ligneDepense, auteur);
		}
	}	


	/**
	 * Mise à jour de la ligne comme dernière opération
	 * @param ligneId
	 */
	public void setLigneDepenseAsDerniereOperation(BudgetMensuel budget, String ligneId){
		for (LigneDepense ligne : budget.getListeDepenses()) {
			if(ligne.getId().equals(ligneId)){
				LOGGER.info("Tag de la ligne {} comme dernière opération", ligne);
				ligne.setDerniereOperation(true);
			}
			else{
				ligne.setDerniereOperation(false);
			}
		}
		// Mise à jour du budget
		budget.setDateMiseAJour(Calendar.getInstance());
		dataDepenses.sauvegardeBudgetMensuel(budget);
	}

	/**
	 * Calcul du budget Courant et sauvegarde
	 * @param budget budget à sauvegarder
	 */
	public void calculBudgetEtSauvegarde(BudgetMensuel budget){
		calculBudget(budget);
		dataDepenses.sauvegardeBudgetMensuel(budget);
	}

	/**
	 * Calcul du résumé
	 * @param budgetMensuelCourant
	 */
	private void calculBudget(BudgetMensuel budget){

		LOGGER.info("(Re)Calcul du budget : {}", budget);
		budget.razCalculs();

		for (LigneDepense depense : budget.getListeDepenses()) {
			LOGGER.trace("     > {}", depense);
			int sens = depense.getTypeDepense().equals(TypeDepenseEnum.CREDIT) ? 1 : -1;
			budget.getSetLibellesDepensesForAutocomplete().add(depense.getLibelle());
			/**
			 *  Calcul par catégorie
			 */
			Double[] valeursCat = {0D,0D};
			if(budget.getTotalParCategories().get(depense.getCategorie()) != null){
				valeursCat = budget.getTotalParCategories().get(depense.getCategorie());
			}
			if(depense.getEtat().equals(EtatLigneDepenseEnum.REALISEE)){
				valeursCat[0] = valeursCat[0] + sens * depense.getValeur();
				valeursCat[1] = valeursCat[1] + sens * depense.getValeur();
			}
			else if(depense.getEtat().equals(EtatLigneDepenseEnum.PREVUE)){
				valeursCat[1] = valeursCat[1] + sens * depense.getValeur();
			}
			budget.getTotalParCategories().put(depense.getCategorie(), valeursCat);

			/**
			 *  Calcul par sous catégorie
			 */
			Double[] valeurSsCat = {0D,0D};
			if( budget.getTotalParSSCategories().get(depense.getSsCategorie()) != null){
				valeurSsCat = budget.getTotalParSSCategories().get(depense.getSsCategorie());
			}
			if(depense.getEtat().equals(EtatLigneDepenseEnum.REALISEE)){
				valeurSsCat[0] = valeurSsCat[0] + sens * depense.getValeur();
				valeurSsCat[1] = valeurSsCat[1] + sens * depense.getValeur();
			}
			if(depense.getEtat().equals(EtatLigneDepenseEnum.PREVUE)){
				valeurSsCat[1] = valeurSsCat[1] + sens * depense.getValeur();
			}
			budget.getTotalParSSCategories().put(depense.getSsCategorie(), valeurSsCat);



			// Si réserve : ajout dans le calcul fin de mois
			// Pour taper dans la réserve : inverser le type de dépense

			if(ID_SS_CAT_RESERVE.equals(depense.getSsCategorie().getId())){
				budget.setMargeSecuriteFinMois(budget.getMargeSecurite() + Double.valueOf(depense.getValeur()));
				sens = - sens;
			}

			/**
			 * Calcul des totaux
			 */

			if(depense.getEtat().equals(EtatLigneDepenseEnum.REALISEE)){
				budget.ajouteANowArgentAvance(sens * depense.getValeur());
				budget.ajouteANowCompteReel(sens * depense.getValeur());
				budget.ajouteAFinArgentAvance(sens * depense.getValeur());
				budget.ajouteAFinCompteReel(sens * depense.getValeur());				
			}
			else if(depense.getEtat().equals(EtatLigneDepenseEnum.PREVUE)){
				budget.ajouteAFinArgentAvance(sens * depense.getValeur());
				budget.ajouteAFinCompteReel(sens * depense.getValeur());
			}
		}
		LOGGER.debug("Argent avancé : {}  :   {}", budget.getNowArgentAvance(), budget.getFinArgentAvance());
		LOGGER.debug("Solde réel    : {}  :   {}", budget.getNowCompteReel(), budget.getFinCompteReel());
	}

	/**
	 * Lock/unlock d'un budget
	 * @param budgetActif
	 */
	public void setBudgetActif(BudgetMensuel budgetMensuel, boolean budgetActif){
		LOGGER.info("{} du budget {}/{} de {}", budgetActif ? "Réouverture" : "Fermeture", budgetMensuel.getMois(), budgetMensuel.getAnnee(), budgetMensuel.getCompteBancaire().getLibelle());
		budgetMensuel.setActif(budgetActif);
		budgetMensuel.setDateMiseAJour(Calendar.getInstance());
		calculBudgetEtSauvegarde(budgetMensuel);
	}
}
