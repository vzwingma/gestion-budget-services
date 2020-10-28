package com.terrier.finances.gestion.services.comptes.business.ports;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;

import java.util.List;

/**
 * Service Provider Interface du compte
 */
public interface IComptesRepository {


    /**
     * Chargement des comptes
     * @param idUtilisateur utilisateur
     * @return liste des comptes associés
     * @throws DataNotFoundException erreur dans la connexion
     */
    public List<CompteBancaire> chargeComptes(String idUtilisateur) throws DataNotFoundException;


    /**
     * Chargement d'un compte par un id
     * @param idCompte id du compte
     * @param idUtilisateur utilisateur associé
     * @return compte
     * @throws DataNotFoundException données introuvable
     */
    public CompteBancaire chargeCompteParId(String idCompte, String idUtilisateur) throws DataNotFoundException;


    /**
     * Chargement d'un compte par un id
     * @param idCompte id du compte
     * @return compte actif
     * @throws DataNotFoundException données introuvable
     */
    public boolean isCompteActif(String idCompte) throws DataNotFoundException;
}
