package com.terrier.finances.gestion.services.budgets.business;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.budget.model.v12.TotauxCategorie;
import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.data.BudgetDataUtils;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.communs.utils.exceptions.UserNotAuthorizedException;
import com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient;
import com.terrier.finances.gestion.services.budgets.api.client.ParametragesAPIClient;
import com.terrier.finances.gestion.services.budgets.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;

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
	private ComptesAPIClient compteClientApi;

	@Autowired
	private ParametragesAPIClient paramClientApi;


	/**
	 * Chargement du budget du mois courant
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public BudgetMensuel chargerBudgetMensuel(String idCompte, Month mois, int annee, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget {} de {}/{}", idCompte, mois, annee);

		CompteBancaire compteBancaire = compteClientApi.getCompteById(idCompte);
		if(compteBancaire != null){
			if(Boolean.TRUE.equals(compteBancaire.isActif())){
				try {
					return chargerBudgetMensuelSurCompteActif(idProprietaire, compteBancaire, mois, annee);
				} catch (CompteClosedException e) {
					// Rien car géré en aval
				}
			}
			else{
				return chargerBudgetMensuelSurCompteInactif(idProprietaire, compteBancaire, mois, annee);
			}
		}
		throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(idCompte).append(" de ").append(idProprietaire).toString());
	}


	/**
	 * Chargement du budget du mois courant pour le compte actif
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private BudgetMensuel chargerBudgetMensuelSurCompteActif(String idProprietaire, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{
		LOGGER.debug("Chargement du budget de {}/{} du compte actif {} ", mois, annee, compteBancaire.getId());

		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee);
		}
		catch(BudgetNotFoundException e){
			budgetMensuel = initNewBudget(compteBancaire, idProprietaire, mois, annee);
		}
		// Maj du budget ssi budget actif
		if(budgetMensuel != null && budgetMensuel.isActif()){
			// Recalcul du résultat du mois précédent
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			try{
				LOGGER.debug("Chargement du budget du mois précédent du compte actif {} : {}/{}", compteBancaire.getId(), moisPrecedent, anneePrecedente);
				BudgetMensuel budgetPrecedent = this.dataDepenses.chargeBudgetMensuel(compteBancaire, moisPrecedent, anneePrecedente);
				budgetMensuel.getSoldes().setSoldeAtFinMoisPrecedent(budgetPrecedent.getSoldes().getSoldeAtFinMoisCourant());
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
	private BudgetMensuel chargerBudgetMensuelSurCompteInactif(String idProprietaire, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte inactif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee);
		}
		catch(BudgetNotFoundException e){
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			budgetMensuel = chargerBudgetMensuelSurCompteInactif(idProprietaire, compteBancaire, moisPrecedent, anneePrecedente);
		}
		// On reporte l'état inactif du compte sur les anciens budgets
		budgetMensuel.setIdCompteBancaire(compteBancaire.getId());
		// L'état du budget est forcé à inactif
		budgetMensuel.setActif(false);
		return budgetMensuel;
	}


	/**
	 * Indique si l'IHM est out of date
	 * @param idBudget identifiant du budget
	 * @param dateSurIHM Date affichée
	 * @return si le budget doit être mis à jour
	 */
	public boolean isBudgetIHMUpToDate(String idBudget, Long dateSurIHM) {

		try {
			BudgetMensuel budgetMensuel =  this.dataDepenses.chargeBudgetMensuel(idBudget);
			if(budgetMensuel != null){
				LOGGER.debug("Budget : Date mise à jour : {} / Date IHM : {}", 
						BudgetDateTimeUtils.getMillisecondsFromLocalDateTime(budgetMensuel.getDateMiseAJour()), dateSurIHM);
				return dateSurIHM >= BudgetDateTimeUtils.getMillisecondsFromLocalDateTime(budgetMensuel.getDateMiseAJour());
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
	private BudgetMensuel initNewBudget(CompteBancaire compteBancaire, String idProprietaire, Month mois, int annee) throws BudgetNotFoundException,CompteClosedException, DataNotFoundException{
		LOGGER.info("Initialisation du budget {} de {}/{}", compteBancaire.getLibelle(), mois, annee);
		BudgetMensuel budget = new BudgetMensuel();
		budget.setActif(true);
		budget.setAnnee(annee);
		budget.setMois(mois);
		budget.setIdCompteBancaire(compteBancaire.getId());

		budget.setNewBudget(true);
		budget.setId();

		budget.setDateMiseAJour(LocalDateTime.now());
		// Init si dans le futur par rapport au démarrage
		LocalDate datePremierBudget;
		try{
			datePremierBudget = getIntervallesBudgets(compteBancaire.getId())[0].with(ChronoField.DAY_OF_MONTH, 1);
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
			BudgetMensuel budgetPrecedent = chargerBudgetMensuel(compteBancaire.getId(), moisPrecedent, anneePrecedente, idProprietaire);
			// #115 : Cloture automatique du mois précédent
			budgetPrecedent = setBudgetActif(budgetPrecedent.getId(), false, idProprietaire);

			initBudgetFromBudgetPrecedent(budget, budgetPrecedent);
		}
		else{
			LOGGER.warn("Le budget {} n'a jamais existé", compteBancaire.getLibelle());
			budget.getSoldes().setSoldeAtFinMoisCourant(0D);
			budget.getSoldes().setSoldeAtMaintenant(0D);
			budget.getSoldes().setSoldeAtFinMoisPrecedent(0D);
			budget.setListeOperations(new ArrayList<>());
		}

		LOGGER.info("Sauvegarde du nouveau budget {}", budget);
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
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param idCompte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public LocalDate[] getIntervallesBudgets(String idCompte) throws DataNotFoundException{

		BudgetMensuel[] premierDernierBudgets = this.dataDepenses.getPremierDernierBudgets(idCompte);
		if(premierDernierBudgets != null && premierDernierBudgets.length >= 2){
			LocalDate premier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
			if(premierDernierBudgets[0] != null){
				premier = premier.withMonth(premierDernierBudgets[0].getMois().getValue()).withYear(premierDernierBudgets[0].getAnnee());
			}
			LocalDate dernier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
			if(premierDernierBudgets[1] != null){
				dernier = dernier.withMonth(premierDernierBudgets[1].getMois().getValue()).withYear(premierDernierBudgets[1].getAnnee());
			}
			return new LocalDate[]{premier, dernier};
		}
		else{
			throw new DataNotFoundException("Données introuvables pour le compte " + idCompte);
		}
	}

	/**
	 * @param idProprietaire
	 * @param idBudget
	 * @return budget correspondant aux paramètres
	 * @throws UserNotAuthorizedException utilisateur non autorisé
	 * @throws BudgetNotFoundException budget introuvable
	 * @throws DataNotFoundException données introuvables
	 */
	public BudgetMensuel chargerBudgetMensuel(String idBudget, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException{
		return chargerBudgetMensuel(BudgetDataUtils.getCompteFromBudgetId(idBudget), BudgetDataUtils.getMoisFromBudgetId(idBudget), BudgetDataUtils.getAnneeFromBudgetId(idBudget), idProprietaire);
	}

	/**
	 * Réinitialiser un budget mensuel
	 * @param idBudget budget mensuel
	 * @param idProprietaire propriétaire du budget
	 * @throws DataNotFoundException  erreur sur les données
	 * @throws BudgetNotFoundException budget introuvable
	 */
	public BudgetMensuel reinitialiserBudgetMensuel(String idBudget, String idProprietaire) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{

		BudgetMensuel budgetMensuel = chargerBudgetMensuel(idBudget, idProprietaire);
		if(budgetMensuel != null){
			CompteBancaire compteBancaire = compteClientApi.getCompteById(budgetMensuel.getIdCompteBancaire());
			if(compteBancaire != null){
				// S'il y a eu cloture, on ne fait rien
				return initNewBudget(compteBancaire, idProprietaire, budgetMensuel.getMois(), budgetMensuel.getAnnee());
			}
			else{
				throw new DataNotFoundException("Le compte bancaire " + budgetMensuel.getIdCompteBancaire() + " est introuvable");
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
		if(true) { // TODO : budget.getCompteBancaire().isActif() && budgetPrecedent.getCompteBancaire().isActif()){
			calculBudget(budgetPrecedent);
			budget.setIdCompteBancaire(budgetPrecedent.getIdCompteBancaire());
			// #116 : Le résultat du moins précédent est le compte réel, pas le compte avancé
			budget.getSoldes().setSoldeAtFinMoisPrecedent(budgetPrecedent.getSoldes().getSoldeAtFinMoisCourant());
			budget.setDateMiseAJour(LocalDateTime.now());
			if(budgetPrecedent.getListeOperations() != null){

				// Recopie de toutes les opérations périodiques, et reportées
				budget.getListeOperations().addAll(
						budgetPrecedent.getListeOperations()
						.stream()
						.filter(op -> op.isPeriodique() || EtatOperationEnum.REPORTEE.equals(op.getEtat()))
						.map(BudgetDataUtils::cloneDepenseToMoisSuivant)
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
	 * @throws CompteClosedException  compte clos
	 */
	public BudgetMensuel createOperationIntercompte(String idBudget, LigneOperation ligneOperation, String idCompteDestination, String idProprietaire) throws BudgetNotFoundException, DataNotFoundException, CompteClosedException{

		/**
		 *  Si transfert intercompte : Création d'une ligne dans le compte distant
		 */
		String idBudgetDestination = BudgetDataUtils.getBudgetId(idCompteDestination, BudgetDataUtils.getMoisFromBudgetId(idBudget), BudgetDataUtils.getAnneeFromBudgetId(idBudget));
		LOGGER.info("Ajout d'un transfert intercompte de {} vers {} ({}) > {} ", idBudget, idBudgetDestination, idCompteDestination, ligneOperation);
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

		CompteBancaire compteSource = this.compteClientApi.getCompteById(idCompteSource);
		CompteBancaire compteCible = this.compteClientApi.getCompteById(idCompteDestination);

		LigneOperation ligneTransfert = new LigneOperation(
				ligneOperation.getCategorie(),
				ligneOperation.getSsCategorie(), 
				"[de "+compteSource.getLibelle()+"] " + ligneOperation.getLibelle(), 
				TypeOperationEnum.CREDIT, 
				Math.abs(ligneOperation.getValeur()), 
				etatDepenseTransfert, 
				ligneOperation.isPeriodique());

		updateOperationInBudget(idBudgetDestination, ligneTransfert, idProprietaire);
		/**
		 *  Ajout de la ligne dans le budget courant
		 */
		ligneOperation.setLibelle("[vers "+compteCible.getLibelle()+"] " + ligneOperation.getLibelle());
		return updateOperationInBudget(idBudget, ligneOperation, idProprietaire);

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
	public BudgetMensuel deleteOperation(String idBudget, String idOperation, String idProprietaire) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{
		try {
			BudgetMensuel budget = chargerBudgetMensuel(idBudget, idProprietaire);
			if(Boolean.TRUE.equals(budget.isActif())) { // TODO : && Boolean.TRUE.equals(budget.getCompteBancaire().isActif())){
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
	public BudgetMensuel updateOperationInBudget(String idBudget, final LigneOperation ligneOperation, String idProprietaire) throws DataNotFoundException, BudgetNotFoundException, CompteClosedException{

		BudgetMensuel budget = chargerBudgetMensuel(idBudget, idProprietaire);
		if(budget != null ){  // TODO : && budget.getCompteBancaire().isActif()
			// Si mise à jour d'une opération, on l'enlève
			int rangMaj = budget.getListeOperations().indexOf(ligneOperation);
			budget.getListeOperations().removeIf(op -> op.getId().equals(ligneOperation.getId()));
			if(ligneOperation.getEtat() != null) {

				LigneOperation ligneUpdatedOperation = updateOperation(ligneOperation, idProprietaire);
				if(rangMaj >= 0) {
					LOGGER.debug("Mise à jour de l'opération {} dans le budget {}", ligneUpdatedOperation, budget);
					budget.getListeOperations().add(rangMaj, ligneUpdatedOperation);
				}
				else {
					LOGGER.debug("Ajout de l'opération {} dans le budget {}", ligneUpdatedOperation, budget);
					budget.getListeOperations().add(ligneUpdatedOperation);

					// Si frais remboursable : ajout du remboursement en prévision
					// #62 : et en mode création
					if(ligneOperation.getSsCategorie() != null
							&& ligneOperation.getCategorie() != null 
							&& IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId().equals(ligneOperation.getCategorie().getId())){
						budget.getListeOperations().add(addOperationRemboursement(ligneOperation, idProprietaire));
					}
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
	 * @param ligneOperation opération
	 * @param idProprietaire idPropriétaire
	 * @param rangMaj rang de mise à jour
	 * @return ligneOperation màj
	 */
	private LigneOperation updateOperation(LigneOperation ligneOperation, String nomProprietaire) {
		ligneOperation.getAutresInfos().setDateMaj(LocalDateTime.now());
		ligneOperation.getAutresInfos().setAuteur(nomProprietaire);
		if(EtatOperationEnum.REALISEE.equals(ligneOperation.getEtat())) {
			ligneOperation.getAutresInfos().setDateOperation(LocalDateTime.now());
		}
		else {
			ligneOperation.getAutresInfos().setDateOperation(null);
		}
		return ligneOperation;
	}

	/**
	 * @param ligneOperation
	 * @param idProprietaire
	 * @return ligne de remboursement
	 */
	private LigneOperation addOperationRemboursement(LigneOperation ligneOperation, String idProprietaire) {

		LigneOperation ligneRemboursement = new LigneOperation(
				paramClientApi.getCategorieParId(IdsCategoriesEnum.REMBOURSEMENT.getId()), 
				"[Remboursement] " + ligneOperation.getLibelle(), 
				TypeOperationEnum.CREDIT, 
				Math.abs(ligneOperation.getValeur()), 
				EtatOperationEnum.REPORTEE, 
				ligneOperation.isPeriodique());
		ligneRemboursement.getAutresInfos().setAuteur(idProprietaire);
		ligneRemboursement.getAutresInfos().setDateMaj(LocalDateTime.now());
		return ligneRemboursement;
	}

	/**
	 * Mise à jour de la ligne comme dernière opération
	 * @param ligneId
	 */
	public boolean setLigneAsDerniereOperation(String idBudget, String ligneId, String idProprietaire) {
		try {
			BudgetMensuel budget = chargerBudgetMensuel(idBudget, idProprietaire);
			if(budget.getListeOperations() != null && !budget.getListeOperations().isEmpty()) {
				LOGGER.info("[idBudget={}][idOperation={}] Tag de la ligne comme dernière opération", idBudget, ligneId);
				budget.getListeOperations()
				.parallelStream()
				.forEach(op -> op.setTagDerniereOperation(ligneId.equals(op.getId())));
				// Mise à jour du budget
				budget.setDateMiseAJour(LocalDateTime.now());
				return dataDepenses.sauvegardeBudgetMensuel(budget) != null;
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
		budget.setDateMiseAJour(LocalDateTime.now());
		calculBudget(budget);
		dataDepenses.sauvegardeBudgetMensuel(budget);
		return budget;
	}


	/**
	 * Calcul du total de la catégorie du budget via l'opération en cours
	 * @param budget budget à calculer
	 * @param operation opération à traiter
	 */
	private void calculBudgetTotalCategories(BudgetMensuel budget, LigneOperation operation) {

		if(operation.getCategorie() != null) {
			Double valeurOperation = operation.getValeur();
			TotauxCategorie valeursCat = new TotauxCategorie();
			if(budget.getTotauxParCategories().get(operation.getCategorie().getId()) != null){
				valeursCat = budget.getTotauxParCategories().get(operation.getCategorie().getId());
			}
			valeursCat.setLibelleCategorie(operation.getCategorie().getLibelle());
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				valeursCat.ajouterATotalAtMaintenant(valeurOperation);
				valeursCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				valeursCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			budget.getTotauxParCategories().put(operation.getCategorie().getId(), valeursCat);
		}
	}

	/**
	 * Calcul du total de la sous catégorie du budget via l'opération en cours
	 * @param budget budget à calculer
	 * @param operation opération à traiter
	 * 
	 * */
	private void calculBudgetTotalSsCategories(BudgetMensuel budget, LigneOperation operation) {
		if(operation.getSsCategorie() != null) {
			Double valeurOperation = operation.getValeur();
			TotauxCategorie valeursSsCat = new TotauxCategorie();
			if( budget.getTotauxParSSCategories().get(operation.getSsCategorie().getId()) != null){
				valeursSsCat = budget.getTotauxParSSCategories().get(operation.getSsCategorie().getId());
			}
			valeursSsCat.setLibelleCategorie(operation.getSsCategorie().getLibelle());
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				valeursSsCat.ajouterATotalAtMaintenant(valeurOperation);
				valeursSsCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				valeursSsCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			budget.getTotauxParSSCategories().put(operation.getSsCategorie().getId(), valeursSsCat);
		}
	}

	/**
	 * Calcul du résumé
	 * @param budgetMensuelCourant
	 */
	protected void calculBudget(BudgetMensuel budget){

		LOGGER.info("(Re)Calcul du budget : {}", budget);
		BudgetDataUtils.razCalculs(budget);

		for (LigneOperation operation : budget.getListeOperations()) {
			LOGGER.trace("     > {}", operation);
			Double valeurOperation = operation.getValeur();

			// Calcul par catégorie
			calculBudgetTotalCategories(budget, operation);
			// Calcul par sous catégorie
			calculBudgetTotalSsCategories(budget, operation);
			// Calcul des totaux
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				BudgetDataUtils.ajouteASoldeNow(budget, valeurOperation);
				BudgetDataUtils.ajouteASoldeFin(budget, valeurOperation);
			}
			else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				BudgetDataUtils.ajouteASoldeFin(budget, valeurOperation);
			}
		}
		LOGGER.debug("Solde prévu\t| {}\t| {}", budget.getSoldes().getSoldeAtMaintenant(), budget.getSoldes().getSoldeAtFinMoisCourant());
	}

	/**
	 * Lock/unlock d'un budget
	 * @param budgetActif etat du budget
	 * @throws BudgetNotFoundException  erreur budget introuvable
	 */
	public BudgetMensuel setBudgetActif(String idBudgetMensuel, boolean budgetActif, String idProprietaire) throws BudgetNotFoundException{
		LOGGER.info("{} du budget {} de {}", budgetActif ? "Réouverture" : "Fermeture", idBudgetMensuel, idProprietaire);
		if(idProprietaire != null){
			BudgetMensuel budgetMensuel = dataDepenses.chargeBudgetMensuel(idBudgetMensuel);
			budgetMensuel.setActif(budgetActif);
			budgetMensuel.setDateMiseAJour(LocalDateTime.now());
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


	/**
	 * Réinjection des catégories dans les opérations du budget
	 * @param budget
	 */
	public void completeCategoriesOnOperation(LigneOperation operation, List<CategorieOperation> categories){
		try {
			CategorieOperation catFound = BudgetDataUtils.getCategorieById(operation.getSsCategorie().getId(), categories);
			if(catFound != null) {
				operation.getSsCategorie().setId(catFound.getId());
				operation.getSsCategorie().setLibelle(catFound.getLibelle());
				operation.getCategorie().setId(catFound.getCategorieParente().getId());
				operation.getCategorie().setLibelle(catFound.getCategorieParente().getLibelle());
				return;
			}
		}
		catch (Exception e) {
			LOGGER.warn("Impossible de retrouver la sous catégorie : {}", operation.getSsCategorie(), e);
		}
		LOGGER.warn("Impossible de retrouver la sous catégorie : {} parmi la liste ci dessous. Le fonctionnement peut être incorrect. \n {}", operation.getSsCategorie(), categories);

	}


	/**
	 * Charge les libelles des opérations
	 * @param idCompte
	 * @param annee
	 * @return liste des libelles opérations
	 */
	public Set<String> getLibellesOperations(String idCompte, int annee){
		return this.dataDepenses.chargeLibellesOperations(idCompte, annee);
	}

	/**
	 * @param dataDepenses the dataDepenses to set
	 */
	protected void setDataDepenses(BudgetDatabaseService dataDepenses) {
		this.dataDepenses = dataDepenses;
	}



	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Opérations");
	}


	/**
	 * @return the compteClientApi
	 */
	public ComptesAPIClient getCompteClientApi() {
		return compteClientApi;
	}


}
