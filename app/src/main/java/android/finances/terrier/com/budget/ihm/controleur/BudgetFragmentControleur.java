package android.finances.terrier.com.budget.ihm.controleur;

import android.content.Context;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractFragmentControleur;
import android.finances.terrier.com.budget.ihm.vue.budget.BudgetMoisFragment;
import android.finances.terrier.com.budget.ihm.vue.budget.ResumeTotauxExpandableAdapter;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.utils.IHMViewUtils;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controleur d'un budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetFragmentControleur extends AbstractFragmentControleur<BudgetMoisFragment> {


    // Logger
    private final Logger LOG = new Logger(BudgetFragmentControleur.class);
    private final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy");

    private BudgetMensuel budget;

    // Informations liées au fragment
    private int mois;
    private int annee;
    private String idCompte;


    public BudgetFragmentControleur(int mois, int annee, String idCompte) {
        this.mois = mois;
        this.annee = annee;
        this.idCompte = idCompte;
    }

    /**
     * Démarrage du controleur
     */
    @Override
    public void startControleur() {

    }


    /**
     * Mise à jour à partir des données déjà reçues
     */
    public void miseAJourVue() {
        miseAJourResume(this.budget);
    }

    /**
     * Mise à jour : données REST reçues
     *
     * @param budgetMensuel budget associé
     */
    public void miseAJourResume(BudgetMensuel budgetMensuel) {
        this.budget = budgetMensuel;
        // Libellé
        Calendar dateBudget = budgetMensuel.getDateMiseAJour();
        ((TextView) getElementFromView(R.id.resume_total_now)).setText("Au " + auDateFormat.format(dateBudget.getTime()));
        ((TextView) getElementFromView(R.id.resume_total_now2)).setText("Au " + auDateFormat.format(dateBudget.getTime()));
        Calendar finBudget = Calendar.getInstance();
        finBudget.set(Calendar.MONTH, budgetMensuel.getMois());
        finBudget.set(Calendar.YEAR, budgetMensuel.getAnnee());
        ((TextView) getElementFromView(R.id.resume_total_fin_mois)).setText(" Fin " + finDateFormat.format(finBudget.getTime()));
        ((TextView) getElementFromView(R.id.resume_total_fin_mois2)).setText(" Fin " + finDateFormat.format(finBudget.getTime()));
        // Valeur
        IHMViewUtils.miseAJourTextViewValeurEuro(getElementFromView(R.id.resume_total_fin_argent_avance), budgetMensuel.getFinArgentAvance());
        IHMViewUtils.miseAJourTextViewValeurEuro(getElementFromView(R.id.resume_total_fin_argent_reel), budgetMensuel.getFinCompteReel());
        IHMViewUtils.miseAJourTextViewValeurEuro(getElementFromView(R.id.resume_total_now_argent_avance), budgetMensuel.getNowArgentAvance());
        IHMViewUtils.miseAJourTextViewValeurEuro(getElementFromView(R.id.resume_total_now_argent_reel), budgetMensuel.getNowCompteReel());


        // Ajout de la liste des catégories
        ExpandableListView expandableList = (ExpandableListView) getElementFromView(R.id.expandableListView);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        ResumeTotauxExpandableAdapter adapter = new ResumeTotauxExpandableAdapter(this.budget, (LayoutInflater) this.getFragment().getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        expandableList.setAdapter(adapter);
    }

    /**
     * Arrét du controleur
     */
    @Override
    public void stopControleur() {

    }

    /**
     * Menu Item select
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            default:
                LOG.info("onMenuItemSelected : " + item.getItemId());
                return super.onMenuItemSelected(featureId, item);
        }
    }

    /**
     * Inject RootView du fragment
     */
    public void initViewCompte() {
        // Déclenchement de l'appel REST si le budget n'est pas encore chargé ou s'il est actif
        if (this.budget == null || this.budget.isActif()) {
            new BudgetHTTPAsyncTask().execute(this);
        } else {
            miseAJourVue();
        }
        Spinner spinner = (Spinner) getElementFromView(R.id.selectCompte);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getFragment().getActivity(),
                R.array.comptes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public Integer getMois() {
        return mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public String getIdCompte() {
        return idCompte;
    }
}
