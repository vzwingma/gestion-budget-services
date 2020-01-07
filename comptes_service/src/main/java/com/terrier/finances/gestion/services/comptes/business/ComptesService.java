package com.terrier.finances.gestion.services.comptes.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.business.AbstractBusinessService;
import com.terrier.finances.gestion.services.comptes.data.ComptesDatabaseService;


/**
 * Services de gestion des comptes
 * @author vzwingma
 *
 */
@Service
public class ComptesService extends AbstractBusinessService {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesService.class);
	/**
	 * Utilisateurs
	 */
	@Autowired
	private ComptesDatabaseService dataDBComptes;


	/**
	 * @param idCompte id du compte
	 * @return etat du compte
	 */
	public boolean isCompteActif(String idCompte){
		try {
			return dataDBComptes.isCompteActif(idCompte);
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
	public CompteBancaire getCompteById(String idCompte, String idUtilisateur) throws DataNotFoundException{
		CompteBancaire compte = dataDBComptes.chargeCompteParId(idCompte, idUtilisateur);
		if(compte != null){
			LOGGER.debug("[idUser={}][idCompte={}] Compte chargé (actif ? {})", idUtilisateur, idCompte, compte.isActif());
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
		List<CompteBancaire> comptes = dataDBComptes.chargeComptes(idUtilisateur);
		if(comptes != null){
			return comptes;
		}
		throw new DataNotFoundException("Aucun compte trouvé pour l'utilisateur " + idUtilisateur);
	}


	
	

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up().withDetail("Service", "Comptes");
	}
}
