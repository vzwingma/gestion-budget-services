/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.terrier.finances.gestion.data.transformer.DataTransformerCategoriesDepense;
import com.terrier.finances.gestion.model.business.parametrage.CompteBancaire;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.data.parametrage.ContexteUtilisateurDTO;
import com.terrier.finances.gestion.model.exception.BudgetNotFoundException;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

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
	 * @param idCompte compte
	 * @param strMois mois en chaine
	 * @param strAnnee année en chaine
	 * @return budget correspondant
	 * @throws DataNotFoundException  erreur de connexion à la BDD
	 * @throws BudgetNotFoundException  erreur budget introuvable
	 */
	@RequestMapping(value="/budget/{idCompte}/{strMois}/{strAnnee}", method=RequestMethod.GET, produces = "application/json",headers="Accept=application/json")
	public BudgetMensuelDTO getBudget(@PathVariable String idCompte, 
			@PathVariable String strMois,
			@PathVariable String strAnnee) throws BudgetNotFoundException, DataNotFoundException{

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
		return businessDepenses.chargerBudgetMensuelConsultation(idCompte, mois, annee);
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
	public List<LigneDepenseDTO> getLignesDepenses(@PathVariable String idbudget) throws BudgetNotFoundException, DataNotFoundException{

		LOGGER.debug("Appel REST getLignesDepenses : idbudget={}", idbudget);
		return businessDepenses.chargerLignesDepensesConsultation(idbudget);
	}




	/**
	 * @param login login
	 * @param motpasse mot de passe
	 * @return contexte utilisateur
	 * @throws BudgetNotFoundException
	 * @throws DataNotFoundException
	 */
	@RequestMapping(value="/utilisateur/{login}/{motpasseHashed}", method=RequestMethod.GET, produces = "application/json",headers="Accept=application/json")
	public ContexteUtilisateurDTO getContexteUtilisateur(@PathVariable String login, @PathVariable String motpasseHashed, HttpServletResponse response){

		LOGGER.debug("Appel REST getContexteUtilisateur : login={}", login);
		ContexteUtilisateurDTO contexteUtilisateur = new ContexteUtilisateurDTO();
		Utilisateur utilisateur = businessAuthentication.getUtilisateur(login, motpasseHashed);
		if(utilisateur != null) {
			try{
			contexteUtilisateur.setUtilisateur(utilisateur);
			List<CompteBancaire> comptes = businessParams.getComptesUtilisateur(utilisateur);
			contexteUtilisateur.setComptes(comptes);

			contexteUtilisateur.setCategories(dataTransformerCategoriesDepense.transformBOstoDTOs(businessParams.getCategories(), null));

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
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		return null;
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
