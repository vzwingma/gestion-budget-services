package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.ihm.vue.BudgetActivity;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.AsyncTask;

/**
 * Tâche HTTP pour appel REST d'authentification
 * Created by vzwingma on 27/12/2014.
 */
class AuthenticationHTTPAsyncTask extends AsyncTask<Void, Void, Boolean> {

    // Logger
    private final Logger LOG = new Logger(AuthenticationHTTPAsyncTask.class);

    // Composant associé à l'asynctask
    private MainControleur controleur;

    /**
     * Tâche d'authentification
     *
     * @param controleur controleur
     */
    public AuthenticationHTTPAsyncTask(MainControleur controleur) {
        this.controleur = controleur;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return résultat d'authentification
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        return FacadeServices.getInstance().getBusinessService().authenticateToServeur();
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Boolean authenticate) {
        LOG.info("[REST] Réception du message : " + authenticate);
        if (authenticate) {
            this.controleur.startActivity(BudgetActivity.class);
        } else {
            this.controleur.showPopupNotification("Erreur d'authentification", 5);
        }
    }
}