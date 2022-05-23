package io.github.vzwingma.finances.budget.services.operations.business;


import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.BudgetNotFoundException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.CompteClosedException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.UserNotAuthorizedException;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.spi.IComptesServiceProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.github.vzwingma.finances.budget.services.operations.utils.BudgetDataUtils;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * Service fournissant les budgets
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor
public class BudgetService implements IBudgetAppProvider {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetService.class);
	/**
	 * Service Provider Interface des données
	 */
	@Inject
	IOperationsRepository dataOperationsProvider;

	@Inject
	IComptesServiceProvider comptesService;

	@Inject
	IOperationsAppProvider operationsAppProvider;

	public BudgetService(IOperationsRepository budgetsRepository, IComptesServiceProvider comptesService, IOperationsAppProvider operationsService){
		this.dataOperationsProvider = budgetsRepository;
		this.comptesService = comptesService;
		this.operationsAppProvider = operationsService;
	}

	/**
	 * Chargement du budget du mois courant
	 * @param idCompte compte
	 * @param mois mois
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	@Override
	public Uni<BudgetMensuel> getBudgetMensuel(String idCompte, Month mois, int annee, String idProprietaire) {
		LOGGER.debug("Chargement du budget {} de {}/{}", idCompte, mois, annee);
		return this.comptesService.getCompteById(idCompte, idProprietaire)
				.onItem()
				.ifNotNull()
				.transformToUni(compte -> {
					if(Boolean.TRUE.equals(compte.isActif())){
						return chargerBudgetMensuelSurCompteActif(idProprietaire, compte, mois, annee);
					}
					else{
						return chargerBudgetMensuelSurCompteInactif(compte, mois, annee);
					}
				});
	}



	/**
	 * Chargement du budget du mois courant pour le compte actif
	 * @param idProprietaire id du propriétaire
	 * @param compteBancaire compte
	 * @param mois mois
	 * @param annee année
	 * @return budget mensuel chargé et initialisé à partir des données précédentes
	 */
	private Uni<BudgetMensuel> chargerBudgetMensuelSurCompteActif(String idProprietaire, CompteBancaire compteBancaire, Month mois, int annee) {
		LOGGER.debug("Chargement du budget de {}/{} du compte actif {} ", mois, annee, compteBancaire.getId());

			return this.dataOperationsProvider.chargeBudgetMensuel(compteBancaire, mois, annee)
					// Budget introuvable - init d'un nouveau budget
					.onItem()
						.ifNull().switchTo(() -> initNewBudget(compteBancaire, idProprietaire, mois, annee))

					.invoke(budgetMensuel -> LOGGER.debug("Budget mensuel chargé {}", budgetMensuel))
					// rechargement du solde mois précédent (s'il a changé)
					.onItem().transformToUni(budgetMensuel -> recalculSoldeAFinMoisPrecedent(budgetMensuel, compteBancaire) )
					// recalcul de tous les soldes du budget courant
					.onItem()
						.ifNotNull()
						.invoke(this::recalculSoldes)
						// Sauvegarde du budget
						.call(this::sauvegardeBudget);
	}

	/**
	 * Recalcul du solde à la fin du mois précédent
	 * @param budgetMensuel budget mensuel
	 * @param compteBancaire	compte
	 * @return budget mensuel recalculé
	 */
	private Uni<BudgetMensuel> recalculSoldeAFinMoisPrecedent(final BudgetMensuel budgetMensuel, CompteBancaire compteBancaire) {
		// Maj du budget ssi budget actif
		if (budgetMensuel != null && budgetMensuel.isActif()) {
			// Recalcul du résultat du mois précédent
			Month moisPrecedent = budgetMensuel.getMois().minus(1);
			int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? budgetMensuel.getAnnee() - 1 : budgetMensuel.getAnnee();

			LOGGER.debug("Recalcul du solde à partir du budget du mois précédent du compte actif {} : {}/{}", compteBancaire, moisPrecedent, anneePrecedente);

			return this.dataOperationsProvider.chargeBudgetMensuel(compteBancaire, moisPrecedent, anneePrecedente)
					.onItem().transformToUni(budgetPrecedent -> {
							if(budgetPrecedent != null) {
								budgetMensuel.getSoldes().setSoldeAtFinMoisPrecedent(budgetPrecedent.getSoldes().getSoldeAtFinMoisCourant());
							}
							return Uni.createFrom().item(budgetMensuel);
						})
;
		}
		else{
			LOGGER.debug("Budget inactif, pas de recalcul du solde à partir du budget du mois précédent du compte actif {}", compteBancaire);
			return Uni.createFrom().item(budgetMensuel);
		}

	}

	/**
	 * Calcul des soldes du budget mensuel
	 *
	 * @param budget budget à calculer
	 */
	@Override
	public void recalculSoldes(BudgetMensuel budget) {

		LOGGER.info("(Re)Calcul des soldes du budget : {}", budget.getId());
		BudgetDataUtils.razCalculs(budget);

		this.operationsAppProvider.calculSoldes(budget.getListeOperations(), budget.getSoldes(), budget.getTotauxParCategories(), budget.getTotauxParSSCategories());
		LOGGER.debug("Solde prévu\t| {}\t| {}", budget.getSoldes().getSoldeAtMaintenant(), budget.getSoldes().getSoldeAtFinMoisCourant());
	}

	/**
	 * Chargement du budget du dernier mois connu pour le compte inactif
	 * @param compteBancaire compte bancaire
	 * @param mois mois
	 * @param annee année
	 * @return budget mensuel chargé à partir des données précédentes
	 */
	private Uni<BudgetMensuel> chargerBudgetMensuelSurCompteInactif(CompteBancaire compteBancaire, Month mois, int annee) {
		LOGGER.debug("Chargement du budget du compte inactif {} de {}/{}", compteBancaire.getId(), mois, annee);

		// Calcul de paramètres pour le recovery
		Month moisPrecedent = mois.minus(1);
		int anneePrecedente = Month.DECEMBER.equals(moisPrecedent) ? annee -1 : annee;

		// Chargement du budget précédent
		return this.dataOperationsProvider.chargeBudgetMensuel(compteBancaire, mois, annee)
				.onItem()
				// Si le budget n'existe pas, on recherche le dernier
				.ifNull()
					.switchTo(() -> chargerBudgetMensuelSurCompteInactif(compteBancaire, moisPrecedent, anneePrecedente))
				.onItem()
					.transform(budgetMensuel -> {
						// On reporte l'état inactif du compte sur les anciens budgets
						budgetMensuel.setIdCompteBancaire(compteBancaire.getId());
						// L'état du budget est forcé à inactif
						budgetMensuel.setActif(false);
						return budgetMensuel;
					})
					.invoke(budgetMensuel -> LOGGER.info("Budget du compte inactif {} de {}/{} chargé : {}", compteBancaire.getId(), mois, annee, budgetMensuel));
	}



	/**
	 * Init new budget
	 * @param compteBancaire compte
	 * @param mois mois
	 * @param annee année
	 * @return budget nouvellement créé
	 */
	protected Uni<BudgetMensuel> initNewBudget(CompteBancaire compteBancaire, String idProprietaire, Month mois, int annee) {

		//Vérification du compte
		if(compteBancaire == null) {
			return Uni.createFrom().failure(new DataNotFoundException("Compte bancaire non trouvé"));
		} else if (!compteBancaire.isActif()) {
			return Uni.createFrom().failure(new CompteClosedException("Compte bancaire inactif"));
		}

		LOGGER.info("Initialisation du budget {} de {}/{}", compteBancaire.getLibelle(), mois, annee);
		BudgetMensuel budgetInitVide = new BudgetMensuel();
		budgetInitVide.setActif(true);
		budgetInitVide.setAnnee(annee);
		budgetInitVide.setMois(mois);
		budgetInitVide.setIdCompteBancaire(compteBancaire.getId());

		budgetInitVide.setNewBudget(true);
		budgetInitVide.setId();

		budgetInitVide.setDateMiseAJour(LocalDateTime.now());

		/*
		// TODO : Normalement, on ne devrait pas avoir de budget sans précédent
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
		*/
		// MAJ Calculs à partir du mois précédent
		// Recherche du budget précédent
		// Si impossible : on retourne le budget initialisé
		return getBudgetMensuel(compteBancaire.getId(),mois.minus(1) , Month.DECEMBER.equals(mois.minus(1)) ? annee -1 : annee, idProprietaire)
				.onItem()
				.invoke(budgetPrecedent -> {
					if(budgetPrecedent != null){
						// #115 : Cloture automatique du mois précédent
						setBudgetActif(budgetPrecedent.getId(), false, idProprietaire);
					}
				})
				.map(budgetPrecedent -> initBudgetFromBudgetPrecedent(budgetInitVide, budgetPrecedent, idProprietaire));
				// La sauvegarde du budget initialisé est faite dans le flux suivant
	}

	/**
	 * Initialisation du budget à partir du budget du mois précédent
	 * @param budget budget à calculer
	 * @param budgetPrecedent budget du mois précédent
	 */
	private BudgetMensuel initBudgetFromBudgetPrecedent(BudgetMensuel budget, BudgetMensuel budgetPrecedent, String proprietaire) {
		// Calcul
		if(budgetPrecedent != null){
			recalculSoldes(budgetPrecedent);
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
								.toList());
			}
		}
		return budget;
	}

	/**
	 * Chargement du budget mensuel
	 * @param idBudget id du budget
	 * @param idProprietaire id du propriétaire
	 * @return budget mensuel
	 */
	@Override
	public Uni<BudgetMensuel> getBudgetMensuel(String idBudget, String idProprietaire) {
		try {
			return getBudgetMensuel(BudgetDataUtils.getCompteFromBudgetId(idBudget), BudgetDataUtils.getMoisFromBudgetId(idBudget), BudgetDataUtils.getAnneeFromBudgetId(idBudget), idProprietaire);
		} catch (BudgetNotFoundException e) {
			return Uni.createFrom().failure(e);
		}
	}

	/**
	 * Réinitialisation du budget
	 * @param idBudget       budget mensuel
	 * @param idProprietaire propriétaire du budget
	 * @return budget mensuel réinitialisé
	 */
	@Override
	public Uni<BudgetMensuel> reinitialiserBudgetMensuel(String idBudget, String idProprietaire) {
		LOGGER.info("Réinitialisation du budget {}", idBudget);
		// Chargement du budget et compte
		return getBudgetMensuel(idBudget, idProprietaire)
				.flatMap(budget -> Uni.combine().all()
							  			.unis(Uni.createFrom().item(budget),
											  this.comptesService.getCompteById(budget.getIdCompteBancaire(), idProprietaire))
										.asTuple())
				// Si pas d'erreur, réinitialisation du budget
				.onItem().transformToUni(tuple -> initNewBudget(tuple.getItem2(), idProprietaire, tuple.getItem1().getMois(), tuple.getItem1().getAnnee()));
	}

	/**
	 * Budget mensuel actif
	 * @param idBudget id budget
	 * @return résultat de l'activation
	 */
	@Override
	public Uni<Boolean> isBudgetMensuelActif(String idBudget) {
		return this.dataOperationsProvider.isBudgetActif(idBudget);
	}

	/**
	 * Dés/Activation du budget mensuel
	 * @param idBudgetMensuel id budget mensuel
	 * @param budgetActif etat du budget
	 * @param idProprietaire id du propriétaire
	 * @return budget mensuel mis à jour
	 */
	@Override
	public Uni<BudgetMensuel> setBudgetActif(String idBudgetMensuel, boolean budgetActif, String idProprietaire) {
		LOGGER.info("{} du budget {} de {}", budgetActif ? "Réouverture" : "Fermeture", idBudgetMensuel, idProprietaire);
		if(idProprietaire != null){
			return dataOperationsProvider.chargeBudgetMensuel(idBudgetMensuel)
					.map(budgetMensuel -> {
						budgetMensuel.setActif(budgetActif);
						budgetMensuel.setDateMiseAJour(LocalDateTime.now());
						//  #119 #141 : Toutes les opérations en attente sont reportées
						if(!budgetActif){
							budgetMensuel.getListeOperations()
									.stream()
									.filter(op -> EtatOperationEnum.PREVUE.equals(op.getEtat()))
									.forEach(op -> op.setEtat(EtatOperationEnum.REPORTEE));
						}
						return budgetMensuel;
					})
					.onItem()
						.ifNotNull()
							.invoke(this::recalculSoldes)
							// Sauvegarde du budget
							.call(this::sauvegardeBudget);
		}
		else{
			return Uni.createFrom().failure(new UserNotAuthorizedException("Le propriétaire n'a pas été trouvé"));
		}
	}

	/**
	 *
	 * @param idBudget identifiant du budget
	 * @param dateSurIHM Date affichée
	 * @return budget up to date ?
	 */
	@Override
	public Uni<Boolean> isBudgetIHMUpToDate(String idBudget, Long dateSurIHM) {
		return this.dataOperationsProvider.chargeBudgetMensuel(idBudget)
				.onItem().transform(budgetMensuel -> {
					LOGGER.debug("Budget : Date mise à jour : {} / Date IHM : {}",
							BudgetDateTimeUtils.getMillisecondsFromLocalDateTime(budgetMensuel.getDateMiseAJour()), dateSurIHM);
					return dateSurIHM >= BudgetDateTimeUtils.getMillisecondsFromLocalDateTime(budgetMensuel.getDateMiseAJour());
				})
				.onFailure().recoverWithItem(Boolean.FALSE);
	}





	/**
	 * sauvegarde du budget Courant
	 *
	 * @param budget budget à sauvegarder
	 */

	private Uni<BudgetMensuel> sauvegardeBudget(BudgetMensuel budget) {
		budget.setDateMiseAJour(LocalDateTime.now());
		return dataOperationsProvider.sauvegardeBudgetMensuel(budget);
	}


	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param idCompte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	@Override
	public Uni<LocalDate[]> getIntervallesBudgets(String idCompte) {
		return this.dataOperationsProvider.getPremierDernierBudgets(idCompte)
				.onItem().ifNotNull()
				.transform(premierDernierBudgets -> {
					LocalDate premier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
					if(premierDernierBudgets[0] != null){
						premier = premier.withMonth(premierDernierBudgets[0].getMois().getValue()).withYear(premierDernierBudgets[0].getAnnee());
					}
					LocalDate dernier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
					if(premierDernierBudgets[1] != null){
						dernier = dernier.withMonth(premierDernierBudgets[1].getMois().getValue()).withYear(premierDernierBudgets[1].getAnnee());
					}
					return new LocalDate[]{premier, dernier};
				});
	}

}
