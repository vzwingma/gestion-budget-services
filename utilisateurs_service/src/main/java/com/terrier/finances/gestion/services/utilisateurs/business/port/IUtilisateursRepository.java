package com.terrier.finances.gestion.services.utilisateurs.business.port;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;

/**
 * Interface du Service Provider BDD Utilisateurs
 */
public interface IUtilisateursRepository {


    /**
     * @param login : login utilisateur
     * @return Utilisateur
     */
    public Utilisateur chargeUtilisateur(String login) throws DataNotFoundException;

    /**
     * Met à jour l'utilisateur en BDD
     */
    public void majUtilisateur(Utilisateur utilisateur);
}
