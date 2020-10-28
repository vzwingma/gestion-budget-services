package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.services.budgets.spi.ComptesAPIClient;
import com.terrier.finances.gestion.services.communs.business.ports.IServiceProvider;

/**
 * Service Provider Interface de {@link ComptesAPIClient}
 */
public interface IComptesServiceProvider extends IServiceProvider {

    /**
     * Recherche du compte
     * @param idCompte id du Compte
     * @return compte correspondant
     */
    public CompteBancaire getCompteById(String idCompte);
}
