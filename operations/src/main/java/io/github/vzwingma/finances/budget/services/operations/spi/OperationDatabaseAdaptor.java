package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.BudgetNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Month;
import java.util.Optional;

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

	private static final String ATTRIBUT_COMPTE_ID = "idCompteBancaire";
	private static final String ATTRIBUT_ANNEE = "annee";
	private static final String ATTRIBUT_MOIS = "mois";

	@Override
	public Uni<BudgetMensuel> chargeBudgetMensuel(CompteBancaire compte, Month mois, int annee) {
		LOGGER.info("Chargement du budget {}/{} du compte {} ", mois, annee, compte.getId());

		return find(ATTRIBUT_COMPTE_ID + "=?1 and " + ATTRIBUT_MOIS + "=?2 and " + ATTRIBUT_ANNEE + "=?3", compte.getId(), mois.toString(), annee)
				.singleResultOptional()
				.onItem()
					.ifNull()
						.failWith(new BudgetNotFoundException("Erreur lors du chargement du budget pour le compte " + compte.getId() + " du mois " + mois + " de l'année " + annee))
				.map(Optional::get)
				.invoke(budget -> LOGGER.debug("\t> Réception du budget {}. {} opérations", budget.getId(), budget.getListeOperations().size()));
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
		LOGGER.info("Sauvegarde du budget du compte {} du {}/{}", budget.getIdCompteBancaire(), budget.getMois(), budget.getAnnee());
		return persistOrUpdate(budget)
				.invoke(budgetSauvegarde -> LOGGER.debug("\t> Budget {} sauvegardé", budgetSauvegarde.getId()));
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