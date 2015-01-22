package android.finances.terrier.com.budget.ihm.vue.budget;

import android.content.Context;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.ihm.controleur.BudgetHTTPAsyncTask;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.models.LigneDepense;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment d'une page de budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetMoisFragment extends Fragment {

    // Logger
    private static final Logger LOG = new Logger(BudgetMoisFragment.class);
    private static final NumberFormat formatter = new DecimalFormat("#0.00");
    private final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy");
    // Informations liées au fragment
    private int mois;
    private int annee;
    private String idCompte;
    // vue associée
    private View rootView;
    private BudgetMensuel budget;

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
    public static BudgetMoisFragment newInstance(int mois, int annee, String idCompte) {
        BudgetMoisFragment fragment = new BudgetMoisFragment();
        LOG.info("Création du fragment " + mois + " " + annee + " de " + idCompte);
        fragment.setArguments(mois, annee, idCompte);
        return fragment;
    }


    /**
     * Ajout des arguments métier au fragment
     */
    public void setArguments(int mois, int annee, String idCompte) {
        this.mois = mois;
        this.annee = annee;
        this.idCompte = idCompte;
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
        // Déclenchement de l'appel REST si le budget n'est pas encore chargé ou s'il est actif
        if (this.budget == null || this.budget.isActif()) {
            new BudgetHTTPAsyncTask().execute(this);
        } else {
            miseAJourVue(budget);
        }
        return rootView;
    }


    /**
     * Mise à jour : données REST reçues
     *
     * @param budgetMensuel budget associé
     */
    public void miseAJourVue(BudgetMensuel budgetMensuel) {
        this.budget = budgetMensuel;
        // Libellé
        Calendar dateBudget = getMaxDate(budgetMensuel.getListeDepenses());
        ((TextView) rootView.findViewById(R.id.resume_total_now)).setText(auDateFormat.format(dateBudget.getTime()));
        ((TextView) rootView.findViewById(R.id.resume_total_fin_mois)).setText(" Fin " + finDateFormat.format(dateBudget.getTime()));
        // Valeur
        miseAJourTextViewValeurEuro(R.id.resume_total_fin_argent_avance, budgetMensuel.getFinArgentAvance());
        miseAJourTextViewValeurEuro(R.id.resume_total_fin_argent_reel, budgetMensuel.getFinCompteReel());
        miseAJourTextViewValeurEuro(R.id.resume_total_now_argent_avance, budgetMensuel.getNowArgentAvance());
        miseAJourTextViewValeurEuro(R.id.resume_total_now_argent_reel, budgetMensuel.getNowCompteReel());


        // Ajout de la liste des catégories
        ExpandableListView expandableList = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        ResumeTotauxExpandableAdapter adapter = new ResumeTotauxExpandableAdapter(this.budget, this.getActivity(), (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        expandableList.setAdapter(adapter);
    }

    /**
     * Mise à jour d'un textview avec une valeur en €
     *
     * @param id     id de l'élément
     * @param valeur valeur à afficher
     */
    private void miseAJourTextViewValeurEuro(int id, double valeur) {

        ((TextView) rootView.findViewById(id)).setText(formatter.format(valeur) + " €");
    }


    /**
     * @param listeDepenses
     * @return date max d'une liste de dépenses
     */
    private Calendar getMaxDate(List<LigneDepense> listeDepenses) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1999);
        if (listeDepenses != null) {
            for (LigneDepense ligneDepense : listeDepenses) {
                if (ligneDepense.getDateOperation() != null && c.getTime().before(ligneDepense.getDateOperation())) {
                    c.setTime(ligneDepense.getDateOperation());
                }
            }
        }
        return c;
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
