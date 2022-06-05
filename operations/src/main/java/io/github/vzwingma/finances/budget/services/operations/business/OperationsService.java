package io.github.vzwingma.finances.budget.services.operations.business;


import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.TotauxCategorie;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.github.vzwingma.finances.budget.services.operations.utils.BudgetDataUtils;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service fournissant les calculs de budget sur les opérations
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor @Setter
public class OperationsService implements IOperationsAppProvider {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsService.class);

	@Inject
	IOperationsRepository dataOperationsProvider;

	@Inject
	IBudgetAppProvider budgetService;


	@Override
	public void completeCategoriesOnOperation(LigneOperation operation, List<CategorieOperations> categories) {
		try {
			CategorieOperations catFound = BudgetDataUtils.getCategorieById(operation.getSsCategorie().getId(), categories);
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

	@Override
	public Multi<String> getLibellesOperations(String idCompte, int annee) {
		return dataOperationsProvider.chargeLibellesOperations(idCompte, annee);
	}

	@Override
	public Uni<BudgetMensuel> createOperationIntercompte(String idBudget, LigneOperation ligneOperation, String idCompteDestination) {
		return null;
	}

	/**
	 * Calcul des soldes
	 * @param operations opérations
	 * @param soldes soldes
	 * @param totauxCategorieMap map des totaux par catégorie
	 * @param totauxSsCategoriesMap map des totaux par sous catégorie
	 */
	@Override
	public void calculSoldes(List<LigneOperation> operations, BudgetMensuel.Soldes soldes, Map<String, TotauxCategorie> totauxCategorieMap, Map<String, TotauxCategorie> totauxSsCategoriesMap) {

		for (LigneOperation operation : operations) {
			LOGGER.trace("     > {}", operation);
			Double valeurOperation = operation.getValeur();

			// Calcul par catégorie
			calculBudgetTotalCategories(totauxCategorieMap, operation);
			// Calcul par sous catégorie
			calculBudgetTotalSsCategories(totauxSsCategoriesMap, operation);
			// Calcul des totaux
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				BudgetDataUtils.ajouteASoldeNow(soldes, valeurOperation);
				BudgetDataUtils.ajouteASoldeFin(soldes, valeurOperation);
			}
			else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				BudgetDataUtils.ajouteASoldeFin(soldes, valeurOperation);
			}
		}
		LOGGER.debug("Solde prévu\t| {}\t| {}", soldes.getSoldeAtMaintenant(), soldes.getSoldeAtFinMoisCourant());

	}


	/**
	 * Calcul du total de la catégorie du budget via l'opération en cours
	 * @param totauxCategorieMap à calculer
	 * @param operation opération à traiter
	 */
	private void calculBudgetTotalCategories(Map<String, TotauxCategorie> totauxCategorieMap, LigneOperation operation) {

		if(operation.getCategorie() != null && operation.getCategorie().getId() != null) {
			Double valeurOperation = operation.getValeur();
			TotauxCategorie valeursCat = new TotauxCategorie();
			if(totauxCategorieMap.get(operation.getCategorie().getId()) != null){
				valeursCat = totauxCategorieMap.get(operation.getCategorie().getId());
			}
			valeursCat.setLibelleCategorie(operation.getCategorie().getLibelle());
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				valeursCat.ajouterATotalAtMaintenant(valeurOperation);
				valeursCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			else if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				valeursCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			LOGGER.debug("Total par catégorie [idCat={} : {}]", operation.getCategorie().getId(), valeursCat);
			totauxCategorieMap.put(operation.getCategorie().getId(), valeursCat);
		}
		else {
			LOGGER.warn("L'opération [{}] n'a pas de catégorie [{}]", operation, operation.getCategorie() );
		}
	}

	/**
	 * Calcul du total de la sous catégorie du budget via l'opération en cours
	 * @param totauxSsCategoriesMap  à calculer
	 * @param operation opération à traiter
	 *
	 * */
	private void calculBudgetTotalSsCategories(Map<String, TotauxCategorie> totauxSsCategoriesMap, LigneOperation operation) {
		if(operation.getSsCategorie() != null && operation.getSsCategorie().getId() != null) {
			Double valeurOperation = operation.getValeur();
			TotauxCategorie valeursSsCat = new TotauxCategorie();
			if( totauxSsCategoriesMap.get(operation.getSsCategorie().getId()) != null){
				valeursSsCat = totauxSsCategoriesMap.get(operation.getSsCategorie().getId());
			}
			valeursSsCat.setLibelleCategorie(operation.getSsCategorie().getLibelle());
			if(operation.getEtat().equals(EtatOperationEnum.REALISEE)){
				valeursSsCat.ajouterATotalAtMaintenant(valeurOperation);
				valeursSsCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			if(operation.getEtat().equals(EtatOperationEnum.PREVUE)){
				valeursSsCat.ajouterATotalAtFinMoisCourant(valeurOperation);
			}
			LOGGER.debug("Total par ss catégorie [idCat={} : {}]", operation.getSsCategorie().getId(), valeursSsCat);
			totauxSsCategoriesMap.put(operation.getSsCategorie().getId(), valeursSsCat);
		}
		else {
			LOGGER.warn("L'opération [{}]  n'a pas de sous-catégorie [{}]", operation, operation.getSsCategorie() );
		}
	}



	@Override
	public Uni<Boolean> setLigneAsDerniereOperation(String idBudget, String ligneId) {
		LOGGER.info("[idBudget={}][idOperation={}] Tag de la ligne comme dernière opération", idBudget, ligneId);
		final AtomicBoolean operationUpdate = new AtomicBoolean(false);
		return this.budgetService.getBudgetMensuel(idBudget)
				.onItem()
				.invoke(budget -> {
					if(budget.getListeOperations() != null && !budget.getListeOperations().isEmpty()) {
						budget.getListeOperations()
							.parallelStream()
							.forEach(op -> {
								op.setTagDerniereOperation(ligneId.equals(op.getId()));
								operationUpdate.set(ligneId.equals(op.getId()));
							});
						// Mise à jour du budget
						budget.setDateMiseAJour(LocalDateTime.now());
				}})
				.call(budget -> {
					if(operationUpdate.get()) {
						return this.dataOperationsProvider.sauvegardeBudgetMensuel(budget);
					}
					else{
						return Uni.createFrom().failure(new DataNotFoundException("L'opération "+ligneId+" n'a pas été trouvée dans le budget "+idBudget));
					}
				}).onItem().transform(Objects::nonNull);
			}


	@Override
	public Uni<BudgetMensuel> deleteOperation(String idBudget, String idOperation) {
		return null;
	}

	@Override
	public Uni<BudgetMensuel> updateOperationInBudget(String idBudget, LigneOperation ligneOperation) {
		return null;
	}
}
