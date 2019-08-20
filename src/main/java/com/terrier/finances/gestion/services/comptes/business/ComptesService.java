package com.terrier.finances.gestion.services.comptes.business;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.comptes.model.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.budget.data.BudgetDatabaseService;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
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
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesService.class);
	/**
	 * Utilisateurs
	 */
	@Autowired
	private UtilisateurDatabaseService dataDBUsers;

	/**
	 * Lien vers les données
	 */
	@Autowired
	private BudgetDatabaseService dataDepenses;



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
	public CompteBancaire getCompteById(String idCompte, String idUtilisateur) throws DataNotFoundException{
		CompteBancaire compte = dataDBUsers.chargeCompteParId(idCompte, idUtilisateur);
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
		List<CompteBancaire> comptes = dataDBUsers.chargeComptes(idUtilisateur);
		if(comptes != null){
			return comptes;
		}
		throw new DataNotFoundException("Aucun compte trouvé pour l'utilisateur " + idUtilisateur);
	}


	/**
	 * Charge la date du premier budget déclaré pour ce compte pour cet utilisateur
	 * @param utilisateur utilisateur
	 * @param idCompte id du compte
	 * @return la date du premier budget décrit pour cet utilisateur
	 */
	public LocalDate[] getIntervallesBudgets(String idCompte) throws DataNotFoundException{

		BudgetMensuelDTO[] premierDernierBudgets = this.dataDepenses.getPremierDernierBudgets(idCompte);
		if(premierDernierBudgets != null && premierDernierBudgets.length >= 2){


			LocalDate premier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
			if(premierDernierBudgets[0] != null){
				premier = premier.with(ChronoField.MONTH_OF_YEAR, premierDernierBudgets[0].getMois() + 1L).with(ChronoField.YEAR, premierDernierBudgets[0].getAnnee());
			}
			LocalDate dernier = BudgetDateTimeUtils.localDateFirstDayOfMonth();
			if(premierDernierBudgets[1] != null){
				dernier = dernier.with(ChronoField.MONTH_OF_YEAR, premierDernierBudgets[1].getMois() + 1L).with(ChronoField.YEAR, premierDernierBudgets[1].getAnnee()).plusMonths(1);
			}
			return new LocalDate[]{premier, dernier};
		}
		else{
			throw new DataNotFoundException("Données introuvables pour le compte " + idCompte);
		}
	}

	/**
	 * @param idUtilisateur
	 * @param idCompte
	 * @param annee
	 * @return liste des libelles opérations
	 */
	public Set<String> getLibellesOperations(String idUtilisateur, String idCompte, int annee){
		return this.dataDepenses.chargeLibellesOperations(idCompte, annee);
	}
}
