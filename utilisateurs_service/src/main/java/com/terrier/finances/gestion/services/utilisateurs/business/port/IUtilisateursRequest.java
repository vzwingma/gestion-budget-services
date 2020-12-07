package com.terrier.finances.gestion.services.utilisateurs.business.port;

import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.utilisateurs.business.model.v12.Utilisateur;

import java.time.LocalDateTime;

/**
 * Port de l'Application Provider Interface des Utilisateurs
 */
public interface IUtilisateursRequest {

    /**
     * Chargement d'un utilisateur
     * @param idUtilisateur login de l'utilisateur
     * @return Utilisateur correspondant au login
     * @throws DataNotFoundException erreur d'accès
     */
    public Utilisateur getUtilisateur(String idUtilisateur) throws DataNotFoundException;


    /**
     * Date de dernier accès
     * @param idUtilisateur login de l'utilisateur
     * @return date de dernier accès
     * @throws DataNotFoundException erreur d'accès
     */
    public LocalDateTime getLastAccessDate(String idUtilisateur) throws DataNotFoundException;
}
