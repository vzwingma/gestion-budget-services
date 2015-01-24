package android.finances.terrier.com.budget.ihm.vue.budget;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.ihm.controleur.BudgetFragmentControleur;
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

    private BudgetFragmentControleur controleur;
    // vue associée
    private View rootView;

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
     * @return fragment à afficher
     */
    public static BudgetMoisFragment newInstance(int mois, int annee) {
        BudgetMoisFragment fragment = new BudgetMoisFragment();
        fragment.initControleur(mois, annee);
        return fragment;
    }

    /**
     * Constructeur public
     */
    public void initControleur(int mois, int annee) {
        this.controleur = new BudgetFragmentControleur(mois, annee);
        this.controleur.setFragment(this);
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
        this.rootView = inflater.inflate(R.layout.fragment_budget, container, false);
        getControleur().initViewCompte();
        return rootView;
    }

    /**
     * @return rootview
     */
    public View getRootView() {
        return rootView;
    }

    public BudgetFragmentControleur getControleur() {
        return controleur;
    }
}
