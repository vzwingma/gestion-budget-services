package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.AsyncTask;

/**
 * Tâche HTTP pour appel REST
 * Created by vzwingma on 27/12/2014.
 */
public class BudgetHTTPAsyncTask extends AsyncTask<BudgetFragmentControleur, Void, BudgetMensuel> {

    // Logger
    private final Logger LOG = new Logger(BudgetHTTPAsyncTask.class);
    private BudgetFragmentControleur fragmentControleur;

    @Override
    protected BudgetMensuel doInBackground(BudgetFragmentControleur... fragmentControleur) {
        this.fragmentControleur = fragmentControleur[0];
        return FacadeServices.getInstance().getBusinessService().getBudget(
                this.fragmentControleur.getMois().toString(),
                this.fragmentControleur.getAnnee().toString(),
                this.fragmentControleur.getIdCompte());
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(BudgetMensuel budget) {
        LOG.info("Budget : " + budget);
        this.fragmentControleur.miseAJourResume(budget);
    }
}