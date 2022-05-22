package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Month;

/**
 * Service de données en MongoDB fournissant les opérations.
 * Adapteur du port {@link IOperationsRepository}
 * @author vzwingma
 *
 */
@ApplicationScoped
public class OperationDatabaseAdaptor implements IOperationsRepository { // extends AbstractDatabaseServiceProvider<CategorieOperation>


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationDatabaseAdaptor.class);

	@Override
	public Uni<BudgetMensuel> chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee) {
		return null;
	}

	@Override
	public Uni<Boolean> isBudgetActif(String idBudget) {
		return null;
	}

	@Override
	public Uni<BudgetMensuel> chargeBudgetMensuel(String idBudget) {
		return null;
	}

	@Override
	public Multi<LigneOperation> chargerLignesDepenses(String idBudget) {
		return null;
	}

	@Override
	public Uni<BudgetMensuel> sauvegardeBudgetMensuel(BudgetMensuel budget) {
		return null;
	}

	@Override
	public Uni<BudgetMensuel[]> getPremierDernierBudgets(String compte) {
		return null;
	}

	@Override
	public Multi<String> chargeLibellesOperations(String idCompte, int annee) {
		return null;
	}
}