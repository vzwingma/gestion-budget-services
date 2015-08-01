package android.finances.terrier.com.budget.services;

import android.finances.terrier.com.budget.abstrait.AbstractRESTService;
import android.finances.terrier.com.budget.models.data.BudgetMensuelDTO;
import android.finances.terrier.com.budget.models.data.CategorieDepenseDTO;
import android.finances.terrier.com.budget.models.data.ContexteUtilisateurDTO;
import android.finances.terrier.com.budget.models.data.LigneDepenseDTO;

import java.util.List;

/**
 * Service REST budget
 * Created by vzwingma on 31/12/2014.
 */
public class InterfaceRESTService extends AbstractRESTService {

    // Logger
    //private static final Logger LOG = new Logger(InterfaceRESTService.class);


    /**
     * chargement des localisations cons depuis le serveur
     *
     * @return liste de localisations cons
     */
    public BudgetMensuelDTO getBudget(String mois, String annee, String compte) {

        // The URL for making the GET request
        final String url = "/budget/" + compte + "/" + mois + "/" + annee;
        return GET(url, BudgetMensuelDTO.class);
    }


    /**
     * @param idBudget id budget
     * @return liste des dépenses du bucget
     */
    @SuppressWarnings("unchecked")
    public List<LigneDepenseDTO> ligneDepenses(String idBudget) {
        final String url = "/depenses/" + idBudget;
        return GET(url, List.class);
    }


    /**
     * @return Contexte utilisateur
     */
    public ContexteUtilisateurDTO getContexteUtilisateur() {
        final String url = "/utilisateur";
        return GET(url, ContexteUtilisateurDTO.class);
    }


    /**
     * Liste des catégories
     *
     * @return liste des catégories
     */
    public List<CategorieDepenseDTO> getCategoriesDepensesDTO() {
        final String url = "/categories/depenses";
        return GET(url, List.class);
    }

}