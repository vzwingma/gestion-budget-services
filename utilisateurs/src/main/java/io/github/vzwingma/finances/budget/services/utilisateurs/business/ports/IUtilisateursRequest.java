package io.github.vzwingma.finances.budget.services.utilisateurs.business.ports;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.UserAccessForbiddenException;
import io.github.vzwingma.finances.budget.services.utilisateurs.business.model.Utilisateur;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;

/**
 * Port de l'Application Provider Interface des Utilisateurs
 */
public interface IUtilisateursRequest {

    /**
     * Chargement d'un utilisateur
     * @param idUtilisateur login de l'utilisateur
     * @return Utilisateur correspondant au login
     * @throws UserAccessForbiddenException erreur d'accès
     */
    Uni<Utilisateur> getUtilisateur(String idUtilisateur) throws DataNotFoundException;


    /**
     * Date de dernier accès
     * @param idUtilisateur login de l'utilisateur
     * @return date de dernier accès
     * @throws UserAccessForbiddenException erreur d'accès
     */
    Uni<LocalDateTime> getLastAccessDate(String idUtilisateur) throws UserAccessForbiddenException;
}
