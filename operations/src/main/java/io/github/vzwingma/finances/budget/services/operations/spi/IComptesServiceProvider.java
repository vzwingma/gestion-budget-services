package io.github.vzwingma.finances.budget.services.operations.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.operations.api.enums.ComptesApiUrlEnum;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Service Provider Interface de {@link }
 */
@RestClient
@Path(ComptesApiUrlEnum.COMPTES_BASE)
public interface IComptesServiceProvider { // extends IServiceProvider {

    /**
     * Recherche du compte
     * @param idCompte id du Compte
     * @param proprietaire proprietaire du compte
     * @return compte correspondant
     */
    @GET
    @Path(ComptesApiUrlEnum.COMPTES_ID)
    Uni<CompteBancaire> getCompteById(String idCompte , String proprietaire);
}
