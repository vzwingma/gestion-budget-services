/**
 * 
 */
package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IComptesServiceProvider;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

/**
 * Client de l'API Comptes 
 * @author vzwingma
 *
 */
@ApplicationScoped
public class ComptesServiceHTTPAdaptator implements IComptesServiceProvider {
//extends AbstractHTTPClient<CompteBancaire>

	/**
	 * Recherche du compte
	 * @param idCompte id du Compte
	 * @return compte correspondant
	 */
	@Override
	public Uni<CompteBancaire> getCompteById(String idCompte, String proprietaire) {
		/*
		try {
			return callHTTPGetData(BudgetApiUrlEnum.COMPTES_ID_FULL, Collections.singletonMap(BudgetApiUrlEnum.PARAM_ID_COMPTE, idCompte)).block();
		} catch (DataNotFoundException e) {
			LOGGER.error("Erreur lors de la recherche du compte", e);
			return null;
		}
		*/
		return null;
	}
}
