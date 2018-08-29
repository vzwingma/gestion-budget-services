package com.terrier.finances.gestion.budget.business;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.budget.model.transformer.DataTransformerLigneDepense;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.data.DataUtils;
import com.terrier.finances.gestion.model.enums.EtatLigneOperationEnum;
import com.terrier.finances.gestion.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.CompteClosedException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.operations.model.LigneOperation;
import com.terrier.finances.gestion.parametrages.business.ParametragesService;
import com.terrier.finances.gestion.utilisateurs.business.AuthenticationService;
import com.terrier.finances.gestion.utilisateurs.data.UtilisateurDatabaseService;

/**
 * Service Métier : Operations
 * @author vzwingma
 *
 */
@Service
public class OperationsService {


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
	private UtilisateurDatabaseService dataUsers;
	/**
	 * Paramétrages
	 */
	@Autowired
	private ParametragesService serviceParams;
	@Autowired
	private AuthenticationService serviceAuth;

	private DataTransformerLigneDepense transformer = new DataTransformerLigneDepense();
	

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
	public BudgetMensuel chargerBudgetMensuel(Utilisateur utilisateur, CompteBancaire compte, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget {} de {}/{}", compte, mois, annee);

		CompteBancaire compteBancaire = serviceParams.getCompteById(compte.getId(), utilisateur.getLogin());
		if(compteBancaire != null){
			if(compteBancaire.isActif()){
				try {
					return chargerBudgetMensuelSurCompteActif(utilisateur, compteBancaire, mois, annee);
				} catch (CompteClosedException e) {
					// Rien car géré en aval
				}
			}
			else{
				return chargerBudgetMensuelSurCompteInactif(utilisateur, compteBancaire, mois, annee);
			}
		}
		throw new BudgetNotFoundException(new StringBuilder().append("Erreur lors du chargement du compte ").append(compte).append(" de ").append(utilisateur));
	}


	/**
	 * Chargement du budget du mois courant pour le compte actif
	 * @param compte compte 
	 * @param mois mois 
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private BudgetMensuel chargerBudgetMensuelSurCompteActif(Utilisateur utilisateur, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte actif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BasicTextEncryptor encryptor = serviceAuth.getEncryptor(utilisateur);
		
		BudgetMensuel budgetMensuel = null;
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee, encryptor);
		}
		catch(BudgetNotFoundException e){
			budgetMensuel = initNewBudget(compteBancaire, utilisateur, mois, annee);
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
				// TODO : Et si le budget n'est pas actif ? On ne recacule pas ?
				budgetMensuel.setResultatMoisPrecedent(budgetPrecedent.getFinArgentAvance());
			}
			catch(BudgetNotFoundException e){
				LOGGER.error("Le budget précédent celui de [{}/{}] est introuvable", mois, annee);
			}
			// Résultat mensuel mis à jour
			calculEtSauvegardeBudget(budgetMensuel, utilisateur);
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
	private BudgetMensuel chargerBudgetMensuelSurCompteInactif(Utilisateur utilisateur, CompteBancaire compteBancaire, Month mois, int annee) throws BudgetNotFoundException, DataNotFoundException{
		LOGGER.debug("Chargement du budget du compte inactif {} de {}/{}", compteBancaire.getId(), mois, annee);

		BudgetMensuel budgetMensuel = null;
		BasicTextEncryptor encryptor = this.serviceAuth.getEncryptor(utilisateur);
		try{
			budgetMensuel = this.dataDepenses.chargeBudgetMensuel(compteBancaire, mois, annee, encryptor);
		}
		catch(BudgetNotFoundException e){
			Month moisPrecedent = mois.minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;
			budgetMensuel = chargerBudgetMensuelSurCompteInactif(utilisateur, compteBancaire, moisPrecedent, anneePrecedente);
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
	public boolean isBudgetUpToDate(String idBudget, Calendar dateToCompare, Utilisateur utilisateur) {

		BasicTextEncryptor encryptor = this.serviceAuth.getEncryptor(utilisateur);
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
	 * @param idCompte id du compte
	 * @return etat du compte
	 */
	public boolean isCompteActif(String idCompte){
		try {
			return dataUsers.isCompteActif(idCompte);
		} catch (DataNotFoundException e) {
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
	private BudgetMensuel initNewBudget(CompteBancaire compteBancaire, Utilisateur utilisateur, Month mois, int annee) throws BudgetNotFoundException,CompteClosedException, DataNotFoundException{
		LOGGER.info("[INIT] Initialisation du budget {} de {}/{}", compteBancaire.getId(), mois, annee);
		BudgetMensuel budget = new BudgetMensuel();
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
			initBudgetFromBudgetPrecedent(budget, chargerBudgetMensuel(utilisateur, compteBancaire, moisPrecedent, anneePrecedente));
		}
		else{
			LOGGER.warn("Le budget {}/{} n'a jamais existé", mois, annee);
			budget.setFinArgentAvance(0D);
			budget.setFinCompteReel(0D);
			budget.setNowArgentAvance(0D);
			budget.setNowCompteReel(0D);
			budget.setResultatMoisPrecedent(0D);
			budget.setListeOperations(new ArrayList<LigneOperation>());
			budget.setMargeSecurite(0D);
			budget.setMargeSecuriteFinMois(0D);
		}

		LOGGER.info("[INIT] Sauvegarde du nouveau budget {}", budget);
		BasicTextEncryptor encryptor = this.serviceAuth.getEncryptor(utilisateur);
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
	public BudgetMensuel reinitialiserBudgetMensuel(BudgetMensuel budgetMensuel, Utilisateur utilisateur) throws BudgetNotFoundException, CompteClosedException, DataNotFoundException{

		CompteBancaire compteBancaire = serviceParams.getCompteById(budgetMensuel.getCompteBancaire().getId(), utilisateur.getLogin());
		if(compteBancaire != null){
			// S'il y a eu cloture, on ne fait rien
			return initNewBudget(compteBancaire, utilisateur, budgetMensuel.getMois(), budgetMensuel.getAnnee());
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
			budget.setMargeSecurite(budgetPrecedent.getMargeSecurite());
			budget.setResultatMoisPrecedent(budgetPrecedent.getFinArgentAvance());
			budget.setDateMiseAJour(Calendar.getInstance());
			for (LigneOperation depenseMoisPrecedent : budgetPrecedent.getListeOperations()) {
				if(depenseMoisPrecedent.isPeriodique() || depenseMoisPrecedent.getEtat().equals(EtatLigneOperationEnum.REPORTEE)){
					budget.getListeOperations().add(transformer.cloneDepenseToMoisSuivant(depenseMoisPrecedent));	
				}

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
	public BudgetMensuel ajoutLigneTransfertIntercompte(String idBudget, LigneOperation ligneOperation, CompteBancaire compteCrediteur, Utilisateur utilisateur) throws BudgetNotFoundException, DataNotFoundException, CompteClosedException{

		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, this.serviceAuth.getEncryptor(utilisateur));
		/**
		 *  Si transfert intercompte : Création d'une ligne dans le compte distant
		 */
		BudgetMensuel budgetTransfert = chargerBudgetMensuel(utilisateur, compteCrediteur, budget.getMois(), budget.getAnnee());
		if(budget.getCompteBancaire().isActif() && budgetTransfert.getCompteBancaire().isActif() && budget.isActif() && budgetTransfert.isActif()){
			LOGGER.info("Ajout d'un transfert intercompte de {} vers {} > {} ", budget.getCompteBancaire().getLibelle(), compteCrediteur, ligneOperation);

			// #59 : Cohérence des états
			EtatLigneOperationEnum etatDepenseCourant = ligneOperation.getEtat();
			EtatLigneOperationEnum etatDepenseTransfert = null;
			switch (etatDepenseCourant) {
			case ANNULEE:
				etatDepenseTransfert = EtatLigneOperationEnum.ANNULEE;
				break;
			case REPORTEE:
				etatDepenseTransfert = EtatLigneOperationEnum.REPORTEE;
				break;
			case PREVUE:
			case REALISEE:
			default:				
				etatDepenseTransfert = EtatLigneOperationEnum.PREVUE;
				break;
			}

			LigneOperation ligneTransfert = new LigneOperation(
					ligneOperation.getSsCategorie(), 
					"[de "+budget.getCompteBancaire().getLibelle()+"] " + ligneOperation.getLibelle(), 
					TypeOperationEnum.CREDIT, 
					Double.toString(Math.abs(ligneOperation.getValeur())), 
					etatDepenseTransfert, 
					ligneOperation.isPeriodique());

			ajoutOperation(budgetTransfert, ligneTransfert, utilisateur.getLibelle());
			calculEtSauvegardeBudget(budgetTransfert, utilisateur);
			/**
			 *  Ajout de la ligne dans le budget courant
			 */
			ligneOperation.setLibelle("[vers "+budgetTransfert.getCompteBancaire().getLibelle()+"] " + ligneOperation.getLibelle());
			return ajoutOperationEtCalcul(idBudget, ligneOperation, utilisateur);
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
	private void ajoutOperation(BudgetMensuel budget, LigneOperation ligneOperation, String auteur){
		LOGGER.info("Ajout d'une Opération : {}", ligneOperation);
		ligneOperation.setAuteur(auteur);
		ligneOperation.setDateMaj(Calendar.getInstance().getTime());
		budget.getListeOperations().add(ligneOperation);
		budget.setDateMiseAJour(Calendar.getInstance());
	}


	/**
	 * Ajout d'une ligne de dépense
	 * @param ligneOperation ligne de dépense
	 * @param auteur auteur de l'action 
	 * @throws BudgetNotFoundException 
	 */
	public BudgetMensuel ajoutOperationEtCalcul(String idBudget, LigneOperation ligneOperation, Utilisateur auteur) throws BudgetNotFoundException{
		BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, this.serviceAuth.getEncryptor(auteur));
		if(budget.isActif() && budget.getCompteBancaire().isActif()){
			ajoutOperation(budget, ligneOperation, auteur.getLibelle());
			// Résultat mensuel
			budget = calculEtSauvegardeBudget(budget, auteur);
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
	 * @param auteur auteur
	 * @throws DataNotFoundException
	 * @throws BudgetNotFoundException
	 */
	public BudgetMensuel majLigneDepense(String idBudget, LigneOperation ligneDepense, Utilisateur auteur) throws DataNotFoundException, BudgetNotFoundException{

		boolean ligneupdated = false;
		if(ligneDepense != null){
			BudgetMensuel budget = dataDepenses.chargeBudgetMensuelById(idBudget, this.serviceAuth.getEncryptor(auteur));
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
				ligneDepense.setAuteur(auteur.getLibelle());
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
				budget = calculEtSauvegardeBudget(budget, auteur);
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
	public BudgetMensuel majEtatLigneOperation(BudgetMensuel budget, String ligneId, EtatLigneOperationEnum etat, Utilisateur auteur) throws DataNotFoundException, BudgetNotFoundException{
		LigneOperation ligneOperation = getLigneOperation(budget, ligneId);
		// Mise à jour de l'état
		if(ligneOperation != null){
			ligneOperation.setEtat(etat);
			if(EtatLigneOperationEnum.REALISEE.equals(etat)){
				ligneOperation.setDateOperation(Calendar.getInstance().getTime());
			}
			else{
				ligneOperation.setDateOperation(null);
			}
			// Mise à jour de la ligne
			ligneOperation.setDateMaj(Calendar.getInstance().getTime());
			ligneOperation.setAuteur(auteur.getLibelle());
			budget = majLigneDepense(budget.getId(), ligneOperation, auteur);
		}
		return budget;
	}	


	/**
	 * Mise à jour de la ligne comme dernière opération
	 * @param ligneId
	 */
	public void setLigneDepenseAsDerniereOperation(BudgetMensuel budget, String ligneId, Utilisateur utilisateur){
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
		dataDepenses.sauvegardeBudgetMensuel(budget, this.serviceAuth.getEncryptor(utilisateur));
	}

	/**
	 * Calcul du budget Courant et sauvegarde
	 * @param budget budget à sauvegarder
	 */
	public BudgetMensuel calculEtSauvegardeBudget(BudgetMensuel budget, Utilisateur utilisateur){
		calculBudget(budget);
		dataDepenses.sauvegardeBudgetMensuel(budget, this.serviceAuth.getEncryptor(utilisateur));
		return budget;
	}

	/**
	 * Calcul du résumé
	 * @param budgetMensuelCourant
	 */
	private void calculBudget(BudgetMensuel budget){

		LOGGER.info("(Re)Calcul du budget : {}", budget);
		budget.razCalculs();

		for (LigneOperation operation : budget.getListeOperations()) {
			LOGGER.trace("     > {}", operation);
			Double depenseVal = operation.getValeur();
			budget.getSetLibellesDepensesForAutocomplete().add(operation.getLibelle());
			/**
			 *  Calcul par catégorie
			 */
			Double[] valeursCat = {0D,0D};
			if(budget.getTotalParCategories().get(operation.getCategorie()) != null){
				valeursCat = budget.getTotalParCategories().get(operation.getCategorie());
			}
			if(operation.getEtat().equals(EtatLigneOperationEnum.REALISEE)){
				valeursCat[0] = valeursCat[0] + depenseVal;
				valeursCat[1] = valeursCat[1] + depenseVal;
			}
			else if(operation.getEtat().equals(EtatLigneOperationEnum.PREVUE)){
				valeursCat[1] = valeursCat[1] + depenseVal;
			}
			budget.getTotalParCategories().put(operation.getCategorie(), valeursCat);

			/**
			 *  Calcul par sous catégorie
			 */
			Double[] valeurSsCat = {0D,0D};
			if( budget.getTotalParSSCategories().get(operation.getSsCategorie()) != null){
				valeurSsCat = budget.getTotalParSSCategories().get(operation.getSsCategorie());
			}
			if(operation.getEtat().equals(EtatLigneOperationEnum.REALISEE)){
				valeurSsCat[0] = valeurSsCat[0] + depenseVal;
				valeurSsCat[1] = valeurSsCat[1] + depenseVal;
			}
			if(operation.getEtat().equals(EtatLigneOperationEnum.PREVUE)){
				valeurSsCat[1] = valeurSsCat[1] + depenseVal;
			}
			budget.getTotalParSSCategories().put(operation.getSsCategorie(), valeurSsCat);

			// Si réserve : ajout dans le calcul fin de mois
			// Pour taper dans la réserve : inverser le type de dépense
			int sens = 1;
			if(ID_SS_CAT_RESERVE.equals(operation.getSsCategorie().getId())){
				budget.setMargeSecuriteFinMois(budget.getMargeSecurite() + Double.valueOf(operation.getValeur()));
				sens = -1;
			}

			/**
			 * Calcul des totaux
			 */

			if(operation.getEtat().equals(EtatLigneOperationEnum.REALISEE)){
				budget.ajouteANowArgentAvance(sens * depenseVal);
				budget.ajouteANowCompteReel(sens * depenseVal);
				budget.ajouteAFinArgentAvance(sens * depenseVal);
				budget.ajouteAFinCompteReel(sens * depenseVal);				
			}
			else if(operation.getEtat().equals(EtatLigneOperationEnum.PREVUE)){
				budget.ajouteAFinArgentAvance(sens * depenseVal);
				budget.ajouteAFinCompteReel(sens * depenseVal);
			}
		}
		LOGGER.debug("Argent avancé : {}  :   {}", budget.getNowArgentAvance(), budget.getFinArgentAvance());
		LOGGER.debug("Solde réel    : {}  :   {}", budget.getNowCompteReel(), budget.getFinCompteReel());
	}

	/**
	 * Lock/unlock d'un budget
	 * @param budgetActif
	 */
	public BudgetMensuel setBudgetActif(BudgetMensuel budgetMensuel, boolean budgetActif, Utilisateur utilisateur){
		LOGGER.info("{} du budget {}/{} de {}", budgetActif ? "Réouverture" : "Fermeture", budgetMensuel.getMois(), budgetMensuel.getAnnee(), budgetMensuel.getCompteBancaire().getLibelle());
		budgetMensuel.setActif(budgetActif);
		budgetMensuel.setDateMiseAJour(Calendar.getInstance());
		return calculEtSauvegardeBudget(budgetMensuel, utilisateur);
	}
}
