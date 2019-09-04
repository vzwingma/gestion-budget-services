package com.terrier.finances.gestion.services.budgets.business;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budgets.model.transformer.DataTransformerLigneOperation;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.comptes.business.ComptesService;
import com.terrier.finances.gestion.services.utilisateurs.model.UserBusinessSession;

/**
 * Service Métier : Operations
 * @author vzwingma
 *
 */
@Service
public class OperationsService extends AbstractBusinessService {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsService.class);

	/**
	 * Lien vers les données
	 */
	@Autowired
	private BudgetDatabaseService dataDepenses;

	@Autowired
	private ComptesService compteServices;

	private DataTransformerLigneOperation transformer = new DataTransformerLigneOperation();




	/**
	 * Chargement du budget du mois courant
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public BudgetMensuel chargerBudgetMensuel(String idCompte, Month mois, int annee, UserBusinessSession userSession) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget {} de {}/{}", idCompte, mois, annee);

		CompteBancaire compteBancaire = getServiceComptes().getCompteById(idCompte, userSession.getUtilisateur().getId());
		if(compteBancaire != null){
			if(compteBancaire.isActif()){
				try {
					return chargerBudgetMensuelSurCompteActif(userSession, compteBancaire, mois, annee);
				} catch (CompteClosedException e) {
					// Rien car géré en aval
				}
			}
			else{
				return chargerBudgetMensuelSurCompteInactif(userSession, compteBancaire, mois, annee);
			}
		}
		throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(idCompte).append(" de ").append(userSession.getUtilisateur().getId()).toString());
	}


	/**
	 * Chargement du budget du mois courant pour le compte actif
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private BudgetMensuel chargerBudgetMensuelSurCompteActif(UserBusinessSession userSession, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte actif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee);
		}
		catch(BudgetNotFoundException e){
			budgetMensuel = initNewBudget(compteBancaire, userSession, mois, annee);
		}
		// Maj du budget ssi budget actif
		if(budgetMensuel != null && budgetMensuel.isActif()){
			// Recalcul du résultat du mois précédent
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			try{
				LOGGER.debug("Chargement du budget du mois précédent du compte actif {} : {}/{}", compteBancaire.getId(), moisPrecedent, anneePrecedente);
				BudgetMensuel budgetPrecedent = this.dataDepenses.chargeBudgetMensuel(compteBancaire, moisPrecedent, anneePrecedente);
				budgetMensuel.setResultatMoisPrecedent(budgetPrecedent.getSoldeFin(), budgetPrecedent.getMarge());
			}
			catch(BudgetNotFoundException e){
				LOGGER.error("Le budget précédent celui de [{}/{}] : [{}/{}] est introuvable", mois, annee, moisPrecedent, anneePrecedente);
			}
			// Résultat mensuel mis à jour
			calculEtSauvegardeBudget(budgetMensuel);
		}
		return budgetMensuel;
	}


	/**
	 * Chargement du budget du mois courant pour le compte inactif
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private BudgetMensuel chargerBudgetMensuelSurCompteInactif(UserBusinessSession userSession, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte inactif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee);
		}
		catch(BudgetNotFoundException e){
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			budgetMensuel = chargerBudgetMensuelSurCompteInactif(userSession, compteBancaire, moisPrecedent, anneePrecedente);
		}
		// On reporte l'état inactif du compte sur les anciens budgets
		budgetMensuel.setCompteBancaire(compteBancaire);
		// L'état du budget est forcé à inactif
		budgetMensuel.setActif(false);
		return budgetMensuel;
	}


	/**
	 * Charge la date de mise à jour du budget
	 * @param idBudget identifiant du budget
	 * @param dateSurIHM Date affichée
	 * @return la date de mise à jour du  budget
	 */
	public boolean isBudgetUpToDate(String idBudget, Date dateSurIHM) {

		try {
			BudgetMensuel budgetMensuel =  this.dataDepenses.chargeBudgetMensuelById(idBudget);
			if(budgetMensuel != null){
				return budgetMensuel.getDateMiseAJour() != null ? dateSurIHM.after(budgetMensuel.getDateMiseAJour().getTime()) : null;
			}
		} catch (BudgetNotFoundException e) {
			LOGGER.error("Erreur lors de la recherche du budget [{}]", idBudget);
		}
		LOGGER.error("[REFRESH] Impossible de trouver la date de mise à jour du budget. Annulation du traitement de rafraichissement");
		return false;
	}


	/**
	 * Chargement de l'état du budget du mois courant en consultation
	 * @param idBudget id budget
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public boolean isBudgetMensuelActif(String idBudget) throws BudgetNotFoundException{
		return this.dataDepenses.isBudgetActif(idBudget);
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
	private BudgetMensuel initNewBudget(CompteBancaire compteBancaire, UserBusinessSession userSession, Month mois, int annee) throws BudgetNotFoundException,CompteClosedException, DataNotFoundException{
		LOGGER.info("[INIT] Initialisation du budget {} de {}/{}", compteBancaire.getLibelle(), mois, annee);
		BudgetMensuel budget = new BudgetMensuel();
		budget.setNewBudget(true);
		budget.setActif(true);
		budget.setAnnee(annee);
		budget.setMois(mois);
		budget.setCompteBancaire(compteBancaire);
		budget.setDateMiseAJour(Calendar.getInstance());
		// Init si dans le futur par rapport au démarrage
		LocalDate datePremierBudget;
		try{
			datePremierBudget = getServiceComptes().getIntervallesBudgets(compteBancaire.getId())[0].with(ChronoField.DAY_OF_MONTH, 1);
		}
		catch(DataNotFoundException e){
			datePremierBudget = null;
		}

		LocalDate dateCourante = BudgetDateTimeUtils.localDateFirstDayOfMonth(mois, annee);

		if(datePremierBudget != null && dateCourante.isAfter(datePremierBudget)){
			// MAJ Calculs à partir du mois précédent
			// Mois précédent
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			// Recherche du budget précédent 
			// Si impossible : BudgetNotFoundException
			BudgetMensuel budgetPrecedent = chargerBudgetMensuel(compteBancaire.getId(), moisPrecedent, anneePrecedente, userSession);
			// #115 : Cloture automatique du mois précédent
			budgetPrecedent = setBudgetActif(budgetPrecedent.getId(), false, userSession);

			initBudgetFromBudgetPrecedent(budget, budgetPrecedent);
		}
		else{
			LOGGER.warn("Le budget {} n'a jamais existé", compteBancaire.getLibelle());
			budget.setSoldeFin(0D);
			budget.setSoldeNow(0D);
			budget.setResultatMoisPrecedent(0D, 0D);
			budget.setListeOperations(new ArrayList<LigneOperation>());
		}

		LOGGER.info("[INIT] Sauvegarde du nouveau budget {}", budget);
		String idBudget = this.dataDepenses.sauvegardeBudgetMensuel(budget);
		if(idBudget != null){
			budget.setId(idBudget);
			return budget;
		}
		else{
			return null;
		}
	}

	/**
	 * @param idUtilisateur
	 * @param idBudget
	 * @return budget correspondant aux paramètres
	 * @throws UserNotAuthorizedException utilisateur non autorisé
	 * @throws BudgetNotFoundException budget introuvable
	 * @throws DataNotFoundException données introuvables
	 */
	public BudgetMensuel chargerBudgetMensuel(String idBudget, UserBusinessSession userSession) throws BudgetNotFoundException, DataNotFoundException{
		return chargerBudgetMensuel(BudgetDataUtils.getCompteFromBudgetId(idBudget), BudgetDataUtils.getMoisFromBudgetId(idBudget), BudgetDataUtils.getAnneeFromBudgetId(idBudget), userSession);
	}

	/**
	 * Réinitialiser un budget mensuel
	 * @param budgetMensuel budget mensuel
	 * @throws DataNotFoundException  erreur sur les données
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public BudgetMensuel reinitialiserBudgetMensuel(String idBudget, UserBusinessSession userSession) throws UserNotAuthorizedException, BudgetNotFoundException, CompteClosedException, DataNotFoundException{

		BudgetMensuel budgetMensuel = chargerBudgetMensuel(idBudget, userSession);
		if(budgetMensuel != null){
			CompteBancaire compteBancaire = getServiceComptes().getCompteById(budgetMensuel.getCompteBancaire().getId(), userSession.getUtilisateur().getId());
			if(compteBancaire != null){
				// S'il y a eu cloture, on ne fait rien
				return initNewBudget(compteBancaire, userSession, budgetMensuel.getMois(), budgetMensuel.getAnnee());
			}
			else{
				throw new DataNotFoundException("Le compte bancaire " + budgetMensuel.getCompteBancaire().getId() + " est introuvable");
			}
		}
		else{
			throw new DataNotFoundException("Le budget " + idBudget + " est introuvable");
		}
	}
	/**
	 * Initialisation du budget à partir du budget du mois précédent
	 * @param budget
	 * @param budgetPrecedent
	 */
	private void initBudgetFromBudgetPrecedent(BudgetMensuel budget, BudgetMensuel budgetPrecedent) throws CompteClosedException{
		// Calcul
		if(budget.getCompteBancaire().isActif() && budgetPrecedent.getCompteBancaire().isActif()){
			calculBudget(budgetPrecedent);
			budget.setCompteBancaire(budgetPrecedent.getCompteBancaire());
			// #116 : Le résultat du moins précédent est le compte réel, pas le compte avancé
			budget.setResultatMoisPrecedent(budgetPrecedent.getSoldeFin(), budgetPrecedent.getMarge());
			budget.setDateMiseAJour(Calendar.getInstance());
			if(budgetPrecedent.getListeOperations() != null){

				// Recopie de toutes les opérations périodiques, et reportées
				budget.getListeOperations().addAll(
						budgetPrecedent.getListeOperations()
						.stream()
						.filter(op -> op.isPeriodique() || EtatOperationEnum.REPORTEE.equals(op.getEtat()))
						.map(op -> transformer.cloneDepenseToMoisSuivant(op))
						.collect(Collectors.toList()));
			}
		}
		else{
			throw new CompteClosedException("Impossible d'initialiser un nouveau budget. Le compte est cloturé");
		}
	}


	/**
	 * Ajout d'une ligne transfert intercompte
	 * @param ligneOperation ligne de dépense de transfert
	 * @param compteCrediteur compte créditeur
	 * @param auteur auteur de l'action
	 * @throws BudgetNotFoundException erreur budget introuvable
	 * @throws DataNotFoundException erreur données
	 * @throws CompteClosedException 
	 */
	public BudgetMensuel createOperationIntercompte(String idBudget, LigneOperation ligneOperation, String idCompteDestination, UserBusinessSession userSession) throws UserNotAuthorizedException, BudgetNotFoundException, DataNotFoundException, CompteClosedException{

		/**
		 *  Si transfert intercompte : Création d'une ligne dans le compte distant
		 */
		String idBudgetDestination = BudgetDataUtils.getBudgetId(idCompteDestination, BudgetDataUtils.getMoisFromBudgetId(idBudget), BudgetDataUtils.getAnneeFromBudgetId(idBudget));
		LOGGER.info("[BUDGET] Ajout d'un transfert intercompte de {} vers {} ({}) > {} ", idBudget, idBudgetDestination, idCompteDestination, ligneOperation);
		String idCompteSource = BudgetDataUtils.getCompteFromBudgetId(idBudget);
		// #59 : Cohérence des états
		EtatOperationEnum etatDepenseCourant = ligneOperation.getEtat();
		EtatOperationEnum etatDepenseTransfert = null;
		switch (etatDepenseCourant) {
		case ANNULEE:
			etatDepenseTransfert = EtatOperationEnum.ANNULEE;
			break;
		case REPORTEE:
			etatDepenseTransfert = EtatOperationEnum.REPORTEE;
			break;
		case PREVUE:
		case REALISEE:
		default:				
			etatDepenseTransfert = EtatOperationEnum.PREVUE;
			break;
		}

		CompteBancaire compteSource = this.compteServices.getCompteById(idCompteSource, userSession.getUtilisateur().getId());
		CompteBancaire compteCible = this.compteServices.getCompteById(idCompteDestination, userSession.getUtilisateur().getId());

		LigneOperation ligneTransfert = new LigneOperation(
				ligneOperation.getSsCategorie(), 
				"[de "+compteSource.getLibelle()+"] " + ligneOperation.getLibelle(), 
				TypeOperationEnum.CREDIT, 
				Double.toString(Math.abs(ligneOperation.getValeur())), 
				etatDepenseTransfert, 
				ligneOperation.isPeriodique());

		createOrUpdateOperation(idBudgetDestination, ligneTransfert, userSession);
		/**
		 *  Ajout de la ligne dans le budget courant
		 */
		ligneOperation.setLibelle("[vers "+compteCible.getLibelle()+"] " + ligneOperation.getLibelle());
		return createOrUpdateOperation(idBudget, ligneOperation, userSession);

	}



	/**
	 * Suppression d'une opération
	 * @param idBudget identifiant de budget
	 * @param ligneOperation ligne opération
	 * @param UserBusinessSession userSession
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 * @throws CompteClosedException compte clos
	 */
	public BudgetMensuel deleteOperation(String idBudget, String idOperation, UserBusinessSession userSession) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		try {
			BudgetMensuel budget = chargerBudgetMensuel(idBudget, userSession);
			if(budget.isActif() && budget.getCompteBancaire().isActif()){
				// Si suppression d'une opération, on l'enlève
				boolean maj = budget.getListeOperations().removeIf(op -> op.getId().equals(idOperation));
				if(maj) {
					LOGGER.info("Suppression d'une Opération : {}", idOperation);
					return calculEtSauvegardeBudget(budget);
				}
				else {
					LOGGER.warn("[idBudget={}][idOperation={}] Impossible de suppression une opération. Introuvable", idBudget, idOperation);
					return null;
				}
			}
			else{
				throw new CompteClosedException("Impossible de modifier ou créer une opération. Le compte est cloturé");
			}
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors de la suppression", e);
			return null;
		}

	}

	/**
	 * Mise à jour d'une ligne de dépense 
	 * @param idBudget identifiant de budget
	 * @param ligneOperation ligne de dépense
	 * @param UserBusinessSession userSession
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 * @throws CompteClosedException compte clos
	 */
	public BudgetMensuel createOrUpdateOperation(String idBudget, LigneOperation ligneOperation, UserBusinessSession userSession) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		BudgetMensuel budget = chargerBudgetMensuel(idBudget, userSession);
		if(budget != null && budget.getCompteBancaire().isActif()){
			// Si mise à jour d'une opération, on l'enlève
			int rangMaj = budget.getListeOperations().indexOf(ligneOperation);
			budget.getListeOperations().removeIf(op -> op.getId().equals(ligneOperation.getId()));
			if(ligneOperation.getEtat() != null) {
				String actionMessage = rangMaj > -1 ? "Mise à jour" : "Ajout";
				LOGGER.info("{} d'une Opération : {}",actionMessage , ligneOperation);
				ligneOperation.setDateMaj(Calendar.getInstance().getTime());
				ligneOperation.setAuteur(userSession.getUtilisateur().getLibelle());
				if(EtatOperationEnum.REALISEE.equals(ligneOperation.getEtat())) {
					ligneOperation.setDateOperation(Calendar.getInstance().getTime());
				}
				else {
					ligneOperation.setDateOperation(null);
				}
				if(rangMaj >= 0) {
					LOGGER.debug("Intégration de l'opération {} dans le budget {}", ligneOperation, budget);
					budget.getListeOperations().add(rangMaj, ligneOperation);
				}
				else {
					LOGGER.debug("Ajout de l'opération {} dans le budget {}", ligneOperation, budget);
					budget.getListeOperations().add(ligneOperation);
				}
			}
			else {
				LOGGER.info("Suppression d'une Opération : {}", ligneOperation);
			}
			// Mise à jour du budget
			calculEtSauvegardeBudget(budget);
		}
		else{
			String idCompte = BudgetDataUtils.getCompteFromBudgetId(idBudget);
			LOGGER.warn("Impossible de modifier ou créer une opération. Le compte {} est cloturé", idCompte);
			throw new CompteClosedException("Impossible de modifier ou créer une opération. Le compte "+idCompte+ " est cloturé");
		}
		return budget;
	}


	/**
	 * Mise à jour de la ligne comme dernière opération
	 * @param ligneId
	 */
	public boolean setLigneAsDerniereOperation(String idBudget, String ligneId, UserBusinessSession userSession) throws UserNotAuthorizedException{
		try {
			BudgetMensuel budget = chargerBudgetMensuel(idBudget, userSession);
			if(budget.getListeOperations() != null && !budget.getListeOperations().isEmpty()) {
				LOGGER.info("[idBudget={}][idOperation={}] Tag de la ligne comme dernière opération", idBudget, ligneId);
				budget.getListeOperations()
				.parallelStream()
				.forEach(op -> op.setDerniereOperation(ligneId.equals(op.getId())));
				// Mise à jour du budget
				budget.setDateMiseAJour(Calendar.getInstance());
				dataDepenses.sauvegardeBudgetMensuel(budget);
				return true;
			}

		} catch (BudgetNotFoundException | DataNotFoundException e) {
			LOGGER.error("Le budget {} est introuvable. Impossible de tagguer l'opération {}", idBudget, ligneId, e);
		} catch (Exception e) {
			LOGGER.error("Erreur lors du tag de la ligne {} dans le budget {}", ligneId, idBudget, e);
		}
		return false;
	}

	/**
	 * Calcul du budget Courant et sauvegarde
	 * @param budget budget à sauvegarder
	 * @throws DataNotFoundException 
	 * @throws BudgetNotFoundException 
	 */
	private BudgetMensuel calculEtSauvegardeBudget(BudgetMensuel budget) {
		budget.setDateMiseAJour(Calendar.getInstance());
		calculBudget(budget);
		dataDepenses.sauvegardeBudgetMensuel(budget);
		return budget;
	}

	/**
	 * Calcul du résumé
	 * @param budgetMensuelCourant
	 */
	protected void calculBudget(BudgetMensuel budget){

		LOGGER.info("(Re)Calcul du budget : {}", budget);
		budget.razCalculs();

		for (LigneOperation operation : budget.getListeOperations()) {
			LOGGER.trace("     > {}", operation);
			Double valeurOperation = operation.getValeur();
			/*
			 * #121 : La réserve n'est pas une véritable opération. Elle n'est pas prise en compte dans les calculs 
			 */
			if(!IdsCategoriesEnum.RESERVE.getId().equals(operation.getIdSsCategorie())){
				/**
				 *  Calcul par catégorie
				 */
				if(operation.getIdCategorie() != null) {
					Double[] valeursCat = {0D,0D};
					if(budget.getTotalParCategories().get(operation.getIdCategorie()) != null){
						valeursCat = budget.getTotalParCategories().get(operation.getIdCategorie());
					}
					if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
						valeursCat[0] = valeursCat[0] + valeurOperation;
						valeursCat[1] = valeursCat[1] + valeurOperation;
					}
					else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
						valeursCat[1] = valeursCat[1] + valeurOperation;
					}
					budget.getTotalParCategories().put(operation.getIdCategorie(), valeursCat);
				}
				/**
				 *  Calcul par sous catégorie
				 */
				if(operation.getIdSsCategorie() != null) {
					Double[] valeurSsCat = {0D,0D};
					if( budget.getTotalParSSCategories().get(operation.getIdSsCategorie()) != null){
						valeurSsCat = budget.getTotalParSSCategories().get(operation.getIdSsCategorie());
					}
					if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
						valeurSsCat[0] = valeurSsCat[0] + valeurOperation;
						valeurSsCat[1] = valeurSsCat[1] + valeurOperation;
					}
					if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
						valeurSsCat[1] = valeurSsCat[1] + valeurOperation;
					}
					budget.getTotalParSSCategories().put(operation.getIdSsCategorie(), valeurSsCat);
				}
				/**
				 * Calcul des totaux
				 */
				if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
					budget.ajouteASoldeNow(valeurOperation);
					budget.ajouteASoldeFin(valeurOperation);
				}
				else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
					budget.ajouteASoldeFin(valeurOperation);
				}
			}


		}
		LOGGER.debug("Solde prévu		| {}	| {}", budget.getSoldeNow(), budget.getSoldeFin());
		LOGGER.debug("Solde réel (+ marge)	| {}	| {}",  budget.getSoldeNow() + budget.getMarge(), budget.getSoldeFin() + budget.getMarge());
	}

	/**
	 * Lock/unlock d'un budget
	 * @param budgetActif etat du budget
	 * @throws BudgetNotFoundException  erreur budget introuvable
	 */
	public BudgetMensuel setBudgetActif(String idBudgetMensuel, boolean budgetActif, UserBusinessSession userSession) throws BudgetNotFoundException{
		LOGGER.info("{} du budget {} de {}", budgetActif ? "Réouverture" : "Fermeture", idBudgetMensuel, userSession);
		if(userSession != null){
			BudgetMensuel budgetMensuel = dataDepenses.chargeBudgetMensuelById(idBudgetMensuel);
			budgetMensuel.setActif(budgetActif);
			budgetMensuel.setDateMiseAJour(Calendar.getInstance());
			//  #119 #141 : Toutes les opérations en attente sont reportées
			if(!budgetActif){		
				budgetMensuel.getListeOperations()
				.stream()
				.filter(op -> EtatOperationEnum.PREVUE.equals(op.getEtat()))
				.forEach(op -> op.setEtat(EtatOperationEnum.REPORTEE));
			}
			return calculEtSauvegardeBudget(budgetMensuel);
		}
		return null;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Opérations");
	}
	

	/**
	 * @param dataDepenses the dataDepenses to set
	 */
	protected void setDataDepenses(BudgetDatabaseService dataDepenses) {
		this.dataDepenses = dataDepenses;
	}
}
