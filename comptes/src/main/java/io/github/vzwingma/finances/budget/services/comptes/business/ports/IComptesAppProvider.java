package io.github.vzwingma.finances.budget.services.comptes.business.ports;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.smallrye.mutiny.Uni;

import java.util.List;

/**
 *  Application Provider Interface de Comptes
 */
public interface IComptesAppProvider {


    /**
     * @param idCompte id du compte
     * @return etat du compte
     */
    Uni<Boolean> isCompteActif(String idCompte);

    /**
     * Recherche du compte par id
     * @param idCompte id du compte
     * @param idUtilisateur utilisateur
     * @return compteBancaire
     */
    Uni<CompteBancaire> getCompteById(String idCompte, String idUtilisateur) ;



    /**
     * Recherche des comptes d'un utilisateur
     * @param idUtilisateur utilisateur
     * @return liste des comptes bancaires
     */
    Uni<List<CompteBancaire>> getComptesUtilisateur(String idUtilisateur) ;

}
