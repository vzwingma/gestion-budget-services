package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.ihm.vue.budget.BudgetMoisFragment;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.AsyncTask;

/**
 * Tâche HTTP pour appel REST
 * Created by vzwingma on 27/12/2014.
 */
public class BudgetHTTPAsyncTask extends AsyncTask<BudgetMoisFragment, Void, BudgetMensuel> {

    // Logger
    private final Logger LOG = new Logger(BudgetHTTPAsyncTask.class);
    private BudgetMoisFragment fragment;

    @Override
    protected BudgetMensuel doInBackground(BudgetMoisFragment... fragments) {
        this.fragment = fragments[0];
        return FacadeServices.getInstance().getBusinessService().getBudget(fragment.getMois().toString(), fragment.getAnnee().toString(), fragment.getCompte());
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(BudgetMensuel budget) {
        LOG.info("Budget : " + budget);
        this.fragment.getControleur().miseAJourVue(budget);
    }
}