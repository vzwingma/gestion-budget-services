package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.ihm.vue.BudgetActivity;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.os.AsyncTask;

/**
 * Tâche HTTP pour appel REST d'authentification
 * Created by vzwingma on 27/12/2014.
 */
class AuthenticationHTTPAsyncTask extends AsyncTask<String, Void, Boolean> {

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
     * Appel d'authentification
     *
     * @param authentication données d'authentification
     * @return résultat d'authentification
     */
    @Override
    protected Boolean doInBackground(String... authentication) {
        return FacadeServices.getInstance().getBusinessService().authenticate(authentication[0], authentication[1]);
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