package android.finances.terrier.com.budget.ihm.vue.budget;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.ihm.controleur.BudgetControleur;
import android.finances.terrier.com.budget.ihm.controleur.BudgetHTTPAsyncTask;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment d'une page de budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetMoisFragment extends Fragment {

    // Logger
    private static final Logger LOG = new Logger(BudgetMoisFragment.class);


    // Informations liées au fragment
    private int mois;
    private int annee;
    private String idCompte;
    private BudgetControleur controleur;

    /**
     * Constructeur public
     */
    public BudgetMoisFragment() {
    }

    /**
     * Création d'une nouvelle page
     *
     * @param mois     mois de la page
     * @param annee    année de la page
     * @param idCompte compte
     * @return fragment à afficher
     */
    public static BudgetMoisFragment newInstance(BudgetControleur controleur, int mois, int annee, String idCompte) {
        BudgetMoisFragment fragment = new BudgetMoisFragment();
        LOG.info("Création du fragment " + mois + " " + annee + " de " + idCompte);
        fragment.setArguments(controleur, mois, annee, idCompte);
        return fragment;
    }


    /**
     * Ajout des arguments métier au fragment
     */
    public void setArguments(BudgetControleur controleur, int mois, int annee, String idCompte) {
        this.mois = mois;
        this.annee = annee;
        this.idCompte = idCompte;
        this.controleur = controleur;
    }

    /**
     * Création de la vue
     *
     * @param inflater           inflater
     * @param container          conteneur des views
     * @param savedInstanceState état de l'instance
     * @return vue
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);
        getControleur().setRootView(rootView);
        // Déclenchement de l'appel REST si le budget n'est pas encore chargé ou s'il est actif
        if (this.controleur.getBudget() == null || this.controleur.getBudget().isActif()) {
            new BudgetHTTPAsyncTask().execute(this);
        } else {
            this.controleur.miseAJourVue(this.controleur.getBudget());
        }
        return rootView;
    }


    public BudgetControleur getControleur() {
        return controleur;
    }

    public Integer getMois() {
        return mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public String getCompte() {
        return idCompte;
    }
}
