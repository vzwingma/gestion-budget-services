package io.github.vzwingma.finances.budget.services.operations.business;


import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContext;
import io.github.vzwingma.finances.budget.services.communs.data.trace.BusinessTraceContextKeyEnum;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.IdsCategoriesEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.TotauxCategorie;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.TypeOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.github.vzwingma.finances.budget.services.operations.spi.IParametragesServiceProvider;
import io.github.vzwingma.finances.budget.services.operations.utils.BudgetDataUtils;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
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


	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsService.class);

	@Inject
	IOperationsRepository dataOperationsProvider;

	@Inject
	IBudgetAppProvider budgetService;

	@RestClient
	@Inject
	IParametragesServiceProvider parametragesService;

	/**
	 * Réinjection des catégories dans les opérations du budget
	 * @param operation opération
	 * @param categories liste des catégories
	 * @deprecated
	 * @since 17.0.0
	 */
	// TODO : A supprimer ?
	@Deprecated
	private void completeCategoriesOnOperation(LigneOperation operation, List<CategorieOperations> categories) {
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
		return dataOperationsProvider.chargeLibellesOperations(idCompte, annee)
				// #124 : suppression des tags [] dans les libellés
				.map(BudgetDataUtils::deleteTagFromString);
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
		LOGGER.debug("Solde prévu\t| {} | {}", soldes.getSoldeAtMaintenant(), soldes.getSoldeAtFinMoisCourant());

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
			LOGGER.trace("Total par catégorie [idCat={} : {}]", operation.getCategorie().getId(), valeursCat);
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
			LOGGER.trace("Total par ss catégorie [idSsCat={} : {}]", operation.getSsCategorie().getId(), valeursSsCat);
			totauxSsCategoriesMap.put(operation.getSsCategorie().getId(), valeursSsCat);
		}
		else {
			LOGGER.warn("L'opération [{}]  n'a pas de sous-catégorie [{}]", operation, operation.getSsCategorie() );
		}
	}



	@Override
	public Uni<Boolean> setLigneAsDerniereOperation(String idBudget, String ligneId) {
		LOGGER.info("Tag de la ligne comme dernière opération");
		final AtomicBoolean operationUpdate = new AtomicBoolean(false);
		return this.budgetService.getBudgetMensuel(idBudget)
				.onItem()
				.invoke(budget -> {
					if(budget.getListeOperations() != null && !budget.getListeOperations().isEmpty()) {
						budget.getListeOperations()
							.forEach(op -> {
								op.setTagDerniereOperation(ligneId.equals(op.getId()));
								if(ligneId.equals(op.getId())) {
									LOGGER.debug("L'opération a été trouvée dans le budget ");
									operationUpdate.set(true);
								}
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
	public List<LigneOperation> addOperation(List<LigneOperation> operations, LigneOperation ligneOperation)  {
		BusinessTraceContext.get().put(BusinessTraceContextKeyEnum.OPERATION, ligneOperation.getId());
		// Si mise à jour d'une opération, on l'enlève
		int rangMaj = operations.indexOf(ligneOperation);
		operations.removeIf(op -> op.getId().equals(ligneOperation.getId()));

		if (ligneOperation.getEtat() != null) {
			LigneOperation ligneUpdatedOperation = updateOperation(ligneOperation);
			if (rangMaj >= 0) {
				LOGGER.debug("Mise à jour de l'opération : {}", ligneUpdatedOperation);
				operations.add(rangMaj, ligneUpdatedOperation);
			} else {
				LOGGER.debug("Ajout de l'opération : {}", ligneUpdatedOperation);
				operations.add(ligneUpdatedOperation);
			}
		} else {
			LOGGER.info("Suppression d'une Opération : {}", ligneOperation);
		}
		return operations;
	}



	/**
	 * @param ligneOperation opération
	 * @return ligneOperation màj
	 */
	private LigneOperation updateOperation(LigneOperation ligneOperation) {
		ligneOperation.getAutresInfos().setDateMaj(LocalDateTime.now());
		// TODO : nomProprietaire à ajouter
		ligneOperation.getAutresInfos().setAuteur("vzwingmann");
		if(EtatOperationEnum.REALISEE.equals(ligneOperation.getEtat())) {
			if(ligneOperation.getAutresInfos().getDateOperation() == null){
				ligneOperation.getAutresInfos().setDateOperation(LocalDateTime.now());
			}
		}
		else {
			ligneOperation.getAutresInfos().setDateOperation(null);
		}
		return ligneOperation;
	}

	/**
	 * Si frais remboursable : ajout du remboursement en prévision
	 * #62 : et en mode création
	 * @param operationSource ligne d'opération source, ajoutée
	 * @return ligne de remboursement
	 */
	@Override
	public Uni<LigneOperation> createOperationRemboursement(LigneOperation operationSource){

		// Si l'opération est une remboursement, on ajoute la catégorie de remboursement
		if (operationSource.getSsCategorie() != null
				&& operationSource.getCategorie() != null
				&& IdsCategoriesEnum.FRAIS_REMBOURSABLES.getId().equals(operationSource.getCategorie().getId())) {

			return Uni.combine().all().unis(
							Uni.createFrom().item(operationSource),
							this.parametragesService.getCategorieParId(IdsCategoriesEnum.REMBOURSEMENT.getId()))
					.asTuple()
					.map(tuple -> createOperationRemboursement(tuple.getItem1(), tuple.getItem2()))
					.onItem()
						.ifNull().failWith(new DataNotFoundException("Impossible de créer le remboursement car la catégorie de remboursement n'a pas été trouvée"));
		}
		else{
			return Uni.createFrom().nullItem();
		}
	}

	/**
	 * Si frais remboursable : ajout du remboursement en prévision
	 * #62 : et en mode création
	 * @param ligneOperation ligne d'opération à ajouter
	 * @return ligne de remboursement
	 */

	private LigneOperation createOperationRemboursement(LigneOperation ligneOperation, CategorieOperations categorieRemboursement) {
		if(categorieRemboursement != null) {
			LigneOperation ligneRemboursement = new LigneOperation(
					categorieRemboursement,
					"[Remboursement] " + ligneOperation.getLibelle(),
					TypeOperationEnum.CREDIT,
					Math.abs(ligneOperation.getValeur()),
					EtatOperationEnum.REPORTEE,
					ligneOperation.isPeriodique());
			// TODO : nomProprietaire à ajouter
			ligneRemboursement.getAutresInfos().setAuteur("vzwingmann");
			ligneRemboursement.getAutresInfos().setDateMaj(LocalDateTime.now());
			return ligneRemboursement;
		}
		else{
			return null;
		}
	}



	@Override
	public List<LigneOperation> addOperationIntercompte(List<LigneOperation> operations, LigneOperation ligneOperationSource, String libelleOperationCible){

		// #59 : Cohérence des états
		EtatOperationEnum etatDepenseTransfert;
		switch (ligneOperationSource.getEtat()) {
			case ANNULEE -> etatDepenseTransfert = EtatOperationEnum.ANNULEE;
			case REPORTEE -> etatDepenseTransfert = EtatOperationEnum.REPORTEE;
			// pour tous les autres cas, on prend l'état de l'opération source
			default -> etatDepenseTransfert = EtatOperationEnum.PREVUE;
		}

		LigneOperation ligneTransfert = new LigneOperation(
				ligneOperationSource.getCategorie(),
				ligneOperationSource.getSsCategorie(),
				libelleOperationCible,
				TypeOperationEnum.CREDIT,
				Math.abs(ligneOperationSource.getValeur()),
				etatDepenseTransfert,
				ligneOperationSource.isPeriodique());
		LOGGER.debug("Ajout de l'opération [{}] dans le budget", ligneTransfert);
		operations.add(ligneTransfert);
		return operations;
	}


	@Override
	public void deleteOperation(List<LigneOperation> operations, String idOperation) {
		// Si suppression d'une opération, on l'enlève
		if(operations.removeIf(op -> op.getId().equals(idOperation))) {
			LOGGER.info("Suppression d'une Opération : {}", idOperation);
		}
		else {
			LOGGER.warn("[idBudget={}][idOperation={}] Impossible de supprimer l'opération. Introuvable", operations, idOperation);
		}
	}
}
