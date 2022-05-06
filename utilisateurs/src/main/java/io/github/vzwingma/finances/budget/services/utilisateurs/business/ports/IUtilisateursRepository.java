package io.github.vzwingma.finances.budget.services.utilisateurs.business.ports;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;

/**
 * Interface du Service Provider BDD Utilisateurs
 */
public interface IUtilisateursRepository {


    /**
     * @param login : login utilisateur
     * @return Utilisateur
     */
    Utilisateur chargeUtilisateur(String login) throws DataNotFoundException;

    /**
     * Met à jour l'utilisateur en BDD
     */
    void majUtilisateur(Utilisateur utilisateur);
}
