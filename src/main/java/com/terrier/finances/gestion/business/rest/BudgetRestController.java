/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.business.rest.auth.RestSession;
import com.terrier.finances.gestion.business.rest.auth.RestSessionManager;
import com.terrier.finances.gestion.data.transformer.DataTransformerCategoriesDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.data.parametrage.CategorieDepenseDTO;
import com.terrier.finances.gestion.model.data.parametrage.ContexteUtilisateurDTO;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;
import com.terrier.finances.gestion.model.exception.UserNotAuthorizedException;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value="/rest")
public class BudgetRestController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetRestController.class);


	/**
	 * Lien vers les services métier
	 */
	@Autowired
	private BusinessDepensesService businessDepenses;
	/**
	 * Lien vers les services métier
	 */
	@Autowired
	private ParametragesService businessParams;
	/**
	 * Lien vers les services métier
	 */
	@Autowired
	private AuthenticationService businessAuthentication;

	@Autowired @Qualifier("dataTransformerCategoriesDepense")
	private DataTransformerCategoriesDepense dataTransformerCategoriesDepense;



	/**
	 * @return contexte catégories
	 * @throws UserNotAuthorizedException utilisateur non trouvé
	 * @throws DataNotFoundException données non trouvées
	 */
	@RequestMapping(value="/categories/depenses", 
			method=RequestMethod.GET, produces = "application/json")
	public List<CategorieDepenseDTO> getCategoriesDepenses(
			HttpServletRequest request) throws DataNotFoundException, UserNotAuthorizedException{

		RestSession restSession = RestSessionManager.getSession(request.getHeader("Authorization"));
		LOGGER.debug("[REST][{}] Appel REST GetCategoriesDepenses", restSession);
		try{
			return dataTransformerCategoriesDepense.transformBOstoDTOs(businessParams.getCategories(), null);
		}
		catch(Exception e){
			throw new DataNotFoundException(e.getMessage());
		}
	}

	/**
	 * @param login login
	 * @param motpasse mot de passe
	 * @return contexte utilisateur
	 * @throws UserNotAuthorizedException utilisateur non trouvé
	 * @throws DataNotFoundException données non trouvées
	 */
	@RequestMapping(value="/utilisateur", 
			method=RequestMethod.GET, produces = "application/json")
	public ContexteUtilisateurDTO getContexteUtilisateur(HttpServletRequest request) throws DataNotFoundException, UserNotAuthorizedException{

		RestSession restSession = RestSessionManager.getSession(request.getHeader("Authorization"));
		LOGGER.debug("[REST][{}] Appel REST getContexteUtilisateur", restSession);

		ContexteUtilisateurDTO contexteUtilisateur = new ContexteUtilisateurDTO();
		Utilisateur utilisateur = restSession.getUtilisateur();
		if(utilisateur != null) {
			try{
				contexteUtilisateur.setUtilisateur(utilisateur);
				List<CompteBancaire> comptes = businessParams.getComptesUtilisateur(utilisateur);
				contexteUtilisateur.setComptes(comptes);

				for (CompteBancaire compteBancaire : comptes) {
					List<BudgetMensuelDTO> listeBudget = businessDepenses.chargerBudgetsMensuelsConsultation(utilisateur, compteBancaire.getId());
					LOGGER.debug(" {} budget chargé pour {}", listeBudget != null ? listeBudget.size() : 0, compteBancaire.getLibelle());
					Calendar minBudget = null;
					Calendar maxBudget = null;
					// Calcul des min/max pour le compte
					for (BudgetMensuelDTO budgetDTO : listeBudget) {
						Calendar dateBudget = Calendar.getInstance();
						dateBudget.set(Calendar.DAY_OF_MONTH, 1);
						dateBudget.set(Calendar.MONTH, budgetDTO.getMois());
						dateBudget.set(Calendar.YEAR, budgetDTO.getAnnee());

						if(minBudget == null || dateBudget.before(minBudget)){
							minBudget = dateBudget;
						}
						if(maxBudget == null || dateBudget.after(maxBudget)){
							maxBudget = dateBudget;
						}
					}
					contexteUtilisateur.setIntervalleCompte(compteBancaire, minBudget, maxBudget);
				}
				return contexteUtilisateur;
			}
			catch(Exception e){
				throw new DataNotFoundException(e.getMessage());
			}
		}
		else{
			LOGGER.error("L'utilisateur n'a pas le droit d'utiliser cette ressource ");
			throw new UserNotAuthorizedException();
		}
	}

	/**
	 * @param idCompte compte
	 * @param strMois mois en chaine
	 * @param strAnnee année en chaine
	 * @return budget correspondant
	 * @throws DataNotFoundException  erreur de connexion à la BDD
	 * @throws BudgetNotFoundException  erreur budget introuvable
	 */
	@RequestMapping(value="/budget/{idCompte}/{strMois}/{strAnnee}", method=RequestMethod.GET, produces = "application/json",headers="Accept=application/json")
	public BudgetMensuelDTO getBudget(
			@PathVariable String idCompte, 
			@PathVariable String strMois,
			@PathVariable String strAnnee, 
			HttpServletRequest request) throws BudgetNotFoundException, DataNotFoundException, UserNotAuthorizedException{

		RestSession restSession = RestSessionManager.getSession(request.getHeader("Authorization"));
		LOGGER.debug("[REST][{}] Appel REST getContexteUtilisateur", restSession);

		LOGGER.debug("Appel REST getBudget : compte : {}, période : {}", idCompte, strMois, strAnnee);
		int mois = Calendar.getInstance().get(Calendar.MONTH);
		try{
			mois = Integer.parseInt(strMois);
		}
		catch(NumberFormatException e){
			LOGGER.error("Erreur dans le mois reçu : utilisation de la valeur courante {}", mois, e);
		}
		int annee = Calendar.getInstance().get(Calendar.YEAR);
		try{
			annee = Integer.parseInt(strAnnee);
		}
		catch(NumberFormatException e){
			LOGGER.error("Erreur dans l'année reçu : utilisation de la valeur courante {}", annee, e);
		}
		BudgetMensuelDTO budget = businessDepenses.chargerBudgetMensuelConsultation(idCompte, mois, annee);

		// Vérification que le buget est lisible par l'utilisateur
		for(Utilisateur proprietaire : budget.getCompteBancaire().getListeProprietaires()){
			if(proprietaire.getId().equals(restSession.getUtilisateur().getId())){
				return budget;
			}
		}
		return null;
	}



	/**
	 * @param idCompte compte
	 * @param strMois mois en chaine
	 * @param strAnnee année en chaine
	 * @return budget correspondant
	 * @throws DataNotFoundException  erreur de connexion à la BDD
	 * @throws BudgetNotFoundException  erreur budget introuvable
	 */
	@RequestMapping(value="/depenses/{idbudget}", method=RequestMethod.GET, produces = "application/json",headers="Accept=application/json")
	public List<LigneDepenseDTO> getLignesDepenses(@PathVariable String idbudget, HttpServletRequest request) 
			throws UserNotAuthorizedException, DataNotFoundException{

		RestSession restSession = RestSessionManager.getSession(request.getHeader("Authorization"));
		LOGGER.debug("[REST][{}] Appel REST getLignesDepenses", restSession);

		LOGGER.debug("Appel REST getLignesDepenses : idbudget={}", idbudget);
		return businessDepenses.chargerLignesDepensesConsultation(idbudget);
	}



	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping(){
		LOGGER.info("Appel ping du service");
		return "L'application est démarrée";
	}
}
