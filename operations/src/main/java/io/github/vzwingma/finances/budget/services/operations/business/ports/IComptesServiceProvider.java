package io.github.vzwingma.finances.budget.services.operations.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.smallrye.mutiny.Uni;

/**
 * Service Provider Interface de {@link }
 */
public interface IComptesServiceProvider { // extends IServiceProvider {

    /**
     * Recherche du compte
     * @param idCompte id du Compte
     * @param proprietaire proprietaire du compte
     * @return compte correspondant
     */
    Uni<CompteBancaire> getCompteById(String idCompte, String proprietaire);
}
