package com.terrier.finances.gestion.services.budgets.business.ports;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;

import java.util.List;

/**
 *  Application Provider Interface de Comptes
 */
public interface IOperationsRequest {

    /**
     * @param idCompte id du compte
     * @return etat du compte
     */
    public boolean isCompteActif(String idCompte);

    /**
     * Recherche du compte par id
     * @param idCompte id du compte
     * @param idUtilisateur utilisateur
     * @return compteBancaire
     * @throws DataNotFoundException erreur données non trouvées
     */
    public CompteBancaire getCompteById(String idCompte, String idUtilisateur) throws DataNotFoundException;



    /**
     * Recherche des comptes d'un utilisateur
     * @param idUtilisateur utilisateur
     * @return liste des comptes bancaires
     * @throws DataNotFoundException erreur données non trouvées
     */
    public List<CompteBancaire> getComptesUtilisateur(String idUtilisateur) throws DataNotFoundException;

}
