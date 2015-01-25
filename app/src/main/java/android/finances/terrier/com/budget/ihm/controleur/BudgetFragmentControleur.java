package android.finances.terrier.com.budget.ihm.controleur;

import android.content.Context;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractFragmentControleur;
import android.finances.terrier.com.budget.ihm.listener.ListeCompteSelectedListener;
import android.finances.terrier.com.budget.ihm.vue.budget.BudgetMoisFragment;
import android.finances.terrier.com.budget.ihm.vue.budget.ResumeTotauxExpandableAdapter;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.models.CompteBancaire;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.IHMViewUtils;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Controleur d'un budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetFragmentControleur extends AbstractFragmentControleur<BudgetMoisFragment> {


    // Logger
    private final Logger LOG = new Logger(BudgetFragmentControleur.class);
    private final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy");
    private final SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM yyyy");

    private BudgetMensuel budget;

    // Informations liées au fragment
    private int mois;
    private int annee;
    private String idCompte;
    private Date dateBudget;


    public BudgetFragmentControleur(int mois, int annee) {
        this.mois = mois;
        this.annee = annee;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, getMois());
        c.set(Calendar.YEAR, getAnnee());
        this.dateBudget = c.getTime();
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
        // Déclenchement de l'appel REST si le budget n'est pas encore chargé ou s'il est actif
        if (this.budget == null || this.budget.isActif()) {
            new BudgetHTTPAsyncTask().execute(this);
        } else {
            miseAJourResume(this.budget);
        }
    }

    /**
     * Mise à jour : données REST reçues
     *
     * @param budgetMensuel budget associé
     */
    public void miseAJourResume(BudgetMensuel budgetMensuel) {
        this.budget = budgetMensuel;
        // Libellé
        Calendar dateBudget = budgetMensuel.getDateMiseAJour() != null ? budgetMensuel.getDateMiseAJour() : Calendar.getInstance();
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

        ResumeTotauxExpandableAdapter adapter = new ResumeTotauxExpandableAdapter(
                this.budget,
                (LayoutInflater) this.getFragment().getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        expandableList.setAdapter(adapter);
    }

    /**
     * Arrét du controleur
     */
    @Override
    public void stopControleur() {
    }

    /**
     * Inject RootView du fragment
     */
    public void initViewCompte() {

        // Mise à jour mois
        ((TextView) getElementFromView(R.id.budget_moisText)).setText("de " + fullDateFormat.format(this.dateBudget.getTime()));

        // Mise à jour spinner Compte
        final Spinner spinner = (Spinner) getElementFromView(R.id.budget_selectCompte);
        final ArrayAdapter<CompteBancaire> adapter = new ArrayAdapter<CompteBancaire>(
                this.getFragment().getActivity(),
                R.layout.spinnercomptetemplate,
                FacadeServices.getInstance().getBusinessService().getContexte().getComptes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new ListeCompteSelectedListener(this));
        // Specify the layout to use when the list of choices appears
        this.getFragment().getRootView().post(new Runnable() {
            @Override
            public void run() {
                CompteBancaire compteEnCours = null;
                for (CompteBancaire compte : getBusinessService().getContexte().getComptes()) {
                    if (compte.getId().equals(getIdCompte())) {
                        compteEnCours = compte;
                        break;
                    }
                }
                if (compteEnCours != null) {
                    spinner.setSelection(adapter.getPosition(compteEnCours));
                }
            }
        });


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

    public void setIdCompte(String idCompte) {
        this.budget = null;
        this.idCompte = idCompte;
    }
}
