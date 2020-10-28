package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;

/**
 * Service Provider Interface de {@link com.terrier.finances.gestion.services.budgets.api.client.ComptesAPIClient}
 */
public interface IComptesServiceProvider {

    /**
     * Recherche du compte
     * @param idCompte id du Compte
     * @return compte correspondant
     */
    public CompteBancaire getCompteById(String idCompte);
}
