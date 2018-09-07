package com.terrier.finances.gestion.services.comptes.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.utilisateurs.data.UtilisateurDatabaseService;


/**
 * Services de gestion des comptes
 * @author vzwingma
 *
 */
@Service
public class ComptesService extends AbstractBusinessService {


	/**
	 * Utilisateurs
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBUsers;
	


	/**
	 * @param idCompte id du compte
	 * @return etat du compte
	 */
	public boolean isCompteActif(String idCompte){
		try {
			return dataDBUsers.isCompteActif(idCompte);
		} catch (DataNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Recherche du compte par id
	 * @param idCompte id du compte
	 * @param utilisateur utilisateur
	 * @return compteBancaire
	 * @throws DataNotFoundException
	 */
	public CompteBancaire getCompteById(String idCompte, String proprietaire) throws DataNotFoundException{
		CompteBancaire compte = dataDBUsers.chargeCompteParId(idCompte, proprietaire);
		if(compte != null){
			return compte;
		}
		throw new DataNotFoundException("Aucun compte "+idCompte+ " trouvé");
	}



	/**
	 * Recherche des comptes d'un utilisateur
	 * @param utilisateur utilisateur
	 * @return liste des comptes bancaires
	 * @throws DataNotFoundException
	 */
	public List<CompteBancaire> getComptesUtilisateur(String idUtilisateur) throws DataNotFoundException{
		List<CompteBancaire> comptes = dataDBUsers.chargeComptes(idUtilisateur);
		if(comptes != null){
			return comptes;
		}
		throw new DataNotFoundException("Aucun compte trouvé pour l'utilisateur " + idUtilisateur);
	}
}
