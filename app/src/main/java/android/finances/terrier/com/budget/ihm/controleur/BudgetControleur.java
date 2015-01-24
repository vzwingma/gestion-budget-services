package android.finances.terrier.com.budget.ihm.controleur;

import android.content.Context;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivityControleur;
import android.finances.terrier.com.budget.ihm.vue.BudgetActivity;
import android.finances.terrier.com.budget.ihm.vue.budget.ResumeTotauxExpandableAdapter;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.utils.IHMViewUtils;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controleur de budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetControleur extends AbstractActivityControleur<BudgetActivity> {


    // Logger
    private final Logger LOG = new Logger(BudgetControleur.class);
    private final SimpleDateFormat auDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final SimpleDateFormat finDateFormat = new SimpleDateFormat("MMM yyyy");
    // vue associée
    private View rootView;
    private BudgetMensuel budget;

    /**
     * Démarrage du controleur
     */
    @Override
    public void startControleur() {

    }


    /**
     * Mise à jour : données REST reçues
     *
     * @param budgetMensuel budget associé
     */
    public void miseAJourVue(BudgetMensuel budgetMensuel) {
        this.budget = budgetMensuel;
        // Libellé
        Calendar dateBudget = budgetMensuel.getDateMiseAJour();
        ((TextView) rootView.findViewById(R.id.resume_total_now)).setText("Au " + auDateFormat.format(dateBudget.getTime()));
        ((TextView) rootView.findViewById(R.id.resume_total_now2)).setText("Au " + auDateFormat.format(dateBudget.getTime()));
        Calendar finBudget = Calendar.getInstance();
        finBudget.set(Calendar.MONTH, budgetMensuel.getMois());
        finBudget.set(Calendar.YEAR, budgetMensuel.getAnnee());
        ((TextView) rootView.findViewById(R.id.resume_total_fin_mois)).setText(" Fin " + finDateFormat.format(finBudget.getTime()));
        ((TextView) rootView.findViewById(R.id.resume_total_fin_mois2)).setText(" Fin " + finDateFormat.format(finBudget.getTime()));
        // Valeur
        IHMViewUtils.miseAJourTextViewValeurEuro(rootView, R.id.resume_total_fin_argent_avance, budgetMensuel.getFinArgentAvance());
        IHMViewUtils.miseAJourTextViewValeurEuro(rootView, R.id.resume_total_fin_argent_reel, budgetMensuel.getFinCompteReel());
        IHMViewUtils.miseAJourTextViewValeurEuro(rootView, R.id.resume_total_now_argent_avance, budgetMensuel.getNowArgentAvance());
        IHMViewUtils.miseAJourTextViewValeurEuro(rootView, R.id.resume_total_now_argent_reel, budgetMensuel.getNowCompteReel());


        // Ajout de la liste des catégories
        ExpandableListView expandableList = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        ResumeTotauxExpandableAdapter adapter = new ResumeTotauxExpandableAdapter(this.budget, this.getActivity(), (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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


    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public BudgetMensuel getBudget() {
        return budget;
    }
}
