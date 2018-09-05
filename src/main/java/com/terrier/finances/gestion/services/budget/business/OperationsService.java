package com.terrier.finances.gestion.services.budget.business;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.enums.IdsCategoriesEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;
import com.terrier.finances.gestion.communs.utils.data.DataUtils;
import com.terrier.finances.gestion.communs.utils.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.communs.utils.exception.CompteClosedException;
import com.terrier.finances.gestion.communs.utils.exception.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.budget.model.transformer.DataTransformerLigneDepense;
import com.terrier.finances.gestion.services.communs.abstrait.AbstractBusinessService;
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

	private DataTransformerLigneDepense transformer = new DataTransformerLigneDepense();


	
	
	/**
	 * Chargement du budget du mois courant
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public BudgetMensuel chargerBudgetMensuel(String idUtilisateur, CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget {} de {}/{}", compte, mois, annee);

		CompteBancaire compteBancaire = getServiceUtilisateurs().getCompteById(compte.getId(), idUtilisateur);
		if(compteBancaire != null){
			if(compteBancaire.isActif()){
				try {
					return chargerBudgetMensuelSurCompteActif(idUtilisateur, compteBancaire, mois, annee);
				} catch (CompteClosedException e) {
					// Rien car géré en aval
				}
			}
			else{
				return chargerBudgetMensuelSurCompteInactif(idUtilisateur, compteBancaire, mois, annee);
			}
		}
		throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(compte).append(" de ").append(idUtilisateur));
	}


	/**
	 * Chargement du budget du mois courant pour le compte actif
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private BudgetMensuel chargerBudgetMensuelSurCompteActif(String idUtilisateur, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte actif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BasicTextEncryptor encryptor = getBusinessSession(idUtilisateur).getEncryptor();

		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee, encryptor);
		}
		catch(BudgetNotFoundException e){
			budgetMensuel = initNewBudget(compteBancaire, idUtilisateur, mois, annee);
		}
		// Maj du budget ssi budget actif
		if(budgetMensuel != null && budgetMensuel.isActif()){
			// Recalcul du résultat du mois précédent
			try{
				Month moisPrecedent = mois.minus(1);
				int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
				LOGGER.debug("Chargement du budget du mois précédent du compte actif {} : {}/{}", compteBancaire.getId(), moisPrecedent, anneePrecedente);
				BudgetMensuel budgetPrecedent = this.dataDepenses.chargeBudgetMensuel(compteBancaire, moisPrecedent, anneePrecedente, encryptor);
				if(budgetPrecedent.isActif()){
					calculBudget(budgetPrecedent);
				}
				budgetMensuel.setResultatMoisPrecedent(budgetPrecedent.getSoldeFin(), budgetPrecedent.getMarge());
			}
			catch(BudgetNotFoundException e){
				LOGGER.error("Le budget précédent celui de [{}/{}] est introuvable", mois, annee);
			}
			// Résultat mensuel mis à jour
			calculEtSauvegardeBudget(budgetMensuel, idUtilisateur);
			// Ajout de l'autocomplete
			budgetMensuel.getSetLibellesDepensesForAutocomplete().addAll(this.dataDepenses.chargeLibellesDepenses(compteBancaire.getId(), annee, encryptor));
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
	private BudgetMensuel chargerBudgetMensuelSurCompteInactif(String idUtilisateur, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte inactif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BudgetMensuel budgetMensuel = null;
		BasicTextEncryptor encryptor = getBusinessSession(idUtilisateur).getEncryptor();
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee, encryptor);
		}
		catch(BudgetNotFoundException e){
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			budgetMensuel = chargerBudgetMensuelSurCompteInactif(idUtilisateur, compteBancaire, moisPrecedent, anneePrecedente);
		}
		// On reporte l'état inactif du compte sur les anciens budgets
		budgetMensuel.setCompteBancaire(compteBancaire);
		// L'état du budget est forcé à inactif
		budgetMensuel.setActif(false);
		return budgetMensuel;
	}

	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param compte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public LocalDate[] getDatePremierDernierBudgets(String compte) throws DataNotFoundException{

		BudgetMensuelDTO[] premierDernierBudgets = this.dataDepenses.getDatePremierDernierBudgets(compte);

		LocalDate premier = DataUtils.localDateFirstDayOfMonth();
		if(premierDernierBudgets[0] != null){
			premier = premier.with(ChronoField.MONTH_OF_YEAR, premierDernierBudgets[0].getMois() + 1L).with(ChronoField.YEAR, premierDernierBudgets[0].getAnnee());
		}
		LocalDate dernier = DataUtils.localDateFirstDayOfMonth();
		if(premierDernierBudgets[1] != null){
			dernier = dernier.with(ChronoField.MONTH_OF_YEAR, premierDernierBudgets[1].getMois() + 1L).with(ChronoField.YEAR, premierDernierBudgets[1].getAnnee()).plusMonths(1);
		}
		return new LocalDate[]{premier, dernier};
	}

	/**
	 * Charge la date de mise à jour du budget
	 * @param idBudget identifiant du budget
	 * @return la date de mise à jour du  budget
	 */
	public boolean isBudgetUpToDate(String idBudget, Calendar dateToCompare, String utilisateur) {

		BasicTextEncryptor encryptor = getBusinessSession(utilisateur).getEncryptor();
		Date dateEnBDD = this.dataDepenses.getDateMiseAJourBudget(idBudget, encryptor);
		if(dateEnBDD != null){
			return dateToCompare.getTime().before(dateEnBDD);
		}
		else{
			LOGGER.error("[REFRESH] Impossible de trouver la date de mise à jour du budget. Annulation du traitement de rafraichissement");
			return false;
		}
	}


	/**
	 * Chargement de l'état du budget du mois courant en consultation
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	public boolean isBudgetMensuelActif(CompteBancaire compte, Month mois, int annee){
		return this.dataDepenses.isBudgetActif(compte, mois, annee);
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
	private BudgetMensuel initNewBudget(CompteBancaire compteBancaire, String idUtilisateur, Month mois, int annee) throws BudgetNotFoundException,CompteClosedException, DataNotFoundException{
		LOGGER.info("[INIT] Initialisation du budget {} de {}/{}", compteBancaire.getLibelle(), mois, annee);
		BudgetMensuel budget = new BudgetMensuel();
		budget.setNewBudget(true);
		budget.setActif(true);
		budget.setAnnee(annee);
		budget.setMois(mois);
		budget.setCompteBancaire(compteBancaire);
		budget.setDateMiseAJour(Calendar.getInstance());
		// Init si dans le futur par rapport au démarrage
		LocalDate datePremierBudget = getDatePremierDernierBudgets(compteBancaire.getId())[0].with(ChronoField.DAY_OF_MONTH, 1);

		LocalDate dateCourante = DataUtils.localDateFirstDayOfMonth(mois, annee);

		if(dateCourante.isAfter(datePremierBudget)){
			// MAJ Calculs à partir du mois précédent
			// Mois précédent
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			// Recherche du budget précédent 
			// Si impossible : BudgetNotFoundException
			BudgetMensuel budgetPrecedent = chargerBudgetMensuel(idUtilisateur, compteBancaire, moisPrecedent, anneePrecedente);
			// #115 : Cloture automatique du mois précédent
			setBudgetActif(budgetPrecedent, false, idUtilisateur);
			
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
		BasicTextEncryptor encryptor = getBusinessSession(idUtilisateur).getEncryptor();
		String idBudget = this.dataDepenses.sauvegardeBudgetMensuel(budget, encryptor);
		if(idBudget != null){
			budget.setId(idBudget);
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
	public BudgetMensuel reinitialiserBudgetMensuel(BudgetMensuel budgetMensuel, String idUtilisateur) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{

		CompteBancaire compteBancaire = getServiceUtilisateurs().getCompteById(budgetMensuel.getCompteBancaire().getId(), idUtilisateur);
		if(compteBancaire != null){
			// S'il y a eu cloture, on ne fait rien
			return initNewBudget(compteBancaire, idUtilisateur, budgetMensuel.getMois(), budgetMensuel.getAnnee());
		}
		else{
			throw new DataNotFoundException("Le compte bancaire " + budgetMensuel.getCompteBancaire().getId() + " est introuvable");
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
			throw new CompteClosedException(new StringBuilder().append("Impossible d'initialiser un nouveau budget. Le compte est cloturé"));
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
	public BudgetMensuel ajoutLigneTransfertIntercompte(String idBudget, LigneOperation ligneOperation, CompteBancaire compteCrediteur, String idUtilisateur) throws BudgetNotFoundException, DataNotFoundException, CompteClosedException{

		UserBusinessSession userSession = getBusinessSession(idUtilisateur);
		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, userSession.getEncryptor());
		/**
		 *  Si transfert intercompte : Création d'une ligne dans le compte distant
		 */
		BudgetMensuel budgetTransfert = chargerBudgetMensuel(idUtilisateur, compteCrediteur, budget.getMois(), budget.getAnnee());
		if(budget.getCompteBancaire().isActif() && budgetTransfert.getCompteBancaire().isActif() && budget.isActif() && budgetTransfert.isActif()){
			LOGGER.info("Ajout d'un transfert intercompte de {} vers {} > {} ", budget.getCompteBancaire().getLibelle(), compteCrediteur, ligneOperation);

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

			LigneOperation ligneTransfert = new LigneOperation(
					ligneOperation.getSsCategorie(), 
					"[de "+budget.getCompteBancaire().getLibelle()+"] " + ligneOperation.getLibelle(), 
					TypeOperationEnum.CREDIT, 
					Double.toString(Math.abs(ligneOperation.getValeur())), 
					etatDepenseTransfert, 
					ligneOperation.isPeriodique());

			ajoutOperation(budgetTransfert, ligneTransfert, userSession.getUtilisateur());
			calculEtSauvegardeBudget(budgetTransfert, idUtilisateur);
			/**
			 *  Ajout de la ligne dans le budget courant
			 */
			ligneOperation.setLibelle("[vers "+budgetTransfert.getCompteBancaire().getLibelle()+"] " + ligneOperation.getLibelle());
			return ajoutOperationEtCalcul(idBudget, ligneOperation, idUtilisateur);
		}
		else{
			throw new CompteClosedException(new StringBuilder("Impossible d'ajouter une opération de transfert intercompte : L'un des deux comptes est cloturé"));
		}
	}



	/**
	 * Ajout d'une ligne de dépense
	 * @param ligneOperation ligne de dépense
	 * @param auteur auteur de l'action
	 */
	private void ajoutOperation(BudgetMensuel budget, LigneOperation ligneOperation, Utilisateur auteur){
		LOGGER.info("Ajout d'une Opération : {}", ligneOperation);
		ligneOperation.setAuteur(auteur.getLibelle());
		ligneOperation.setDateMaj(Calendar.getInstance().getTime());
		budget.getListeOperations().add(ligneOperation);
		budget.setDateMiseAJour(Calendar.getInstance());
	}


	/**
	 * Ajout d'une ligne de dépense
	 * @param ligneOperation ligne de dépense
	 * @param idUtilisateur auteur de l'action 
	 * @throws BudgetNotFoundException 
	 */
	public BudgetMensuel ajoutOperationEtCalcul(String idBudget, LigneOperation ligneOperation, String idUtilisateur) throws BudgetNotFoundException{
		UserBusinessSession userSession = getBusinessSession(idUtilisateur);
		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, userSession.getEncryptor());
		if(budget.isActif() && budget.getCompteBancaire().isActif()){
			ajoutOperation(budget, ligneOperation, userSession.getUtilisateur());
			// Résultat mensuel
			budget = calculEtSauvegardeBudget(budget, idUtilisateur);
		}
		else{
			LOGGER.warn("Impossible créer une nouvelle opération. Le compte est cloturé");
		}
		return budget;
	}

	/**
	 * @param ligneId
	 * @return {@link LigneDepense} correspondance
	 */
	private LigneOperation getLigneOperation(BudgetMensuel budget, String ligneId){
		// Recherche de la ligne
		Optional<LigneOperation> ligneDepense = 
				budget.getListeOperations()
				.parallelStream()
				.filter(ligne -> ligne.getId().equals(ligneId))
				.findFirst();
		if(ligneDepense.isPresent()){
			return ligneDepense.get();
		}
		return null;
	}

	/**
	 * Mise à jour d'une ligne de dépense 
	 * @param idBudget identifiant de budget
	 * @param ligneDepense ligne de dépense
	 * @param idUtilisateur auteur
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 */
	public BudgetMensuel majLigneDepense(String idBudget, LigneOperation ligneDepense, String idUtilisateur) throws DataNotFoundException, BudgetNotFoundException{

		boolean ligneupdated = false;
		if(ligneDepense != null){
			UserBusinessSession auteur = getBusinessSession(idUtilisateur);
			BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, auteur.getEncryptor());
			if(ligneDepense.getEtat() == null){
				for (Iterator<LigneOperation> iterator = budget.getListeOperations().iterator(); iterator
						.hasNext();) {
					LigneOperation type = iterator.next();
					if(type.getId().equals(ligneDepense.getId())){
						iterator.remove();
						ligneupdated = true;
					}
				}
			}
			else{
				ligneDepense.setDateMaj(Calendar.getInstance().getTime());
				ligneDepense.setAuteur(auteur.getUtilisateur().getLibelle());
				// Mise à jour de la ligne de dépense
				for (int i = 0; i < budget.getListeOperations().size(); i++) {
					if(budget.getListeOperations().get(i).getId().equals(ligneDepense.getId())){
						budget.getListeOperations().set(i, ligneDepense);
						ligneupdated = true;
						break;
					}
				}
			}
			if(ligneupdated){
				// Mise à jour du budget
				budget.setDateMiseAJour(Calendar.getInstance());
				// Budget
				budget = calculEtSauvegardeBudget(budget, idUtilisateur);
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
	 * Mise à jour de la ligne de dépense du budget
	 * @param ligneId ligne à modifier
	 * @param etat état de la ligne
	 * @param auteur auteur de l'action
	 * @throws DataNotFoundException erreur ligne non trouvé
	 * @throws BudgetNotFoundException not found
	 */
	public BudgetMensuel majEtatLigneOperation(BudgetMensuel budget, String ligneId, EtatOperationEnum etat, String idUtilisateur) throws DataNotFoundException, BudgetNotFoundException{
		LigneOperation ligneOperation = getLigneOperation(budget, ligneId);
		// Mise à jour de l'état
		if(ligneOperation != null){
			ligneOperation.setEtat(etat);
			if(EtatOperationEnum.REALISEE.equals(etat)){
				ligneOperation.setDateOperation(Calendar.getInstance().getTime());
			}
			else{
				ligneOperation.setDateOperation(null);
			}
			// Mise à jour de la ligne
			ligneOperation.setDateMaj(Calendar.getInstance().getTime());
			ligneOperation.setAuteur(getBusinessSession(idUtilisateur).getUtilisateur().getLibelle());
			budget = majLigneDepense(budget.getId(), ligneOperation, idUtilisateur);
		}
		return budget;
	}	


	/**
	 * Mise à jour de la ligne comme dernière opération
	 * @param ligneId
	 */
	public void setLigneDepenseAsDerniereOperation(BudgetMensuel budget, String ligneId, String idUtilisateur){
		for (LigneOperation ligne : budget.getListeOperations()) {
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
		dataDepenses.sauvegardeBudgetMensuel(budget, getBusinessSession(idUtilisateur).getEncryptor());
	}

	/**
	 * Calcul du budget Courant et sauvegarde
	 * @param budget budget à sauvegarder
	 */
	public BudgetMensuel calculEtSauvegardeBudget(BudgetMensuel budget, String utilisateur){
		calculBudget(budget);

		dataDepenses.sauvegardeBudgetMensuel(budget, getBusinessSession(utilisateur).getEncryptor());
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
			budget.getSetLibellesDepensesForAutocomplete().add(operation.getLibelle());
			
			/*
			 * #121 : La réserve n'est pas une véritable opération. Elle n'est pas prise en compte dans les calculs 
			 */
			if(!IdsCategoriesEnum.RESERVE.getId().equals(operation.getSsCategorie().getId())){
				/**
				 *  Calcul par catégorie
				 */
				Double[] valeursCat = {0D,0D};
				if(budget.getTotalParCategories().get(operation.getCategorie()) != null){
					valeursCat = budget.getTotalParCategories().get(operation.getCategorie());
				}
				if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
					valeursCat[0] = valeursCat[0] + valeurOperation;
					valeursCat[1] = valeursCat[1] + valeurOperation;
				}
				else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
					valeursCat[1] = valeursCat[1] + valeurOperation;
				}
				budget.getTotalParCategories().put(operation.getCategorie(), valeursCat);

				/**
				 *  Calcul par sous catégorie
				 */
				Double[] valeurSsCat = {0D,0D};
				if( budget.getTotalParSSCategories().get(operation.getSsCategorie()) != null){
					valeurSsCat = budget.getTotalParSSCategories().get(operation.getSsCategorie());
				}
				if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
					valeurSsCat[0] = valeurSsCat[0] + valeurOperation;
					valeurSsCat[1] = valeurSsCat[1] + valeurOperation;
				}
				if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
					valeurSsCat[1] = valeurSsCat[1] + valeurOperation;
				}
				budget.getTotalParSSCategories().put(operation.getSsCategorie(), valeurSsCat);
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
		LOGGER.debug("Solde prévu	| {}	| {}", budget.getSoldeNow(), budget.getSoldeFin());
		LOGGER.debug("Solde réel (avec marge)| {}	| {}",  budget.getSoldeNow() + budget.getMarge(), budget.getSoldeFin() + budget.getMarge());
	}

	/**
	 * Lock/unlock d'un budget
	 * @param budgetActif
	 */
	public BudgetMensuel setBudgetActif(BudgetMensuel budgetMensuel, boolean budgetActif, String idUtilisateur){
		LOGGER.info("{} du budget {}/{} de {}", budgetActif ? "Réouverture" : "Fermeture", budgetMensuel.getMois(), budgetMensuel.getAnnee(), budgetMensuel.getCompteBancaire().getLibelle());
		budgetMensuel.setActif(budgetActif);
		budgetMensuel.setDateMiseAJour(Calendar.getInstance());
		//#119 : Toutes les opérations en attente sont annulées
		if(!budgetActif){		
			budgetMensuel.getListeOperations()
				.stream()
				.filter(op -> EtatOperationEnum.PREVUE.equals(op.getEtat()))
				.forEach(op -> op.setEtat(EtatOperationEnum.ANNULEE));
		}
		return calculEtSauvegardeBudget(budgetMensuel, idUtilisateur);
	}


	/**
	 * @param dataDepenses the dataDepenses to set
	 */
	protected void setDataDepenses(BudgetDatabaseService dataDepenses) {
		this.dataDepenses = dataDepenses;
	}
	
	
}
